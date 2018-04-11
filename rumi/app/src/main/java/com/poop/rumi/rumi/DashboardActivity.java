package com.poop.rumi.rumi;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.poop.rumi.rumi.fragments.AddRoommateFragment;
import com.poop.rumi.rumi.fragments.UserSettingsFragment;
import com.poop.rumi.rumi.ocr.OcrCaptureActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
{
    private RecyclerView mRecyclerView;
    private RecycleViewAdapter adapter;
//    private ProgressBar progressBar;
    private List<TransactionFeed> feedsList;
    private TextView sidebarName;
    private TextView sidebarEmail;

    // User values
    private UserSession userSession;
    private String currUser;
    private String currUserToken;
    private JSONObject currUserJSON;

    private UserSettingsFragment userSettingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Create a new session for the logged user and grab their data from the last intent
        userSession = new UserSession(this);
        currUser = getIntent().getStringExtra("user");
        currUserToken = getIntent().getStringExtra("token");

        try {
            currUserJSON = new JSONObject(currUser);
        } catch(Exception ex) {
            Log.i("DASHBOARD", "Error creating JSON");
        }
        userSession.setUser(currUser);

        // Get nested view from sidebar
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);

        // Set user info in the sidebar
        sidebarName = header.findViewById(R.id.sidebar_name);
        sidebarEmail = header.findViewById(R.id.sidebar_email);

        try {
            sidebarName.setText(currUserJSON.getString("name"));
            sidebarEmail.setText(currUserJSON.getString("email"));
        } catch(Exception ex) {
            Log.i("DASHBOARD", "Error setting sidebar info for user.");
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        progressBar = findViewById(R.id.app_bar_main_layout).findViewById(R.id.app_bar_main_layout).findViewById(R.id.progress_bar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(view.getContext(), OcrCaptureActivity.class));
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

//        String url = "http://stacktips.com/?json=get_category_posts&slug=news&count=30";
//
//        new DownloadTask().execute(url);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            userSettingsFragment = new UserSettingsFragment();
            Bundle args = new Bundle();
            args.putString("user", currUser);
            args.putString("token", currUserToken);
            userSettingsFragment.setArguments(args);

            userSettingsFragment.show(getFragmentManager(), "Add Roommate Fragment");
        }
        if(id == R.id.action_logout){
            Intent logoutIntent = new Intent(DashboardActivity.this, LoginActivity.class);
            logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            MessagePopups.showToast(this, "Logged out!");
            startActivity(logoutIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_scan)
        {
            // Handle the camera action
        }
        else if (id == R.id.nav_roommates)
        {
            Intent getRoommateActivity = new Intent(getApplicationContext(), RoommateActivity.class);

            // Put the user info JSON in the intent to pass it to the next activity
            getRoommateActivity.putExtra("user", currUser);
            getRoommateActivity.putExtra("token", currUserToken);

            startActivity(getRoommateActivity);
        }
        else if (id == R.id.nav_transactions)
        {
            Intent getTransactionActivity = new Intent(getApplicationContext(), TransasctionActivity.class);
            startActivity(getTransactionActivity);
        }
        else if (id == R.id.nav_request)
        {

        }
        else if (id == R.id.nav_send)
        {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public class DownloadTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected void onPreExecute() {
//            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Integer doInBackground(String... params) {
            Integer result = 0;
            HttpURLConnection urlConnection;
            try {
                URL url = new URL(params[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                int statusCode = urlConnection.getResponseCode();

                // 200 represents HTTP OK
                if (statusCode == 200) {
                    BufferedReader r = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = r.readLine()) != null) {
                        response.append(line);
                    }
                    parseResult(response.toString());
                    result = 1; // Successful
                } else {
                    result = 0; //"Failed to fetch data!";
                }
            } catch (Exception e) {
                //Log.d(TAG, e.getLocalizedMessage());
            }
            return result; //"Failed to fetch data!";
        }

        @Override
        protected void onPostExecute(Integer result) {
//            progressBar.setVisibility(View.GONE);

            if (result == 1) {
                adapter = new RecycleViewAdapter(DashboardActivity.this, feedsList);
                mRecyclerView.setAdapter(adapter);
            } else {
                Toast.makeText(DashboardActivity.this, "Failed to fetch data!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void parseResult(String result) {
        try {
            JSONObject response = new JSONObject(result);
            JSONArray posts = response.optJSONArray("posts");
            feedsList = new ArrayList<>();

            for (int i = 0; i < posts.length(); i++) {
                JSONObject post = posts.optJSONObject(i);
                TransactionFeed item = new TransactionFeed();
                item.setTitle(post.optString("title"));
                item.setThumbnail(post.optString("thumbnail"));
                feedsList.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

