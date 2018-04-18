package com.poop.rumi.rumi;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.poop.rumi.rumi.models.UserModel;

import java.util.List;

public class RoommateRecycleViewAdapter extends RecyclerView.Adapter<RoommateRecycleViewAdapter.CustomViewHolder> {
    private List<UserModel> roommateList;
    private Context mContext;

    public RoommateRecycleViewAdapter(Context context, List<UserModel> roommateList) {
        this.roommateList = roommateList;
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
        final UserModel currRoommate = roommateList.get(i);

        customViewHolder.title.setText(currRoommate.name);
        customViewHolder.subtitle.setText(currRoommate.email);

        if(currRoommate.username != null && !currRoommate.username.isEmpty())
            customViewHolder.desc.setText("@" + currRoommate.username);
    }

    @Override
    public int getItemCount() {
        return (null != roommateList ? roommateList.size() : 0);
    }

    // Inherited from Dashboard, roommates list only uses title, subtitle
    class CustomViewHolder extends RecyclerView.ViewHolder {
        protected ImageView image;
        protected TextView title;
        protected TextView subtitle;
        protected TextView desc;
        protected TextView desc2;

        public CustomViewHolder(View view) {
            super(view);
            this.image = view.findViewById(R.id.image);
            this.image.setVisibility(View.GONE);
            this.title = (TextView) view.findViewById(R.id.title);
            this.subtitle = (TextView) view.findViewById(R.id.subtitle);
            this.desc = (TextView) view.findViewById(R.id.desc);
            this.desc2 = (TextView) view.findViewById(R.id.desc2);

        }
    }
}