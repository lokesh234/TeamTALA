import cx_Oracle


class DatabaseConnection():

    def __init__(self):

        dsn_tns = cx_Oracle.makedsn('oracle.wpi.edu', '1521','ORCL') # if needed, place an 'r' before any parameter in order to address special characters such as '\'.
        self.conn = cx_Oracle.connect(user=r'ajwurts', password ='AJWURTS', dsn=dsn_tns) # if needed, place an 'r' before any parameter in order to address special characters such as '\'. For example, if your user name contains '\', you'll need to place 'r' before the user name: user=r'User Name'

    def add(self, drop):
        
        query = "INSERT INTO drops (username, force, time, device_type, orientation) VALUES ({0}, {1}, {2}, {3}, {4});".format("ajwurts", 10.44, )

        c = self.conn.cursor();
        c.execute(query);

    

if __name__ == "__main__":
    db = DatabaseConnection()
    db.add(, )