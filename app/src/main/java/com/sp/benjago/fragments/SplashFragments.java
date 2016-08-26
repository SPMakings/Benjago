package com.sp.benjago.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.ScaleAnimation;

import com.sp.benjago.LandingPage;
import com.sp.benjago.R;
import com.sp.benjago.SplashScreen;

/**
 * Created by Saikat's Mac on 13/03/16.
 */

public class SplashFragments extends Fragment {


    boolean needToAnimate = false;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_splash_layout, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        view.findViewById(R.id.lets_go).setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent i=new Intent(getActivity(), LandingPage.class);
                startActivity(i);
            }
        });


        try {
            needToAnimate = ((SplashScreen) getActivity()).isFirstTime();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (needToAnimate) {

            scaleView(getActivity().findViewById(R.id.mainbg), 2.0f);
            scaleViewLogo(view.findViewById(R.id.logo_main), 1.7f);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    view.findViewById(R.id.splash_text).setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            view.findViewById(R.id.lets_go).setVisibility(View.VISIBLE);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    view.findViewById(R.id.line).setVisibility(View.VISIBLE);
                                    view.findViewById(R.id.bottom_panel).setVisibility(View.VISIBLE);

                                    ((SplashScreen) getActivity()).setIsFirstTime(false);
                                }
                            }, 600);
                        }
                    }, 600);
                }
            }, 9000);


        } else {
            view.findViewById(R.id.splash_text).setVisibility(View.VISIBLE);
            view.findViewById(R.id.lets_go).setVisibility(View.VISIBLE);
            view.findViewById(R.id.line).setVisibility(View.VISIBLE);
            view.findViewById(R.id.bottom_panel).setVisibility(View.VISIBLE);
        }

        view.findViewById(R.id.sign_in).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ((SplashScreen) getActivity()).openLoginPage();
            }
        });

        view.findViewById(R.id.sign_up).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ((SplashScreen) getActivity()).openSignUpPage();
            }
        });

    }


    public void scaleView(View v, final float startScale) {
        Animation anim = new ScaleAnimation(
                startScale, 1.2f, // Start and end values for the X axis scaling
                startScale, 1.2f, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling
        anim.setFillAfter(true); // Needed to keep the result of the animation
        anim.setInterpolator(new AnticipateOvershootInterpolator());
        anim.setDuration(10000);
        v.startAnimation(anim);
    }

    public void scaleViewLogo(View v, final float startScale) {
        Animation anim = new ScaleAnimation(
                startScale, 1.1f, // Start and end values for the X axis scaling
                startScale, 1.1f, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling
        anim.setFillAfter(true); // Needed to keep the result of the animation
//        anim.setInterpolator(new AnticipateOvershootInterpolator());
        anim.setDuration(8000);
        v.startAnimation(anim);
    }


    //openLoginPage()
}
