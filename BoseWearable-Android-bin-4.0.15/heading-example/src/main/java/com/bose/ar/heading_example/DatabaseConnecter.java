package com.bose.ar.heading_example;

import android.app.DownloadManager;
import android.content.Context;
import android.os.AsyncTask;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class DatabaseConnecter {
    String username;
    String password;
    String token;
    Context context;
    JSONArray tracks;

    final static String BASE = "https://framefinder.appspot.com/api/";

    public DatabaseConnecter(Context context) {
        this.context = context;

    }

    private void request(String url, int type, JSONObject body, RequestUpdate callback) {
        RequestQueue queue = Volley.newRequestQueue(this.context);

        DatabaseConnecter that = this;

        // Request a string response from the provided URL
        JsonObjectRequest stringRequest = new JsonObjectRequest(type, url,  body,
                response -> {
                    System.out.println(response);
                    callback.updateAfterRequest(response);

                },
                error -> System.out.println(error))
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + that.token);
                return params;
            }
        };
        queue.add(stringRequest);
    }

    public void login(String username, String password, RequestUpdate callback) throws ExecutionException, InterruptedException, JSONException, TimeoutException {
        // Instantiate the RequestQueue.
        String url = DatabaseConnecter.BASE + "user/login";

        JSONObject body = new JSONObject();
        try {
            body.put("username", username);
            body.put("password", password);
        } catch (Exception e) {
            // Pass
        }
        DatabaseConnecter that = this;

        request(url, Request.Method.POST, body, new RequestUpdate() {
            @Override
            public void updateAfterRequest(JSONObject obj) {
                try {
                    that.token = obj.getString("token");
                    callback.updateAfterRequest(new JSONObject());
                } catch (Exception e) {

                }
            }
        });

    }

    public void getTracks(RequestUpdate callback)  {
        // Instantiate the RequestQueue
        String url = DatabaseConnecter.BASE + "tracks";
        request(url, Request.Method.GET, null, callback);
    }

    public void getTrackRequests(RequestUpdate callback)  {
        // Instantiate the RequestQueue
        String url = DatabaseConnecter.BASE + "track/requests";

        request(url, Request.Method.GET,null, callback);

    }

    public void requestTrack(int id, RequestUpdate callback) {
        String url = DatabaseConnecter.BASE + "/track/request/" + Integer.toString(id);
        request(url, Request.Method.POST,null, callback);
    }

    public void allowTrack(int id, RequestUpdate callback) {
        String url = DatabaseConnecter.BASE + "/track/request/approve/" + Integer.toString(id);
        request(url, Request.Method.POST, null, callback);
    }

    public void setLoc(float lat, float lon, RequestUpdate callback) {
        String url = DatabaseConnecter.BASE + "user/location";
        JSONObject body = new JSONObject();
        try {
            body.put("lat", lat);
            body.put("lon", lon);
        } catch (Exception e) {
            // Pass
        }
        request(url, Request.Method.POST, body, callback);
    }

    public void track(int id, RequestUpdate callback) {
        String url = DatabaseConnecter.BASE + "track/" + Integer.toString(id);
        request(url, Request.Method.GET,null, callback);
    }

    public void deleteTrack(int id, RequestUpdate callback) {
        String url = DatabaseConnecter.BASE + "/track/" + Integer.toString(id);
        request(url, Request.Method.DELETE, null, callback);
    }

}
