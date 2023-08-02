package com.obektevCo.lunchlink;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private void goToActivity(Enums activity_index) {

        Intent intent = null;
        if (activity_index  == Enums.SETTINGSACTIVITY) {
            intent = new Intent(MainActivity.this, SettingsActivity.class);
        } else {
            intent = new Intent(MainActivity.this, MealOrderActivity.class);
            intent.putExtra("meal_number", activity_index.toString());
        }

        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }


    private void setDateAndDay() {
        TextView date_object = findViewById(R.id.date_text);
        date_object.setText(LunchLinkUtilities.getDate(getApplicationContext()));
    }
    private void setUpMealsButtons() {
        CardView breakfast_card = findViewById(R.id.breakfast_card);
        breakfast_card.setOnClickListener(view -> { goToActivity(Enums.BREAKFAST);});
        CardView diet_card = findViewById(R.id.diet_card);
        diet_card.setOnClickListener(view -> { goToActivity(Enums.DIET);});
        CardView brunch_card = findViewById(R.id.brunch_card);
        brunch_card.setOnClickListener(view -> { goToActivity(Enums.BRUNCH);});
        CardView custom_card = findViewById(R.id.custom_card);
        custom_card.setOnClickListener(view -> { goToActivity(Enums.CUSTOM);});
    }

    private void setUpWidgets() {
        // Settings button
        View setting_button = findViewById(R.id.menuSettings);
        setting_button.setOnClickListener(view -> { goToActivity(Enums.SETTINGSACTIVITY);});

        // Credits when tap on the app's title
        TextView application_title = findViewById(R.id.app_title);
        application_title.setOnClickListener(view -> LunchLinkUtilities.makeToast(getApplicationContext(), getString(R.string.credits)));

        // About button
        View about_button = findViewById(R.id.info_button);
        about_button.setOnClickListener(item -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            Typeface typeface = ResourcesCompat.getFont(this, R.font.aldrich);

            TextView myMsg = new TextView(this);
            TextView myTitle = new TextView(this);

            myMsg.setText(getString(R.string.main_activity_about_text));
            myTitle.setText(R.string.about_and_credits);

            myMsg.setGravity(Gravity.CENTER_HORIZONTAL);
            myTitle.setGravity(Gravity.CENTER_HORIZONTAL);

            myMsg.setTypeface(typeface);
            myTitle.setTypeface(typeface, Typeface.BOLD_ITALIC);

            myTitle.setTextSize(30);
            builder.setCustomTitle(myTitle)
                    .setView(myMsg)
                    .setNegativeButton("Cool!", (dialog, id) -> dialog.dismiss());
            AlertDialog dialog = builder.create();

            dialog.show();
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getIntent().getBooleanExtra("SIGN_OUT", false)) {
            FirebaseAuth.getInstance().signOut();
            LunchLinkUtilities.makeToast(getApplicationContext(), getString(R.string.signed_out));
            finish();
        }

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if(firebaseUser == null) { // Check if user signed in firebase
            Intent intent = new Intent(this, RegistrationActivity.class);
            startActivity(intent);
        } else {
            LunchLinkUtilities.makeToast(getApplicationContext(), String.format("%s, %s", getString(R.string.welcome), firebaseUser.getDisplayName()));
        }


        // Setup
        setDateAndDay();
        setUpMealsButtons();
        setUpWidgets();
        UserSettings.initialize(getApplicationContext());

    }
}