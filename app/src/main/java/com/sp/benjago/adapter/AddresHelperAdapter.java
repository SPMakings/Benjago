package com.sp.benjago.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sp.benjago.R;
import com.sp.benjago.customview.RobotoLight;
import com.sp.benjago.helper.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Saikat's Mac on 21/01/16.
 */
public class AddresHelperAdapter extends RecyclerView.Adapter<AddresHelperAdapter.ViewHolder> {

    Context mContext = null;
    JSONArray mainJArray;
    final String TAG = "AddresHelper";
    onClickAdapterItem callback = null;

    public AddresHelperAdapter(Context mContext, JSONArray mainJArray, onClickAdapterItem callback) {
        this.mContext = mContext;
        this.mainJArray = mainJArray;
        this.callback = callback;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.items_address_helper, null);
        return (new ViewHolder(view));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        try {
            final JSONObject jaObjec_ = mainJArray.getJSONObject(position);
            holder.taskName.setText(jaObjec_.getString("formatted_address"));
            holder.taskName.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    try {
                        JSONObject innerGeo = jaObjec_.getJSONObject("geometry").getJSONObject("location");
                        callback.onClick(innerGeo, jaObjec_.getString("formatted_address"), jaObjec_.getString("place_id"));
                        //Logger.showMessage(TAG, innerGeo.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mainJArray.length();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        RobotoLight taskName;

        public ViewHolder(View itemView) {
            super(itemView);
            this.taskName = (RobotoLight) itemView.findViewById(R.id.address);
        }
    }


    public interface onClickAdapterItem {
        public abstract void onClick(JSONObject resultLocation, String address, String placeID);
    }
}
