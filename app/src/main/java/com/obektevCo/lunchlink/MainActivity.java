package com.obektevCo.lunchlink;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ortiz.touchview.TouchImageView;

import gun0912.tedimagepicker.builder.TedImagePicker;


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
        CardView lunchCard = findViewById(R.id.lunch_card);
        lunchCard.setOnClickListener(view -> goToActivity(Enums.LUNCH));
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
        aboutButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, MealsInfoActivity.class);
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        });
        /*
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
        */
        SharedPreferences sharedPreferences = getSharedPreferences("MODE", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        MenuItem themeButton = ((Toolbar) findViewById(R.id.top_toolbar)).getMenu().findItem(R.id.the_button);
        themeButton.setOnMenuItemClickListener(menuItem -> {
            boolean night = sharedPreferences.getBoolean("theme_mode", true);
            if (night) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                editor.putBoolean("theme_mode", false);
                themeButton.setIcon(R.drawable.baseline_wb_sunny_24);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                editor.putBoolean("theme_mode", true);
                themeButton.setIcon(R.drawable.baseline_dark_mode_24);
            }
            editor.apply();
            return false;
        });

        FloatingActionButton menuButton = findViewById(R.id.menu_floating_button);
        menuButton.setOnClickListener(view -> {
            openMenuDialog();
        });
    }

    private void checkUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user == null) { // Check if user signed in firebase
            Intent intent = new Intent(MainActivity.this, RegistrationActivity.class);
            startActivity(intent);
        } else {
            if (user.getDisplayName() == null || user.getDisplayName().equals("")) {
                Toast.makeText(getApplicationContext(), getString(R.string.name_not_set), Toast.LENGTH_LONG).show();
                startActivity(new Intent(MainActivity.this, SettingsActivity.class).putExtra("registration_flag", 1));
            } /*else {

                FirebaseFirestore db = FirebaseFirestore.getInstance();

                db.collection("users")
                        .document(user.getUid())
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                Log.d("168 168", String.valueOf(documentSnapshot));
                                String cityName = documentSnapshot.getString("cityName");
                                String className = documentSnapshot.getString("className");
                                String schoolName = documentSnapshot.getString("schoolName");
                                Log.d("User Info 171", cityName + ' ' + schoolName + ' ' + className);

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
                                }
                            }
                        })
                        .addOnFailureListener(e -> LunchLinkUtilities.makeToast(getApplicationContext(), getString(R.string.failed_to_connect)));
            }
*/
        }
    }

    private void openMenuDialog() {
        loadingIcon.setVisibility(View.VISIBLE);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        Typeface typeface = ResourcesCompat.getFont(this, R.font.aldrich);

        TextView title = new TextView(getApplicationContext());
        title.setTypeface(typeface, Typeface.BOLD);
        title.setTextColor(getColor(R.color.text_main));
        title.setTextSize(20);
        title.setText(getString(R.string.menu));
        title.setBackground(AppCompatResources.getDrawable(getApplicationContext(), R.drawable.round_shape));
        title.setBackgroundColor(getColor(R.color.background));
        title.setGravity(Gravity.CENTER);
        builder.setCustomTitle(title);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics()),
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        LinearLayout layout = new LinearLayout(getApplicationContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackground(AppCompatResources.getDrawable(getApplicationContext(), R.drawable.round_shape));
        layout.setBackgroundColor(getColor(R.color.background));
        layout.setLayoutParams(layoutParams);
        layout.setPadding(20, 20, 20, 20);

        FirebaseIntegration.getMenuFromDB(getApplicationContext(), image_uri -> {
            loadingIcon.setVisibility(View.GONE);
            if (image_uri == null) {
                // Handle the case when there is no menu image
                TextView noMenuTextView = new TextView(getApplicationContext());
                noMenuTextView.setTypeface(typeface, Typeface.BOLD);
                noMenuTextView.setTextColor(getColor(R.color.light_text));
                noMenuTextView.setTextSize(15);
                noMenuTextView.setText(getString(R.string.no_menu_yet));
                noMenuTextView.setGravity(View.TEXT_ALIGNMENT_CENTER);

                AppCompatButton uploadButton = new AppCompatButton(this);

                ConstraintLayout.LayoutParams buttonLayoutParams = new ConstraintLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics())
                );
                buttonLayoutParams.setMargins(15, 10, 15, 10);
                uploadButton.setBackground(AppCompatResources.getDrawable(getApplicationContext(), R.drawable.round_shape));
                uploadButton.setBackgroundColor(getColor(R.color.button_color));
                uploadButton.setLayoutParams(buttonLayoutParams);
                uploadButton.setElevation(5);
                uploadButton.setTranslationZ(15);

                uploadButton.setTextColor(getColor(R.color.semi_text));
                uploadButton.setText(R.string.upload_menu_image);
                uploadButton.setTypeface(typeface);

                layout.addView(noMenuTextView);
                layout.addView(uploadButton);
                builder.setView(layout);
                Dialog dialog = builder.create();
                uploadButton.setOnClickListener(view -> {
                    TedImagePicker.with(MainActivity.this)
                            .start(uri -> {
                                loadingIcon.setVisibility(View.GONE);
                                loadingIcon.setVisibility(View.VISIBLE);
                                LunchLinkUtilities.uploadMenuImage(uri, MainActivity.this, () -> {
                                    dialog.cancel();
                                });
                            });
                });
                dialog.show();
            } else {
                TouchImageView menuImageView = new TouchImageView(getApplicationContext());
                // Load the image into the ImageView using Glide
                loadingIcon.setVisibility(View.VISIBLE);
                Glide.with(this)
                        .asDrawable()
                        .load(image_uri.toString()) // Convert URL to string
                        .into(new CustomTarget<Drawable>() {
                            @Override
                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                menuImageView.setImageDrawable(resource);
                                loadingIcon.setVisibility(View.GONE);
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                                loadingIcon.setVisibility(View.GONE);
                            }
                        });

                AppCompatButton deleteButton = new AppCompatButton(getApplicationContext());
                deleteButton.setTranslationZ(5);
                deleteButton.setElevation(5);

                deleteButton.setPadding(0,10,0,10);
                deleteButton.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                deleteButton.setBackground(AppCompatResources.getDrawable(getApplicationContext(), R.drawable.round_shape));
                deleteButton.setBackgroundColor(getColor(R.color.button_color));
                deleteButton.setText(getString(R.string.delete_menu_image));
                deleteButton.setTypeface(typeface);
                deleteButton.setTextColor(getColor(R.color.light_text));
                deleteButton.setTextSize(25);


                layout.addView(menuImageView);
                layout.addView(deleteButton);
                builder.setView(layout);

                Dialog dialog = builder.create();
                deleteButton.setOnClickListener(view ->{
                    showDeletingMenuDialog(dialog);
                });
                dialog.show();
            }
        });
    }

    private void showDeletingMenuDialog(Dialog mainDialog) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(getString(R.string.are_u_sure_to_delete))
                .setPositiveButton(getString(R.string.delete), (dialog, id) -> {
                    loadingIcon.setVisibility(View.VISIBLE);
                    FirebaseIntegration.deleteMenuFromDB(MainActivity.this, () -> {
                        loadingIcon.setVisibility(View.GONE);
                        dialog.cancel();
                        mainDialog.cancel();
                    });
                })
                .setNegativeButton(getString(R.string.cancel), (dialog, id) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //Intent intent = new Intent(this, FirestorePopulationActivity.class);
        //startActivity(intent);


        if (getIntent().getBooleanExtra("SIGN_OUT", false)) {
            FirebaseAuth.getInstance().signOut();
            LunchLinkUtilities.makeToast(getApplicationContext(), getString(R.string.signed_out));
            finish();
        }

        checkUser(); // check if user signed in, has name, class
        setUserTheme();

        // Setup
        setDateAndDay(this::setUpMealsButtons); // To block ability of user entering to meals when no Internet
        setUpWidgets();
        setUpLoadingIcon();

        UserSettings.initialize(getApplicationContext());

    }

    ImageView loadingIcon;
    private void setUpLoadingIcon() {
        loadingIcon = findViewById(R.id.loadingIcon);
        loadingIcon.setVisibility(View.GONE);

        AnimatedVectorDrawableCompat animatedVectorDrawableCompat = AnimatedVectorDrawableCompat.create(this, R.drawable.spin_loading);
        loadingIcon.setImageDrawable(animatedVectorDrawableCompat);

        ObjectAnimator rotationAnimator = ObjectAnimator.ofFloat(loadingIcon, "rotation", 0f, 360f);
        rotationAnimator.setDuration(2000); // Set the animation duration in milliseconds
        rotationAnimator.setRepeatCount(ValueAnimator.INFINITE);
        rotationAnimator.start();
    }

    private void setUserTheme() {
        SharedPreferences sharedPreferences = getSharedPreferences("MODE", MODE_PRIVATE);
        boolean night = sharedPreferences.getBoolean("theme_mode", true);
        MenuItem themeButton = ((Toolbar) findViewById(R.id.top_toolbar)).getMenu().findItem(R.id.the_button);
        if (night) {
            themeButton.setIcon(R.drawable.baseline_dark_mode_24);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            themeButton.setIcon(R.drawable.baseline_wb_sunny_24);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            LunchLinkUtilities.makeToast(getApplicationContext(), String.format("%s, %s", getString(R.string.welcome), user.getDisplayName()));
        }
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