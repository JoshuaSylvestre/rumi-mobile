package com.poop.rumi.rumi;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mongodb.stitch.android.StitchClient;
import com.mongodb.stitch.android.StitchClientFactory;
import com.mongodb.stitch.android.auth.emailpass.EmailPasswordAuthProvider;
import com.mongodb.stitch.android.services.mongodb.MongoClient;

public class RumiStitchClient {

    // DB references
    private StitchClient stitchClient;
    private MongoClient mongoClient;
    private EmailPasswordAuthProvider dbAuthProvier;
    private MongoClient.Database mongoDb;
    private Boolean isRequesting = false;

    RumiStitchClient(final Context context) {

        isRequesting = true;
        StitchClientFactory.create(context, context.getString(R.string.stitch_api_key)).addOnCompleteListener(new OnCompleteListener<StitchClient>() {
            @Override
            public void onComplete(@NonNull Task<StitchClient> task) {
                if(!task.isSuccessful()) {
                    Log.e("Stitch", "Error getting stitch client.");
                    isRequesting = false;
                    return;
                }

                stitchClient = task.getResult();
                dbAuthProvier = new EmailPasswordAuthProvider(context.getString(R.string.stitch_user_email), context.getString(R.string.stitch_user_password));

                if(!stitchClient.isAuthenticated()) {
                    isRequesting = true;
                    stitchClient.logInWithProvider(dbAuthProvier).addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            if (!task.isSuccessful()) {
                                Log.e("Stitch", "Cannot log into stitch.");
                                isRequesting = false;
                                return;
                            }

                            mongoClient = new MongoClient(stitchClient, context.getString(R.string.stitch_service));
                            mongoDb = mongoClient.getDatabase(context.getString(R.string.stitch_db_name));
                            isRequesting = false;
                        }
                    });
                } else {
                    mongoClient = new MongoClient(stitchClient, context.getString(R.string.stitch_service));
                    mongoDb = mongoClient.getDatabase(context.getString(R.string.stitch_db_name));

                    if(mongoDb == null)
                        System.out.println("mongoDb is null");

                    isRequesting = false;
                }
            }
        });

//        try {
//            Thread.sleep(1000);
//        } catch(Exception e) {
//
//        }
    }

    public Boolean getIsRequesting() { return isRequesting; }

    public StitchClient getStitchClient() {
        return stitchClient;
    }

    public MongoClient getMongoClient() {
        return mongoClient;
    }

    public EmailPasswordAuthProvider getDbAuthProvier() {
        return dbAuthProvier;
    }

    public MongoClient.Database getMongoDb() {
        return mongoDb;
    }
}
