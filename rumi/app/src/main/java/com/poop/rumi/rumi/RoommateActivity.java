package com.poop.rumi.rumi;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.poop.rumi.rumi.fragments.SearchRoommateFragment;
import com.poop.rumi.rumi.models.UserModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoommateActivity extends AppCompatActivity {

    private String roommateListUrl;
    private String currUserToken;
    private String currUser;
//    private JSONObject responseJSON;
    private RequestQueue requestQueue;
    private JSONArray roommateList;

    private ListView contentRoommateListView;
    private List<UserModel> roommateArrayList = new ArrayList<>();
    private RoommateRecycleViewAdapter roommateListAdapter;
    private RecyclerView roommateRecyclerView;
    private boolean success = false;

    private SearchRoommateFragment searchRoommateFragment;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roommate);

        requestQueue = Volley.newRequestQueue(this);
        roommateListUrl = getString(R.string.base_url) + getString(R.string.roommate_list_url);
        currUserToken = getIntent().getStringExtra("token");
        currUser = getIntent().getStringExtra("user");

        View contentRoommateView = findViewById(R.id.content_roommate_layout);

        roommateRecyclerView = contentRoommateView.findViewById(R.id.roommate_recycler_view);
        roommateRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        roommateListAdapter = new RoommateRecycleViewAdapter(this, roommateArrayList, currUserToken);

        // Set list of roommates to the view in UI
        roommateRecyclerView.setAdapter(roommateListAdapter);

        (new RoommateTask()).execute((Void) null);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Roommates");
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_roommate_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchRoommateFragment = new SearchRoommateFragment();
                Bundle args = new Bundle();
                args.putString("user", currUser);
                args.putString("token", currUserToken);
                searchRoommateFragment.setArguments(args);

                searchRoommateFragment.show(getFragmentManager(), "Search Roommate Fragment");
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == SearchRoommateFragment.SEARCH_REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK) {
                finish();
                startActivity(getIntent());
            }
        }
    }

    public void makeListRequest() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, roommateListUrl,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        // Success callback
                        Log.i("ROOMMATE", response.toString());
//                        responseJSON = response;
                        try {
                            roommateList = response.getJSONArray("roommates");
                            roommateArrayList.clear();

                            int numRoommates = roommateList.length();

                            for(int i = 0; i < numRoommates; i++) {
                                try {
                                    JSONObject currJSON = roommateList.getJSONObject(i);
                                    UserModel userModel = new UserModel(currJSON);
                                    roommateArrayList.add(userModel);
                                    roommateListAdapter.notifyDataSetChanged();

                                } catch (Exception ex) {
                                    Log.e("ROOMMATES", "Cannot get roommates");
                                }
                            }

                            success = true;

                        } catch(Exception ex) {
                            Log.e("ROOMMATE", "Error parsing JSON");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Error callback
                        Log.i("ROOMMATE", error.toString());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
//                headers.put("Content-Type", "application/json");
                headers.put("Authorization", currUserToken);

                return headers;
            }
        };

        requestQueue.add(request);
    }

    public class RoommateTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
//            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            makeListRequest();

            try {
                Thread.sleep(2000);

            } catch(Exception ex) {
                Log.e("DASHBOARD", "Error fetching dashboard: " + ex.getMessage());
            }

            return success;
        }

        @Override
        protected void onPostExecute(Boolean success) {
//            progressBar.setVisibility(View.GONE);

            if (success) {
                roommateListAdapter.notifyDataSetChanged();
            } else {
                roommateListAdapter.notifyDataSetChanged();
                Toast.makeText(RoommateActivity.this, "Failed to fetch data!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

