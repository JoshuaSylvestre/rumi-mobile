package com.poop.rumi.rumi.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.poop.rumi.rumi.MessagePopups;
import com.poop.rumi.rumi.R;
import com.poop.rumi.rumi.RoommateActivity;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by dita on 4/9/18.
 */

public class AddRoommateFragment extends DialogFragment {

    private EditText searchQuery;

    private View dialogView;

    private JSONObject currUserJSON;
    private String currUserToken;
    private RequestQueue requestQueue;
    private AlertDialog alertDialog;

//    HashMap<String, String> newRoommateMap;
    private OnCompleteListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme);
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        dialogView = inflater.inflate(R.layout.fragment_add_roommate, null);

        String currUser = getArguments().getString("user");
        currUserToken = getArguments().getString("token");
        requestQueue = Volley.newRequestQueue(getActivity());

        try {
            currUserJSON = new JSONObject(currUser);
        } catch(Exception ex) {
            Log.e("ADD-ROOMMATE", "Cannot create user JSON");
        }

        searchQuery = dialogView.findViewById(R.id.search_roommate);

        builder.setPositiveButton(R.string.search_users, null);
        builder.setTitle("Search Users");

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(dialogView)
                .setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AddRoommateFragment.this.getDialog().cancel();
                    }
                });

        alertDialog = builder.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.WHITE);
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);

                Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        searchRoommates();
                    }
                });
            }
        });

        return alertDialog;
    }

    private void searchRoommates() {
        String roommateAddUrl = getString(R.string.base_url) + getString(R.string.roommate_add_url);
        JSONObject params = new JSONObject();

        try {
//            params.put(RoommateActivity.FIRST_NAME, mFirstName.getText().toString());
//            params.put(RoommateActivity.LAST_NAME, mLastName.getText().toString());
//            params.put(RoommateActivity.PREFERRED_NAME, mPreferredName.getText().toString());
//            params.put(RoommateActivity.ADDRESS, mAddress.getText().toString());
//            params.put(RoommateActivity.EMAIL, mEmail.getText().toString());
//            params.put(RoommateActivity.HOME_PHONE, mHomePhone.getText().toString());
//            params.put(RoommateActivity.CELL_PHONE, mCellPhone.getText().toString());
            params.put("date", (new SimpleDateFormat("EEE MMM dd YYYY", Locale.US)).format(new Date()));
            params.put("user_id", currUserJSON.getString("id"));

        } catch(Exception ex) {
            Log.e("ADD-ROOMMATE", "Cannot create request params");

            MessagePopups.showToast(getActivity(), "Failed adding new roommate.");

            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, roommateAddUrl, params,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        // Success callback
                        Log.i("ADD-ROOMMATE", response.toString());

                        try {
                            boolean success = response.getBoolean("success");

                            if(success) {
//                                newRoommateMap = new HashMap<>();
//                                newRoommateMap.put(RoommateActivity.NAME, mFirstName.getText().toString() + " " + mLastName.getText().toString());
//                                newRoommateMap.put(RoommateActivity.PREFERRED_NAME, mPreferredName.getText().toString());
//                                newRoommateMap.put(RoommateActivity.ADDRESS, mAddress.getText().toString());
//                                newRoommateMap.put(RoommateActivity.EMAIL, mAddress.getText().toString());
//                                newRoommateMap.put(RoommateActivity.HOME_PHONE, mHomePhone.getText().toString());
//                                newRoommateMap.put(RoommateActivity.CELL_PHONE, mCellPhone.getText().toString());
//                                mListener.onComplete(newRoommateMap);

                                MessagePopups.showToast(AddRoommateFragment.this.getContext(), "Added new roommate!");
                                AddRoommateFragment.this.getDialog().cancel();
                            }
                            else {
                                MessagePopups.showToast(AddRoommateFragment.this.getContext(), "Failed adding new roommate.");
                                AddRoommateFragment.this.getDialog().cancel();
                            }

                        } catch(Exception ex) {
                            Log.e("ADD-ROOMMATE", "Failed parsing JSON response");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Error callback
                        Log.e("ADD-ROOMMATE", error.toString());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", currUserToken);

                return headers;
            }
        };

        requestQueue.add(request);
    }

    public interface OnCompleteListener {
        void onComplete(HashMap<String, String> newRoommate);
    }

    // make sure the Activity implemented it
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            this.mListener = (OnCompleteListener)context;
        }
        catch (final ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnCompleteListener");
        }
    }

}
