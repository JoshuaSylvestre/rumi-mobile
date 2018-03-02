package com.poop.rumi.rumi;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mongodb.stitch.android.services.mongodb.MongoClient;

import org.bson.Document;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

public class RegisterActivity extends AppCompatActivity {

    private final int USERNAME_EXISTS = 1;
    private final int EMAIL_EXISTS = 2;

    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mFirstNameView;
    private EditText mLastNameView;
    private EditText mEmailView;
    private EditText mUsernameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mRegisterFormView;

    // DB references
    private RumiStitchClient stitchClient;
    private MongoClient.Collection usersCollection;
    private Boolean isRequesting = false;
    private Long userExists = Long.valueOf(0);
    private Document newUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Set up the register form.
        mFirstNameView = findViewById(R.id.first_name);
        mLastNameView = findViewById(R.id.last_name);
        mEmailView = findViewById(R.id.email);
        mUsernameView = findViewById(R.id.username);
        mPasswordView = findViewById(R.id.password);

        Button mRegisterButton = findViewById(R.id.register_button);
        mRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

        mRegisterFormView = findViewById(R.id.register_form);
        mProgressView = findViewById(R.id.login_progress);

        // Make Stitch client
        stitchClient = new RumiStitchClient(getApplicationContext());
    }

    private void attemptRegister() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mFirstNameView.setError(null);
        mLastNameView.setError(null);
        mEmailView.setError(null);
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String firstName = mFirstNameView.getText().toString();
        String lastName = mLastNameView.getText().toString();
        String email = mEmailView.getText().toString();
        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for valid first name
        if(TextUtils.isEmpty(firstName)) {
            mFirstNameView.setError(getString(R.string.error_field_required));
            focusView = mFirstNameView;
            cancel = true;
        }

        // Check for valid last name
        if(TextUtils.isEmpty(lastName)) {
            mLastNameView.setError(getString(R.string.error_field_required));
            if(focusView == null)
                focusView = mLastNameView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            if(focusView == null)
                focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        // Check for valid username
        if(TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            if(focusView == null)
                focusView = mUsernameView;
            cancel = true;
        } else if(!isUsernameValid(username)) {
            mUsernameView.setError("Username should be at least 2 characters long");
            if(focusView == null)
                focusView = mUsernameView;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            if(focusView == null)
                focusView = mPasswordView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(firstName, lastName, email, username, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isUsernameValid(String username) {
        return username.length() > 2;
    }

    private boolean isEmailValid(String email) {
        return email.indexOf("@") < email.lastIndexOf(".");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRegisterFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String firstName;
        private final String lastName;
        private final String username;
        private final String email;
        private final String password;

        UserLoginTask(String firstName, String lastName, String email, String username, String password) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.username = username;
            this.password = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            Document newUserDoc = new Document();
            newUserDoc.put("firstName", this.firstName);
            newUserDoc.put("lastName", this.lastName);
            newUserDoc.put("username", this.username);
            newUserDoc.put("email", this.email);
            newUserDoc.put("password", this.password);

            usersCollection = stitchClient.getMongoDb().getCollection(getString(R.string.stitch_users_collection));

            isRequesting = true;
            stitchClient.getStitchClient().executeFunction("userExists0", this.email, this.username).addOnCompleteListener(new OnCompleteListener<Object>() {
                @Override
                public void onComplete(@NonNull Task<Object> task) {
                    if(!task.isSuccessful()) {
                        Log.e("Stitch", "Cannot check if new user exists.");
                        return;
                    }

                    userExists = (Long) task.getResult();
                    isRequesting = false;
                }
            });

            // Insert a user only if they don't already exist
            if(userExists == 0) {
                isRequesting = true;
                usersCollection.insertOne(newUserDoc).addOnCompleteListener(new OnCompleteListener<Document>() {
                    @Override
                    public void onComplete(@NonNull Task<Document> task) {
                        if (!task.isSuccessful()) {
                            Log.e("Stitch", "Cannot insert new user into collection.");
                            return;
                        }

                        newUser = task.getResult();
                        isRequesting = false;
                    }
                });
            }

            try {
                // Simulate network access.
                Thread.sleep(2000);

                if(!isRequesting && newUser != null && userExists == 0) {
                    userExists = Long.valueOf(0);
                    isRequesting = false;
                    return true;
                }

            } catch (InterruptedException e) {
                return false;
            }

            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);
            Intent getDashboardActivity = new Intent(getApplicationContext(),DashboardActivity.class);


            if (success) {
                startActivity(getDashboardActivity);
//                finish();
            } else {

                if(userExists == USERNAME_EXISTS) {
                    mUsernameView.setError("Account under this username already exists");
                    mUsernameView.requestFocus();
                } else if(userExists == EMAIL_EXISTS) {
                    mEmailView.setError("Account under this email already exists");
                    mEmailView.requestFocus();
                }
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

