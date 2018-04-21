package com.poop.rumi.rumi;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.poop.rumi.rumi.models.TransactionModel;
import com.poop.rumi.rumi.models.UserModel;
import com.poop.rumi.rumi.transaction.Transaction;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactionActivity extends AppCompatActivity {

    private RecyclerView transactionRecyclerView;
    private TransactionRecycleViewAdapter transactionAdapter;
    private List<TransactionModel> transactionsList = new ArrayList<>();

    private RequestQueue requestQueue;
    private String currUserToken;
    private String currUser;
    private boolean success = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        requestQueue = Volley.newRequestQueue(this);
        currUserToken = getIntent().getStringExtra("token");
        currUser = getIntent().getStringExtra("user");

        View transactionListView = findViewById(R.id.content_transaction_view);
        transactionRecyclerView = transactionListView.findViewById(R.id.transaction_list_recycler_view);
        transactionRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        transactionAdapter = new TransactionRecycleViewAdapter(this, transactionsList);

        transactionRecyclerView.setAdapter(transactionAdapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Transactions");
        setSupportActionBar(toolbar);

        (new TransactionTask()).execute((Void) null);
    }

    private void makeListRequest() {
        String transactionListUrl = getString(R.string.base_url) + getString(R.string.transactions_list_url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, transactionListUrl,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        // Success callback
                        Log.i("TRANSACTION", response.toString());

                        try {
                            JSONArray arr = response.getJSONArray("transactions");

                            int numRoommates = arr.length();

                            for(int i = 0; i < numRoommates; i++) {
                                transactionsList.add(new TransactionModel(arr.getJSONObject(i)));
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
                        Log.i("TRANSACTION", error.toString());
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

    public class TransactionTask extends AsyncTask<Void, Void, Boolean> {

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
                Log.e("TRANSACTIONS", "Error fetching transactions: " + ex.getMessage());
            }

            return success;
        }

        @Override
        protected void onPostExecute(Boolean success) {
//            progressBar.setVisibility(View.GONE);

            if (success) {
                transactionAdapter.notifyDataSetChanged();
            } else {
                transactionAdapter.notifyDataSetChanged();
                Toast.makeText(TransactionActivity.this, "Failed to fetch data!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
