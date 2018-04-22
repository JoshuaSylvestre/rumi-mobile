package com.poop.rumi.rumi.summary;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.poop.rumi.rumi.DashboardActivity;
import com.poop.rumi.rumi.R;
import com.poop.rumi.rumi.models.UserModel;
import com.poop.rumi.rumi.transaction.Transaction;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

public class SummaryActivity extends AppCompatActivity {

    private final String TAG = "SummaryActivity";

    // Adapters
    private RecyclerViewAdapter nameListAdapter;
    private SummaryListAdapter summaryListAdapter;

    //
    private ArrayList<Transaction> transactionList;
    private ArrayList<UserModel> addedNamesUM;
    private ArrayList<String> mNames;       // to pass to nameListAdapter
    private ArrayList<String> mImageUrls;

    //
    private ArrayList<ParticipantInfo> participantList;

    //
    private String currUserToken;
    private String currUser;

    private String receiptImagePath;
    private String storeName;
    private String dateOfCapture;

    private String billCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        Bundle transData = getIntent().getExtras();

        assert transData != null;

        transactionList = transData.getParcelableArrayList("TRANSACTION");
        addedNamesUM = transData.getParcelableArrayList("PARTICIPANTS");
        mNames = transData.getStringArrayList("NAMES");


        currUser = getIntent().getStringExtra(getString(R.string.current_user_json_to_string));
        currUserToken = getIntent().getStringExtra(getString(R.string.current_user_token));

        storeName = transData.getString("STORE_NAME");
        receiptImagePath = transData.getString("RECEIPT_IMAGE_PATH");
        dateOfCapture = transData.getString("DATE_OF_CAPTURE");


        participantList = new ArrayList<>();

        tooDeepToGetIntoThis(); // .. to fill participantList []

        final ListView listViewItems = (ListView)findViewById(R.id.vertical_list_participation);

        initImageBitmaps();
        initRecyclerView();

        // take in the context, custom layout that made, arraylist(which is transactionList)
        summaryListAdapter = new SummaryListAdapter(this, R.layout.layout_participation_view, participantList.get(0).getTriadList());
        listViewItems.setAdapter(summaryListAdapter);

        nameListAdapter.passSummaryData(summaryListAdapter, participantList, listViewItems);


        generateSharedCode();


        Button nextButton = (Button)findViewById(R.id.button_next);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                generateJSONSchema();

                Intent intent = new Intent(getApplicationContext(),DashboardActivity.class);
                intent.putExtra(getString(R.string.current_user_json_to_string), currUser);
                intent.putExtra(getString(R.string.current_user_token), currUserToken);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        });

        Button backButton = (Button)findViewById(R.id.button_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });


    }


    private void tooDeepToGetIntoThis() {

        DecimalFormat df = new DecimalFormat("#.00");

        for (UserModel um : addedNamesUM)
            participantList.add(new ParticipantInfo(um.name));


        int numParticipants;
        Float eachPay;
        ArrayList<String> curNames;
        for (Transaction t : transactionList) {

            curNames = t.getNames();

            numParticipants = curNames.size();

            eachPay = t.getPrice() / numParticipants;
            df.format(eachPay);

            //Toast.makeText(this, "ITEM :" + t.getItem() + "<= " + curNames.toString(), Toast.LENGTH_LONG).show();

            for (String name : curNames)
                for (ParticipantInfo p : participantList)
                    if (p.getName().equals(name))
                        p.addItemPrice(t.getItem(), t.getPrice(), eachPay);

        }
    }


    private void initImageBitmaps(){

        mImageUrls = new ArrayList<>();

        for(int i = 0; i <  addedNamesUM.size(); i++)
            mImageUrls.add("");

    }

    private void initRecyclerView() {

        LinearLayoutManager layoutManager = new LinearLayoutManager( SummaryActivity.this,
                LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = findViewById(R.id.horizontal_recycler_view);
        recyclerView.setLayoutManager(layoutManager);
        nameListAdapter = new RecyclerViewAdapter(this, mNames, mImageUrls);
        recyclerView.setAdapter(nameListAdapter);


    }

    protected String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 6) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }

        return salt.toString();

    }

    public void generateSharedCode(){

        TextView textView = (TextView)findViewById(R.id.share_code);
        billCode = getSaltString();
        textView.setText(billCode);
    }


    private void generateJSONSchema() {



    }


}

