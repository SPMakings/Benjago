package com.sp.benjago;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import com.sp.benjago.allinterface.onUrlResponceListener;
import com.sp.benjago.fragments.MeetingFragments;
import com.sp.benjago.helper.Logger;
import com.sp.benjago.helper.OkHttepest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

public class GateWayActivity extends AppCompatActivity {


    ViewPager meetingPager = null;
    TabLayout tabLayout;
    final String TAG = "StudyMaterials";
    LinkedList<JSONArray> fragmentData = null;
    ProgressBar pBAR = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gate_way);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fragmentData = new LinkedList<JSONArray>();

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);


        pBAR = (ProgressBar) findViewById(R.id.progressBar);
        meetingPager = (ViewPager) findViewById(R.id.meeting_pager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);


        //====================JSON Management

        loadData();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            if (fm.getFragments() != null) {
                fm.getFragments().clear();
            }
        }

        @Override
        public Fragment getItem(int position) {
            return MeetingFragments.getInstance(fragmentData.get(position));
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return "To Be Confirmed";
            } else if (position == 0) {
                return "Expected";
            } else {
                return "Waiting For Rating";
            }
        }
    }


    public void retryMe() {
        loadData();
    }


    private void loadData() {
        pBAR.setVisibility(View.VISIBLE);
        new OkHttepest().getAllMeetings(new onUrlResponceListener() {
            @Override
            public void onSuccess(final String statusCode, final String result) {
                Logger.showMessage("SaikatsTest", "statusCode : " + statusCode + "\n result : " + result);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pBAR.setVisibility(View.GONE);

                        if (statusCode.equals("200")) {
                            try {
                                JSONObject mainObject = new JSONObject(result).getJSONObject("lists_of_meetings");
                                JSONArray innerArray = mainObject.getJSONArray("to-be-confirmed");
                                fragmentData.add(innerArray);

                                innerArray = mainObject.getJSONArray("expected");
                                fragmentData.add(innerArray);

                                innerArray = mainObject.getJSONArray("waiting-for-rating");
                                fragmentData.add(innerArray);

                                meetingPager.setAdapter(new SectionsPagerAdapter(getSupportFragmentManager()));
                                tabLayout.setupWithViewPager(meetingPager);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Logger.showMessage("SaikatsTest", "error : " + result);
                        }
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pBAR.setVisibility(View.GONE);
                    }
                });
                Logger.showMessage("SaikatsTest", "error : " + error);

            }
        });
    }

}
