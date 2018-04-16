package com.poop.rumi.rumi.transaction_classes;

/**
 * Created by Abe on 4/10/2018.
 */

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.poop.rumi.rumi.R;


import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";
    private ArrayList<String> mImageNames;
    private ArrayList<String> mImagesURL;
    private Context mContext;

    private int lastNamePos;
    private String lastNameTapped;
    private ViewHolder lastViewHolder;

    private TransactionListAdapter transListAdapter;

    private final int primaryColor = Color.rgb(87, 188, 150);
    private final int secondaryColor = Color.rgb(238, 238, 255);

    public RecyclerViewAdapter(Context mContext, ArrayList<String> mImageNames, ArrayList<String> mImagesURL) {
        this.mImageNames = mImageNames;
        this.mImagesURL = mImagesURL;
        this.mContext = mContext;

        lastNamePos = -1;
        lastViewHolder = null;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: called Viewholder!");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_name_box, parent, false);

        return new ViewHolder(view);

    }

    /**
     *  Needed to call highlightRowsAppropriately()
     */
    public void setTransactionListAdapter(TransactionListAdapter transListAdapter){
        this.transListAdapter = transListAdapter;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called!");


        Glide.with(mContext)
                .asBitmap()
                .load(mImagesURL.get(position))
                .into(holder.image);

        holder.name.setText(mImageNames.get(position));

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d(TAG, "onClick: Clicked on: " + mImageNames.get(position));
                //Toast.makeText(mContext, "NAME " +position+ " " +mImageNames.get(position), Toast.LENGTH_SHORT).show();

                // Set to primary color to indicate which participant is selected
                holder.parentLayout.findViewById(R.id.name_layout).setBackgroundColor(secondaryColor);

                // Set previously selected participant to secondary color and remove all associated highlights
                if(lastViewHolder != null && holder != lastViewHolder) {
                    lastViewHolder.parentLayout.findViewById(R.id.name_layout).setBackgroundColor(primaryColor);

                }
                lastNameTapped = mImageNames.get(position);
                lastViewHolder = holder;
                lastNamePos = position;

                transListAdapter.highlightRowsAppropriately();

            }
        });




    }

    @Override
    public int getItemCount() {
        return mImageNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView image;
        TextView name;
        RelativeLayout parentLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image_view);
            name = itemView.findViewById(R.id.textView_image_name);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }

    public int getLastNamePos(){
        return lastNamePos;
    }

    public String getLastNameTapped(){
        return lastNameTapped;
    }



}
