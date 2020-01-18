from flask import Flask, escape, request
import json
from Database import DatabaseConnection
app = Flask(__name__)

db = DatabaseConnection()

## Create User
@app.route('/api/user', methods=['POST'])
def user():
    user_data = request.get_json()
    wasSuccess = db.createUser(user_data['username'], user_data['password'])
    return "success" if wasSuccess else "already exists"

## User Login
@app.route('/api/user/login', methods=['POST'])
def user_login():
    user_data = request.get_json()
    token = db.loginUser(user_data['username'], user_data['password'])
    return_json = json.dumps({"token": token})
    return return_json

## Get User Track
@app.route('/api/track/<int:user_id>', methods=['GET', 'DELETE'])
def track(user_id):
    auth = request.headers.get('Authorization')
    token = auth.split(' ')[1]
    if request.method == "GET":
        return str(db.trackUser(token, user_id))
    else:
        wasSuccess = db.removeTrack(token, user_id)
        return "success" if wasSuccess else "failure"

## Get "Friend" track list
@app.route('/api/tracks', methods=['GET'])
def tracks():
    auth = request.headers.get("Authorization")
    token = auth.split(' ')[1]

    tracks = db.getTrackList(token)
    tracks = [t[0] for t in tracks]
    tracks_json = json.dumps(tracks)
    return tracks_json

## Create/Delete Track Request
@app.route('/api/track/request/<int:user_id>', methods=['POST', 'DELETE'])
def track_request(user_id):
    auth = request.headers.get("Authorization")
    token = auth.split(' ')[1]
    if request.method == "POST":
        wasSuccess = db.requestTrack(token, user_id)
        return "success" if wasSuccess else "already exists"
    else:
        wasSuccess = db.removeTrackRequest(token, user_id)
        return "success" if wasSuccess else "failure"
   
## Approve Track Request
@app.route('/api/track/request/approve/<int:user_id>', methods=['POST'])
def approve_track_request(user_id):
    auth = request.headers.get("Authorization")
    token = auth.split(' ')[1]

    wasSuccess = db.allowTrack(token, user_id)
    return "success" if wasSuccess else "already exists"

## Get All Track Requests
@app.route('/api/track/requests', methods=['GET', 'POST'])
def requests():
    auth = request.headers.get("Authorization")
    token = auth.split(' ')[1]

    tracks = db.getFriendRequests(token)
    tracks = [t[0] for t in tracks]
    tracks_json = json.dumps(tracks)
    print(tracks_json)
    return tracks_json
