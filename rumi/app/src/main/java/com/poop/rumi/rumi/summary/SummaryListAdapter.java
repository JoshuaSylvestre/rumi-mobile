package com.poop.rumi.rumi.summary;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.poop.rumi.rumi.R;
import com.poop.rumi.rumi.transaction.Transaction;

import java.util.ArrayList;


public class SummaryListAdapter extends ArrayAdapter<ParticipantInfo.ParticipantTriad> {

    private static final String TAG = "SummaryListAdapter";
    private Context mContext;
    private int mResource;

    private ArrayList<ParticipantInfo.ParticipantTriad> participantInfo;

    private ArrayList<Transaction> transactionList;
    private RecyclerViewAdapter nameListAdapter;

    private ViewGroup parent;


    private final int primaryColor = Color.rgb(87, 188, 150);
    private final int secondaryColor = Color.rgb(238, 238, 255);

    public SummaryListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<ParticipantInfo.ParticipantTriad> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        participantInfo =  objects;
    }


    /**
     *  Needed to call get getLastNamePos() & getLastNameTapped()
     */
    public void setRecyclerViewAdapter(RecyclerViewAdapter nameListAdapter){
        this.nameListAdapter = nameListAdapter;
    }

    public static class ViewHolder{
        TextView itemTextView;
        TextView origPriceTextView;
        TextView owedPriceTextView;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        this.parent = parent;
        View rowView = convertView;

        if(convertView == null){

            LayoutInflater inflater = LayoutInflater.from(mContext);
            rowView = inflater.inflate(mResource, parent, false);

            ViewHolder vh = new ViewHolder();
            vh.itemTextView = rowView.findViewById(R.id.item_name_view);
            vh.origPriceTextView = rowView.findViewById(R.id.original_price_view);
            vh.owedPriceTextView = rowView.findViewById(R.id.owed_price_view);

            rowView.setTag(vh);

        }

        final ViewHolder holder = (ViewHolder) rowView.getTag();


        // Get transaction information
        // Get transaction information
        final String item = getItem(position).getItem();
        final Float ogPrice = getItem(position).getOgPrice();
        final Float owedPrice = getItem(position).getOwedPrice();


        final LinearLayout linearLayout = (LinearLayout)rowView.findViewById(R.id.parent_layout_item_owed);


        linearLayout.isLongClickable();
        /**
        *   Adding/removing names to each item and highlighting appropriately
        * */
        final View finalRowView = rowView;

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        /**
         *   Long click listener to enable editing item name and price
         * */

        linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                return true;

            }
        });



        holder.itemTextView.setText(item);
        holder.origPriceTextView.setText("$" + ogPrice.toString());
        holder.owedPriceTextView.setText("$" + owedPrice.toString());

        return rowView;
    }



}