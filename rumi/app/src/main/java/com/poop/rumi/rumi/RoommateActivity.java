package com.poop.rumi.rumi;

import android.app.DialogFragment;
import android.database.DataSetObserver;
import android.net.sip.SipSession;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.poop.rumi.rumi.fragments.AddRoommateFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
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
    private List<HashMap<String, String>> roommateArrayList = new ArrayList<>();
    private ListAdapter roommateListAdapter;

    // User info mapping
    public static String NAME = "name";
    public static String FIRST_NAME = "firstName";
    public static String LAST_NAME = "lastName";
    public static String PREFERRED_NAME = "preferredName";
    public static String ADDRESS = "address";
    public static String EMAIL = "email";
    public static String HOME_PHONE = "homePhone";
    public static String CELL_PHONE = "cellPhone";

    private AddRoommateFragment addRoommateFragment;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roommate);

        requestQueue = Volley.newRequestQueue(this);
        roommateListUrl = getString(R.string.base_url) + getString(R.string.roommate_list_url);
        currUserToken = getIntent().getStringExtra("token");
        currUser = getIntent().getStringExtra("user");

        View contentRoommateView = findViewById(R.id.content_roommate_layout);
        contentRoommateListView = contentRoommateView.findViewById(R.id.roommate_list_view);

        // Map user info to the correct TextView item in UI
        roommateListAdapter = new SimpleAdapter(this, roommateArrayList, R.layout.roommate_list_item,
                new String[] {NAME, PREFERRED_NAME, ADDRESS, EMAIL, CELL_PHONE, HOME_PHONE},
                new int[] {R.id.roommate_list_name, R.id.roommate_list_preferred_name, R.id.roommate_list_address,
                        R.id.roommate_list_email, R.id.roommate_list_cell_phone, R.id.roommate_list_home_phone});

        // Set list of roommates to the view in UI
        contentRoommateListView.setAdapter(roommateListAdapter);

        // TODO: RecycleView and onUpdateListener
        makeListRequest();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_roommate_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addRoommateFragment = new AddRoommateFragment();
                Bundle args = new Bundle();
                args.putString("user", currUser);
                args.putString("token", currUserToken);
                addRoommateFragment.setArguments(args);

                addRoommateFragment.show(getFragmentManager(), "Add Roommate Fragment");

                addRoommateFragment.setDialogResult(new AddRoommateFragment.AddRoommateFragmentResult() {
                    @Override
                    public void finish(HashMap<String, String> result) {
                        roommateArrayList.add(result);

                        Log.e("ROOMMATE", result.get(FIRST_NAME));
                        ((BaseAdapter)roommateListAdapter).notifyDataSetChanged();
                    }
                });
            }
        });
    }

    protected void makeListRequest() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, roommateListUrl,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        // Success callback
                        Log.i("ROOMMATE", response.toString());
//                        responseJSON = response;
                        try {
                            roommateList = response.getJSONArray("contactlist");

                            int numRoommates = roommateList.length();

                            for(int i = 0; i < numRoommates; i++) {
                                HashMap<String, String> currUserInfoMap = new HashMap<>();

                                try {
                                    JSONObject currJSON = roommateList.getJSONObject(i);

                                    currUserInfoMap.put(NAME, currJSON.getString("firstName") + " " + currJSON.getString("lastName"));
                                    currUserInfoMap.put(PREFERRED_NAME, currJSON.getString(PREFERRED_NAME));
                                    currUserInfoMap.put(ADDRESS, currJSON.getString(ADDRESS));
                                    currUserInfoMap.put(EMAIL, currJSON.getString(EMAIL));
                                    currUserInfoMap.put(HOME_PHONE, currJSON.getString(HOME_PHONE));
                                    currUserInfoMap.put(CELL_PHONE, currJSON.getString(CELL_PHONE));

                                    roommateArrayList.add(currUserInfoMap);
                                } catch(Exception ex) {
                                    Log.i("ROOMMATE", "Cannot add roommate to list");
                                }
                            }

                            ((BaseAdapter)roommateListAdapter).notifyDataSetChanged();

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

}

