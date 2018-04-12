package com.poop.rumi.rumi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.poop.rumi.rumi.models.DashboardContentModel;
import com.poop.rumi.rumi.models.ReceiptModel;
import com.poop.rumi.rumi.models.RoommateModel;
import com.poop.rumi.rumi.models.TransactionModel;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.CustomViewHolder> {
    private List<DashboardContentModel> dashboardList;
    private Context mContext;

    public RecycleViewAdapter(Context context, List<DashboardContentModel> dashboardList) {
        this.dashboardList = dashboardList;
        this.mContext = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {
        final DashboardContentModel currItem = dashboardList.get(i);

        if(currItem instanceof ReceiptModel) {
            String imageUrl = ((ReceiptModel) currItem).link;
            customViewHolder.image.setVisibility(View.VISIBLE);
            RequestOptions options = new RequestOptions();
//            options.override(customViewHolder.image.getWidth() / 2, customViewHolder.image.getHeight() / 2);
//            options.placeholder(R.drawable.rumi_logo);

            // Why is this shit not working. The image loaded once and disappeared like a leprechaun
            Glide.with(customViewHolder.image.getContext())
                    .load(imageUrl)
                    .apply(options.override(customViewHolder.image.getWidth(), customViewHolder.image.getHeight() / 2))
                    .into(customViewHolder.image);

            customViewHolder.title.setText("New Receipt");
            customViewHolder.subtitle.setText(((ReceiptModel) currItem).name);
            customViewHolder.desc.setText(((ReceiptModel) currItem).key);
//            customViewHolder.desc2.setText(((ReceiptModel) currItem).link);
        } else if(currItem instanceof RoommateModel) {
            customViewHolder.image.setVisibility(View.GONE);
            customViewHolder.title.setText("New Roommate");
            customViewHolder.subtitle.setText(((RoommateModel) currItem).firstName + ((RoommateModel) currItem).lastName);
            customViewHolder.desc.setText(((RoommateModel) currItem).preferredName);
            customViewHolder.desc2.setText(((RoommateModel) currItem).email);
        } else if(currItem instanceof TransactionModel){
            customViewHolder.image.setVisibility(View.GONE);
            customViewHolder.title.setText("New Transaction: " + ((TransactionModel) currItem).transactionType);
            customViewHolder.subtitle.setText(((TransactionModel) currItem).companyName);
            customViewHolder.desc.setText(formatStringArray(((TransactionModel) currItem).roommateNames));
            customViewHolder.desc2.setText(formatStringArray(((TransactionModel) currItem).items));
        }
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
        return (null != dashboardList ? dashboardList.size() : 0);
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        protected ImageView image;
        protected TextView title;
        protected TextView subtitle;
        protected TextView desc;
        protected TextView desc2;

        public CustomViewHolder(View view) {
            super(view);
            this.image = view.findViewById(R.id.image);
            this.title = (TextView) view.findViewById(R.id.title);
            this.subtitle = (TextView) view.findViewById(R.id.subtitle);
            this.desc = (TextView) view.findViewById(R.id.desc);
            this.desc2 = (TextView) view.findViewById(R.id.desc2);

        }
    }
}