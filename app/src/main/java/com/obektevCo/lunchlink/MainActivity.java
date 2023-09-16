package com.obektevCo.lunchlink;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


public class MainActivity extends AppCompatActivity {

    private void goToActivity(Enums activity_index) {

        Intent intent;
        if (activity_index  == Enums.SETTINGSACTIVITY) {
            intent = new Intent(MainActivity.this, SettingsActivity.class);
        } else {
            intent = new Intent(MainActivity.this, MealOrderActivity.class);
            intent.putExtra("meal_number", activity_index.toString());
        }

        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }


    private void setDateAndDay(onDateGot listener) {
        TextView dateObject = findViewById(R.id.date_text);
        LunchLinkUtilities.getDate(getApplicationContext(), date -> {
            changeInternetConnectionIcon(date != null);
            dateObject.setText(String.format("%s: %s", getString(R.string.date), date));
            listener.dateGot();
        });
    }
    private interface onDateGot {
        void dateGot();  // To block ability of user entering to meals when no Internet
    }
    private void setUpMealsButtons() {
        CardView breakfastCard = findViewById(R.id.breakfast_card);
        breakfastCard.setOnClickListener(view -> goToActivity(Enums.BREAKFAST));
        CardView dietCard = findViewById(R.id.diet_card);
        dietCard.setOnClickListener(view -> goToActivity(Enums.DIET));
        CardView brunchCard = findViewById(R.id.brunch_card);
        brunchCard.setOnClickListener(view -> goToActivity(Enums.BRUNCH));
        CardView customCard = findViewById(R.id.custom_card);
        customCard.setOnClickListener(view -> goToActivity(Enums.CUSTOM));
    }

    private void setUpWidgets() {
        // Settings button
        View settingButton = findViewById(R.id.menuSettings);
        settingButton.setOnClickListener(view -> goToActivity(Enums.SETTINGSACTIVITY));

        // Credits when tap on the app's title
        TextView applicationTitle = findViewById(R.id.app_title);
        applicationTitle.setOnClickListener(view -> LunchLinkUtilities.makeToast(getApplicationContext(), getString(R.string.credits)));

        // About button
        View aboutButton = findViewById(R.id.info_button);
        aboutButton.setOnClickListener(item -> {
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
        View themeButton = findViewById(R.id.the_button);
        themeButton.setOnClickListener(view -> {
            Log.d("gsdfdsf101", String.valueOf(getResources().getIdentifier("AppTheme", "style", getPackageName())));
        });
    }

    private void checkUser() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if(firebaseUser == null) { // Check if user signed in firebase
            Intent intent = new Intent(MainActivity.this, RegistrationActivity.class);
            startActivity(intent);
        } else {
            if (firebaseUser.getDisplayName() == null || firebaseUser.getDisplayName().equals("")) {
                Toast.makeText(getApplicationContext(), getString(R.string.name_not_set), Toast.LENGTH_LONG).show();
                startActivity(new Intent(MainActivity.this, SettingsActivity.class).putExtra("registration_flag", 1));
            } else {

                FirebaseFirestore db = FirebaseFirestore.getInstance();

                db.collection("users")
                        .document(firebaseUser.getUid())
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                String cityName = documentSnapshot.getString("cityName");
                                String className = documentSnapshot.getString("className");
                                String schoolName = documentSnapshot.getString("schoolName");

                                if (cityName == null || className == null || schoolName == null) {
                                    if (cityName == null)
                                        LunchLinkUtilities.makeToast(getApplicationContext(), getString(R.string.city_not_set));
                                    if (schoolName == null)
                                        LunchLinkUtilities.makeToast(getApplicationContext(), getString(R.string.school_not_set));
                                    if (className == null)
                                        LunchLinkUtilities.makeToast(getApplicationContext(), getString(R.string.class_not_set));

                                    Intent intent = new Intent(this, RegistrationActivity.class);
                                    intent.putExtra("registration_flag", 2);
                                    startActivity(intent);
                                } else {
                                    LunchLinkUtilities.makeToast(getApplicationContext(), String.format("%s, %s", getString(R.string.welcome), firebaseUser.getDisplayName()));
                                }
                            }
                        })
                        .addOnFailureListener(e -> LunchLinkUtilities.makeToast(getApplicationContext(), getString(R.string.failed_to_connect)));
            }
        }
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

        checkUser(); // check if user signed in, has name, class

        // Setup
        setDateAndDay(this::setUpMealsButtons); // To block ability of user entering to meals when no Internet
        setUpWidgets();

        UserSettings.initialize(getApplicationContext());

    }


    private void changeInternetConnectionIcon(Boolean connected) {

        Toolbar toolbar = findViewById(R.id.top_toolbar);
        MenuItem menuItem = toolbar.getMenu().findItem(R.id.connectionImage);
        menuItem.setOnMenuItemClickListener(menuItem1 -> {
            LunchLinkUtilities.makeToast(getApplicationContext(), getString(R.string.show_your_internet_connection));
            return false;
        });

        if (!connected) {
            LunchLinkUtilities.makeToast(getApplicationContext(), getString(R.string.failed_connected_to_server));

            menuItem.setVisible(true);
            menuItem.setIcon(R.drawable.baseline_signal_wifi_connected_no_internet_4_24);
        }
        else {
            menuItem.setVisible(true);
            menuItem.setIcon(R.drawable.baseline_signal_wifi_4_bar_24);
        }
    }
}