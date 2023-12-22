package com.example.finaltermandroid.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;

import com.example.finaltermandroid.R;

public class HelpFragment extends Fragment {
    ImageButton btnCall;
    private float lastX, lastY;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_help, container, false);
        btnCall = (ImageButton) view.findViewById(R.id.btnCall);


        Animation shakeAnimation = new TranslateAnimation(0, 5, 0, 0);
        shakeAnimation.setInterpolator(new CycleInterpolator(5));
        shakeAnimation.setDuration(500);
        shakeAnimation.setRepeatCount(Animation.INFINITE);
        btnCall.startAnimation(shakeAnimation);

        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePhoneCall(v);

            }
        });
        return view;
    }
    public void makePhoneCall(View view) {
        Uri number = Uri.parse("tel:1900 6469");
        Intent dialIntent = new Intent(Intent.ACTION_DIAL, number);
            startActivity(dialIntent);
            //Log.d("MakePhoneCall", "Calling: " + number);
    }
}
