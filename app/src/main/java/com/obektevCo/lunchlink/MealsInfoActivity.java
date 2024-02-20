package com.obektevCo.lunchlink;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

public class MealsInfoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mealsinfo);

        setupLoadingIcon();
        setupSchoolName();
    }

    private void setupSchoolName() {
        TextView schoolName = findViewById(R.id.schoolTextView);

        loadingImageView.setVisibility(View.VISIBLE);

        FirebaseIntegration.getUserInfo(userInfo -> {
            loadingImageView.setVisibility(View.GONE);

            schoolName.setText(userInfo.get("schoolName"));

        });

    }


    ImageView loadingImageView;
    private void setupLoadingIcon() {
        loadingImageView = findViewById(R.id.loadingIcon);
        AnimatedVectorDrawableCompat animatedVectorDrawableCompat = AnimatedVectorDrawableCompat.create(this, R.drawable.spin_loading);
        loadingImageView.setImageDrawable(animatedVectorDrawableCompat);

        ObjectAnimator rotationAnimator = ObjectAnimator.ofFloat(loadingImageView, "rotation", 0f, 360f);
        rotationAnimator.setDuration(2000); // Set the animation duration in milliseconds
        rotationAnimator.setRepeatCount(ValueAnimator.INFINITE);
        rotationAnimator.start();

        loadingImageView.setVisibility(View.GONE);
    }

}
