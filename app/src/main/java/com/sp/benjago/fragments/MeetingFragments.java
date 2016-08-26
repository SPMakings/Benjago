package com.sp.benjago.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sp.benjago.GateWayActivity;
import com.sp.benjago.R;
import com.sp.benjago.adapter.MeetingAdapter;

import org.json.JSONArray;

/**
 * Created by apple on 17/05/16.
 */

public class MeetingFragments extends Fragment {

    private JSONArray jsonArray = null;

    RecyclerView meetingList = null;
    View error = null;

    public static MeetingFragments getInstance(final JSONArray jsonArray) {
        MeetingFragments frag_ = new MeetingFragments();
        frag_.jsonArray = jsonArray;
        return frag_;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragments_meetinglist, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        error = view.findViewById(R.id.error_view);
        meetingList = (RecyclerView) view.findViewById(R.id.meetinglist);
        meetingList.setHasFixedSize(true);
        meetingList.setLayoutManager(new LinearLayoutManager(getActivity()));


        view.findViewById(R.id.retry).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ((GateWayActivity) getActivity()).retryMe();
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (jsonArray.length() > 0) {
            error.setVisibility(View.GONE);
            meetingList.setAdapter(new MeetingAdapter(getActivity(), jsonArray));
        } else {
            error.setVisibility(View.VISIBLE);
        }

    }
}
