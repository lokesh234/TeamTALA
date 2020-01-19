import sqlalchemy
from sqlalchemy.sql import text
from werkzeug.security import generate_password_hash, check_password_hash
from datetime import datetime, timedelta
import secrets

class DatabaseConnection():

    def __init__(self):
        # self.engine = sqlalchemy.create_engine('mysql+pymysql://root:FxNl7O9xp0CduDHH@35.193.223.192:3306/default')

        #     # ... Specify additional properties here.
        #     # ...
        # )
        self.engine = sqlalchemy.create_engine(
            # Equivalent URL:
            # mysql+pymysql://<db_user>:<db_pass>@/<db_name>?unix_socket=/cloudsql/<cloud_sql_instance_name>
            sqlalchemy.engine.url.URL(
                drivername="mysql+pymysql",
                username="root",
                password="FxNl7O9xp0CduDHH",
                database="default",
                query={"unix_socket": "/cloudsql/{}".format("framefinder:us-central1:framefinder2")},
            ),
        # ... Specify additional properties here.
        # ...
        )
        # self.engine = sqlalchemy.create_engine('mysql+pymysql://root:FxNl7O9xp0CduDHH@?unix_socket=/cloudsql/framefinder:us-central1:framefinder2/default'

        #     # ... Specify additional properties here.
        #     # ...
        # )
        
        print("Maybe Connected?")
        print(self.engine.connect())

  
    def getUser(self, token):
        current_id = self._verifyRequest(token)
        
        query = "SELECT id, username, email, lat, lon FROM users WHERE users.id = '{0}'".format(current_id)

        c = self.engine.connect()

     
        return [v.strip() if type(v) is str else v for v in c.execute(query).first()]

    def loginUser(self, username, password):
        ## Return Token for future logins
        ## Search for user with matching username and matched hashed password
        query = "SELECT password FROM users WHERE users.username = '{0}'".format(username)

        c = self.engine.connect()
        query = c.execute(query)
        
        next = query.first()
        c.close()
        print("Inside UserLogin After first Query")
        if next is not None:
            isValidPass = check_password_hash(next[0].strip(), password)
            token = secrets.token_hex()

            now_plus_24 = datetime.now() + timedelta(hours=24)
            print(now_plus_24)
            

            query = "UPDATE users SET token = '{0}', token_expiration = '{1}' where users.username = '{2}'".format(token, str(now_plus_24), username)

            c = self.engine.connect()
            c.execute(query)

            #self.conn.commit()
            c.close()

            return token
            

        return False

        ## return Token if success
        ## return False if not

    def createUser(self, username, password):
        ## Verify username does not already exist
        query = "SELECT username FROM users WHERE users.username = '{0}'".format(username)

        c = self.engine.connect()
        

        next = c.execute(query).first()
        print(next)
        if next is not None:
            ## Return false if username exists
            return False
        else:
            ## Create user with hashed password
            c = self.engine.connect()

            hashed = generate_password_hash(password)
            query = "INSERT INTO users (username, password) VALUES ('{0}', '{1}')".format(username, hashed)
            
            c.execute(query)
            c.close()

            ## Return True if success
            return True

    def _verifyRequest(self, token):
        ## Select user with token, if any
        if not token:
            return False

        query = "SELECT id, token_expiration FROM users WHERE users.token = '{0}'".format(token)

        ## Verify token is not expired
        c = self.engine.connect()
        now = datetime.now()

        
        next = c.execute(query).first()
        ## Return True if valid and not expired, else return False
        c.close()
        if next is not None:
            expiration_time = next[1]
            if now < expiration_time:
                return next[0]
        
        return False

    def userLoc(self, token, lat, lon):
        ## Verify request
        current_id = self._verifyRequest(token)
        if not current_id:
            return False
        ## Return False if not valid

        ## Save Lat Lon to user
        query = "UPDATE users SET lat = {0}, lon = {1} WHERE users.token = '{2}'".format(lat, lon, token)

        c = self.engine.connect()
        c.execute(query)
        #self.conn.commit()
        c.close()

        return True

    def trackUser(self, token, id):
        ## Verify request
        current_id = self._verifyRequest(token)
        if not current_id:
            return False

        ## Verify track exists
        
        query = "SELECT * FROM track WHERE track.tracker = {0} AND track.tracked = {1}".format(current_id, id)

        c = self.engine.connect()
        
        if c.execute(query).first()is not None:
            ## Get Lat Lon
            query = "SELECT lat, lon FROM users WHERE users.id = {0}".format(id)

            return c.execute(query).first()
        ## Return Lat Lon.

        return False

    def allowTrack(self, token, id):
        current_id = self._verifyRequest(token)
        if not current_id:
            return False

        ## verify request
       

        query = "SELECT id FROM trackrequest WHERE trackrequest.requester = {0} AND trackrequest.requested = {1}".format(id, current_id)

        c = self.engine.connect()

        next = c.execute(query).first()

        c.close()

        if next is not None:
            trackrequest_id = next[0]
            # Insert Track
            query = "INSERT INTO track (tracker, tracked) VALUES ({0}, {1})".format(id, current_id)

            c = self.engine.connect()
            c.execute(query)

            # Delete Old Track
            query = "DELETE FROM trackrequest WHERE trackrequest.id = {0}".format(trackrequest_id)
            c.execute(query)

            #self.conn.commit()
            return True
        ## verify track does not already exist
        ## return false if already exists
        ## Create track

        ## return true
        return False

    def requestTrack(self, token, username):
        current_id = self._verifyRequest(token)
        if not current_id:
            return False

        c = self.engine.connect()

        ## Verify Username exists
        query = "SELECT id FROM users WHERE users.id = '{0}'".format(username)

        other = c.execute(query).first()

        if other is None:
            return False

        id = other[0]
        
        query = "SELECT * FROM trackrequest WHERE trackrequest.requester = {0} AND trackrequest.requested = {1}".format(current_id, id)


        isTrackRequest = c.execute(query).first()

        query = "SELECT * FROM track WHERE track.tracker = {0} AND track.tracked = {1}".format(current_id, id)

        isTrack = c.execute(query).first()
        ## Request Already Exists
        if isTrackRequest or isTrack:
            return False

        query = "INSERT INTO trackrequest (requester, requested) VALUES ({0}, {1})".format(current_id, id)

        c.execute(query)
        #self.conn.commit()
        c.close()
        return True
        ## token is requester, 
        ## id is requested

    def removeTrackRequest(self, token, id):
        ## verify request
        current_id = self._verifyRequest(token)
        if not current_id:
            return False
        ## remove and return true
        query = "DELETE FROM trackrequest WHERE trackrequest.requester = {0} AND trackrequest.requested = {1}".format(id, current_id)

        c = self.engine.connect()
        c.execute(query)
        #self.conn.commit()
        c.close()

        ## if track does not exist return false.
        return True

    def removeTrack(self, token, id):
        ## verify request
        current_id = self._verifyRequest(token)
        if not current_id:
            return False
        ## remove and return true
        query = "DELETE FROM track WHERE track.tracker = {0} AND track.tracked = {1}".format(id, current_id)

        c = self.engine.connect()
        c.execute(query)
        #self.conn.commit()
        c.close()

        ## if track does not exist return false.
        return True
    
    def getFriendRequests(self, token):
        ## Verify Request
        current_id = self._verifyRequest(token)
        if not current_id:
            return False
            
        ## retrieve friend_requests objects.
        query = "SELECT username FROM users WHERE users.id IN (SELECT requester FROM trackrequest WHERE trackrequest.requested = {0})".format(current_id)
        # query = "SELECT requester FROM trackrequest WHERE trackrequest.requested = {0}".format(current_id)

        ## return list of request id's
        c = self.engine.connect()
        

        requests = [r for r in c.execute(query)]
        c.close()

        return requests

    def getTrackList(self, token):
        current_id = self._verifyRequest(token)
        if not current_id:
            return False

        # query = "SELECT tracked FROM track WHERE track.tracker = {0}".format(current_id)

        query = "SELECT username FROM users WHERE users.id IN (SELECT tracked FROM track where track.tracker = {0})".format(current_id)

        c = self.engine.connect()


        tracked = [v.strip() if type(v) is str else v for v in c.execute(query)]
        c.close()

        return tracked

