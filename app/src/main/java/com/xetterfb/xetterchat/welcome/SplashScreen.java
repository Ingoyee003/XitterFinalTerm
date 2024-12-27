package com.xetterfb.xetterchat.welcome;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.xetterfb.xetterchat.R;

public class SplashScreen extends AppCompatActivity {

    private RelativeLayout splashLayout;
    private ImageView splashImage;

    //page color change hbe
    private int[] colors;
    private int currentColorIndex = 0;
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        //link the layout elements
        splashLayout = findViewById(R.id.splash_layout);
        splashImage = findViewById(R.id.logo);

        //initialize the colors
        colors = new int[]
                {
                        ContextCompat.getColor(this,R.color.black),
                        ContextCompat.getColor(this,R.color.more_dark_blue),
                        ContextCompat.getColor(this,R.color.dark_blue)

                };

        //start bg change
        startColorChange();

        //start pulsing image
        Animation pulseAnimation = AnimationUtils.loadAnimation(this,R.anim.pulse);
        splashImage.startAnimation(pulseAnimation);

        //set a delay to nav Loginpage
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreen.this, IntroActivity.class));
                finish();
            }
        }, 4000); // Duration of splash screen in milliseconds
    }

    private void startColorChange() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (currentColorIndex < colors.length) {
                    splashLayout.setBackgroundColor(colors[currentColorIndex]);
                    currentColorIndex++;
                    handler.postDelayed(this, 1000); // Change color every second
                }
            }
        }, 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}