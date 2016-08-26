package com.sp.benjago.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sp.benjago.LandingPage;
import com.sp.benjago.R;
import com.sp.benjago.customview.RobotoLight;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Saikat's Mac on 18/05/16.
 */


public class MeetingAdapter extends RecyclerView.Adapter<MeetingAdapter.ViewHolder> {


    JSONArray mainData = null;
    Context mContext = null;

    public MeetingAdapter(final Context mContext, final JSONArray mainData) {
        super();
        this.mContext = mContext;
        this.mainData = mainData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.items_meeting_normal, null));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            JSONObject innerObject = mainData.getJSONObject(position);
            holder.profileName.setText("" + innerObject.getJSONObject("monitor").getString("first_name") + " " + innerObject.getJSONObject("monitor").getString("last_name"));
            holder.meetingDateTime.setText("From " + innerObject.getString("begin") + " to " + innerObject.getString("end"));
            holder.meetingPlace.setText("" + innerObject.getJSONObject("address").getString("text"));
            holder.meetingPrice.setText("" + innerObject.getString("total_price") + " $");
            holder.meetingDuration.setText("" + innerObject.getString("duration_hours") + " Hrs");
            if (innerObject.getBoolean("is_accepted")) {
                holder.approvalStatus.setVisibility(View.VISIBLE);
            } else {
                holder.approvalStatus.setVisibility(View.GONE);
            }


            holder.mainView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                    Intent i = new Intent(mContext, LandingPage.class);
                    mContext.startActivity(i);

                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public int getItemCount() {
        return mainData.length();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        RobotoLight profileName, meetingDateTime, meetingDuration, meetingPlace, meetingPrice;
        ImageView profileImage, approvalStatus;
        View mainView = null;

        public ViewHolder(View itemView) {
            super(itemView);
            this.mainView = itemView;
            profileName = (RobotoLight) itemView.findViewById(R.id.prof_name);
            meetingDateTime = (RobotoLight) itemView.findViewById(R.id.timing);
            meetingDuration = (RobotoLight) itemView.findViewById(R.id.duration);
            meetingPlace = (RobotoLight) itemView.findViewById(R.id.meeting_place);
            meetingPrice = (RobotoLight) itemView.findViewById(R.id.pricing);

            profileImage = (ImageView) itemView.findViewById(R.id.prof_pic);
            approvalStatus = (ImageView) itemView.findViewById(R.id.approval);

        }
    }
}
