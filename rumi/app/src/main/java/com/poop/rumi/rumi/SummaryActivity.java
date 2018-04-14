package com.poop.rumi.rumi;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.poop.rumi.rumi.transaction_classes.Transaction;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class SummaryActivity extends AppCompatActivity {

    ArrayList<Transaction> transactionList;
    ArrayList<String> names;

    ArrayList<ParticipantInfo> participantList;
    String storeName;


    int numTapped = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        Bundle transData = getIntent().getExtras();

        assert transData != null;
        transactionList = transData.getParcelableArrayList("TRANSACTION");
        names = transData.getStringArrayList("PARTICIPANTS");
        storeName = transData.getString("STORENAME");
        //TODO: get store name and data


        participantList = new ArrayList<>();
        doMath();


        Button bt = findViewById(R.id.infoButton);
        final TextView tv = findViewById(R.id.infoText);


        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (numTapped < participantList.size())
                    tv.setText(participantList.get(numTapped++).printInfo());


            }
        });

    }

    private void doMath() {

        DecimalFormat df = new DecimalFormat("#.00");

        for (String name : names)
            participantList.add(new ParticipantInfo(name));


        int numParticipants;
        Float eachPay;
        ArrayList<String> curNames;
        for (Transaction t : transactionList) {

            curNames = t.getNames();

            numParticipants = curNames.size();

            eachPay = t.getPrice() / numParticipants;
            df.format(eachPay);

            Toast.makeText(this, "ITEM :" + t.getItem() + "<= " + curNames.toString(), Toast.LENGTH_LONG).show();

            for (String name : curNames)
                for (ParticipantInfo p : participantList)
                    if (p.getName().equals(name))
                        p.addItemPrice(t.getItem(), t.getPrice(), eachPay);

        }
    }


}

