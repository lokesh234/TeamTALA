from flask import Flask, escape, request
import json
from Database import DatabaseConnection
app = Flask(__name__)

db = DatabaseConnection()

@app.route('/')
def hello():
    name = request.args.get("name", "World")
    return f'Hello, {escape(name)}!'

@app.route('/api/drop', methods=['GET', 'POST'])
def drop():
    if request.method == "POST":
        print(request.get_json())
        
        db.add(json.loads(request.get_json()))
   
    return f'success'

@app.route('/api/drops', methods=['GET'])
def getAll():
    if request.method == "GET":
        return db.getAll();

