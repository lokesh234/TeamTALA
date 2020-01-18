import cx_Oracle
from werkzeug.security import generate_password_hash, check_password_hash
from datetime import datetime, timedelta
import secrets

class DatabaseConnection():

    def __init__(self):

        dsn_tns = cx_Oracle.makedsn('oracle.wpi.edu', '1521','ORCL') # if needed, place an 'r' before any parameter in order to address special characters such as '\'.
        self.conn = cx_Oracle.connect(user=r'ajwurts', password ='AJWURTS', dsn=dsn_tns) # if needed, place an 'r' before any parameter in order to address special characters such as '\'. For example, if your user name contains '\', you'll need to place 'r' before the user name: user=r'User Name'
  
    def getUser(self, token):
        current_id = self._verifyRequest(token)
        
        query = "SELECT id, username, email, lat, lon FROM users WHERE users.id = '{0}'".format(current_id)

        c = self.conn.cursor()

        c.execute(query)

        return [v.strip() if type(v) is str else v for v in c.fetchone()]

    def loginUser(self, username, password):
        ## Return Token for future logins
        ## Search for user with matching username and matched hashed password
        query = "SELECT password FROM users WHERE users.username = '{0}'".format(username)

        c = self.conn.cursor()
        c.execute(query)


        next = c.fetchone()
        c.close()
        if next is not None:
            isValidPass = check_password_hash(next[0].strip(), password)
            token = secrets.token_hex()

            now_plus_24 = datetime.now() + timedelta(hours=24)
            print(now_plus_24)
            

            query = "UPDATE users SET token = '{0}', token_expiration = TO_TIMESTAMP('{1}', 'YYYY-MM-DD HH24:MI:SS.FF') where users.username = '{2}'".format(token, str(now_plus_24), username)

            c = self.conn.cursor()
            c.execute(query)

            self.conn.commit()

            return token
            

        return False

        ## return Token if success
        ## return False if not

    def createUser(self, username, password):
        ## Verify username does not already exist
        query = "SELECT username FROM users WHERE users.username = '{0}'".format(username)

        c = self.conn.cursor()
        c.execute(query)

        next = c.fetchone()
        print(next)
        if next is not None:
            ## Return false if username exists
            return False
        else:
            ## Create user with hashed password
            c = self.conn.cursor()

            hashed = generate_password_hash(password)
            query = "INSERT INTO users (username, password) VALUES ('{0}', '{1}')".format(username, hashed)
            
            c.execute(query)
            self.conn.commit()
            c.close()

            ## Return True if success
            return True

    def _verifyRequest(self, token):
        ## Select user with token, if any

        query = "SELECT id, token_expiration FROM users WHERE users.token = '{0}'".format(token)

        ## Verify token is not expired
        c = self.conn.cursor()
        now = datetime.now()

        c.execute(query)
        next = c.fetchone()
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

        c = self.conn.cursor()
        c.execute(query)
        self.conn.commit()
        c.close()

        return True

    def trackUser(self, token, id):
        ## Verify request
        current_id = self._verifyRequest(token)
        if not current_id:
            return False

        ## Verify track exists
        
        query = "SELECT * FROM track WHERE track.tracker = {0} AND track.tracked = {1}".format(current_id, id)

        c = self.conn.cursor()
        c.execute(query)
        if c.fetchone() is not None:
            ## Get Lat Lon
            query = "SELECT lat, lon FROM users WHERE users.id = {0}".format(id)
            c.execute(query)

            return c.fetchone()
        ## Return Lat Lon.

        return False

    def allowTrack(self, token, id):
        current_id = self._verifyRequest(token)
        if not current_id:
            return False

        ## verify request
       

        query = "SELECT id FROM trackrequest WHERE trackrequest.requester = {0} AND trackrequest.requested = {1}".format(id, current_id)

        c = self.conn.cursor()
        c.execute(query)

        next = c.fetchone()
        c.close()

        if next is not None:
            trackrequest_id = next[0]
            # Insert Track
            query = "INSERT INTO track (tracker, tracked) VALUES ({0}, {1})".format(id, current_id)

            c = self.conn.cursor()
            c.execute(query)

            # Delete Old Track
            query = "DELETE FROM trackrequest WHERE trackrequest.id = {0}".format(trackrequest_id)
            c.execute(query)

            self.conn.commit()
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

        c = self.conn.cursor()

        ## Verify Username exists
        query = "SELECT id FROM users WHERE users.id = '{0}'".format(username)

        c.execute(query)

        other = c.fetchone()

        if other is None:
            return False

        id = other[0]
        
        query = "SELECT * FROM trackrequest WHERE trackrequest.requester = {0} AND trackrequest.requested = {1}".format(current_id, id)

        c.execute(query)
        isTrackRequest = c.fetchone()

        query = "SELECT * FROM track WHERE track.tracker = {0} AND track.tracked = {1}".format(current_id, id)

        c.execute(query)
        isTrack = c.fetchone()
        ## Request Already Exists
        if isTrackRequest or isTrack:
            return False

        query = "INSERT INTO trackrequest (requester, requested) VALUES ({0}, {1})".format(current_id, id)

        c.execute(query)
        self.conn.commit()
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

        c = self.conn.cursor()
        c.execute(query)
        self.conn.commit()
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

        c = self.conn.cursor()
        c.execute(query)
        self.conn.commit()
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
        c = self.conn.cursor()
        c.execute(query)

        requests = c.fetchall()
        c.close()

        return requests

    def getTrackList(self, token):
        current_id = self._verifyRequest(token)
        if not current_id:
            return False

        # query = "SELECT tracked FROM track WHERE track.tracker = {0}".format(current_id)

        query = "SELECT username FROM users WHERE users.id IN (SELECT tracked FROM track where track.tracker = {0})".format(current_id)

        c = self.conn.cursor()
        c.execute(query)

        tracked = [v.strip() if type(v) is str else v for v in c.fetchall()]
        c.close()

        return tracked

if __name__ == "__main__":
    db = DatabaseConnection()

    ajwurts_token = db.loginUser("ajwurts", "password")
    user2_token = db.loginUser("user2", "pword")

    ajwurts_user = db.getUser(ajwurts_token)
    user2_user = db.getUser(user2_token)

    # print(db._verifyRequest("alpba"))
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