if __name__ == "__main__":
    db = DatabaseConnection()

    db.createUser("ajwurts", "password")
    db.createUser("user2", "pword")

    ajwurts_token = db.loginUser("ajwurts", "password")
    user2_token = db.loginUser("user2", "pword")

    ajwurts_user = db.getUser(ajwurts_token)
    user2_user = db.getUser(user2_token)

    # print(_verifyRequest("alpba"))
    print("Ajwurts Set Lat Lon:", db.userLoc(ajwurts_token, 10, 20))
    print("User2 Set Lat Lon:", db.userLoc(user2_token, 20, 30))

    # Create Track Request
    print("Track Request ajwurts request user2", db.requestTrack(ajwurts_token, user2_user[0] ))
    print("Track Request user2 request ajwurts", db.requestTrack(user2_token, ajwurts_user[0]))

    # Get Track Requests
    print("Get Track Requests Ajwurts", db.getFriendRequests(ajwurts_token))

    print("Approve Track Request user2 approve ajwurts", db.allowTrack(user2_token, ajwurts_user[0]))
    print("Approve Track Request ajwurts approve user2", db.allowTrack(ajwurts_token, user2_user[0]))

    print("Remove Track ajwurts remove user2 permission", db.removeTrack(ajwurts_token, user2_user[0]))

    print("Track User2 from ajwurts",  db.trackUser(ajwurts_token, user2_user[0]))

    print("Friend List ajwurts", db.getTrackList(ajwurts_token))