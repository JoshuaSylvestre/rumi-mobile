package com.poop.rumi.rumi;

/**
 * Created by dita on 4/21/18.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.poop.rumi.rumi.models.TransactionModel;

import java.util.List;


public class TransactionRecycleViewAdapter extends RecyclerView.Adapter<TransactionRecycleViewAdapter.CustomViewHolder> {
    private List<TransactionModel> transactionList;
    private Context mContext;

    public TransactionRecycleViewAdapter(Context context, List<TransactionModel> dashboardList) {
        this.transactionList = dashboardList;
        this.mContext = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.transaction_list_card_view, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {
        final TransactionModel currItem = transactionList.get(i);

        customViewHolder.storeName.setText(currItem.storeName);
        customViewHolder.date.setText(currItem.billDate);
        customViewHolder.billCode.setText(currItem.billCode);
        List<TransactionModel.TransactionItemModel> transactionItemModelList = currItem.transactionList;
        int len = transactionItemModelList.size();
        String[] participants = new String[len];

        for(int j = 0; j < len; j++) {
            participants[j] = transactionItemModelList.get(j).name;
        }

        customViewHolder.participants.setText(formatStringArray(participants));
    }

    private String formatStringArray(String[] arr) {
        StringBuilder sb = new StringBuilder();
        int len = arr.length;

        for(int i = 0; i < len; i++) {
            sb.append(arr[i]);

            if(i < (len - 1))
                sb.append(", ");
        }

        return sb.toString();
    }

    @Override
    public int getItemCount() {
        return (null != transactionList ? transactionList.size() : 0);
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView storeName;
        protected TextView date;
        protected TextView billCode;
        protected TextView participants;

        public CustomViewHolder(View view) {
            super(view);

            this.storeName = view.findViewById(R.id.store_name);
            this.date = view.findViewById(R.id.date);
            this.billCode = view.findViewById(R.id.bill_code);
            this.participants = view.findViewById(R.id.participants);
        }
    }
}
