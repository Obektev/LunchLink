package com.obektevCo.lunchlink;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.res.ResourcesCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;


public class SettingsActivity extends AppCompatActivity {

    Typeface typeface = null; // Initialize during onCreate

    private void setUpWidgets() {
        AppCompatButton signOutButton = findViewById(R.id.sign_out_button);
        signOutButton.setOnClickListener(view -> {
            // Start main activity to close all application
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("SIGN_OUT", true);
            startActivity(intent);
        });

        AppCompatButton changeAvatarButton = findViewById(R.id.change_avatar_button);
        changeAvatarButton.setOnClickListener(view -> UserAvatarChanging.chooseImage(SettingsActivity.this));

        AppCompatButton changeUsernameButton = findViewById(R.id.change_username_button);
        changeUsernameButton.setOnClickListener(view -> showNameInputDialog());

        AppCompatButton getUserInfoButton = findViewById(R.id.get_userinfo_button);
        getUserInfoButton.setOnClickListener(view -> getUserInfoMenu());
    }

    private void getUserInfoMenu() {
        FirebaseIntegration.getUserInfo(userInfo -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setPadding(15,15,15,15);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setBackground(AppCompatResources.getDrawable(getApplicationContext(), R.drawable.round_shape));

            TextView usernameTextView = new TextView(this);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(10, 10, 10,15);

            usernameTextView.setLayoutParams(layoutParams);
            usernameTextView.setTypeface(typeface);
            usernameTextView.setText(String.format("%s: %s", getString(R.string.username), userInfo.get("userName")));
            usernameTextView.setTextSize(18);

            TextView cityTextView = new TextView(this);
            cityTextView.setLayoutParams(layoutParams);
            cityTextView.setTypeface(typeface);
            cityTextView.setText(String.format("%s: %s", getString(R.string.city), userInfo.get("cityName")));
            cityTextView.setTextSize(18);

            TextView schoolTextView = new TextView(this);
            schoolTextView.setLayoutParams(layoutParams);
            schoolTextView.setTypeface(typeface);
            schoolTextView.setText(String.format("%s: %s", getString(R.string.school), userInfo.get("schoolName")));
            schoolTextView.setTextSize(18);

            TextView classTextView = new TextView(this);
            classTextView.setLayoutParams(layoutParams);
            classTextView.setTypeface(typeface);
            classTextView.setText(String.format("%s: %s", getString(R.string.class_), userInfo.get("className")));
            classTextView.setTextSize(18);

            AppCompatButton coolButton = new AppCompatButton(this);
            coolButton.setText(getString(R.string.cool));
            coolButton.setTextSize(18);
            coolButton.setTypeface(typeface, Typeface.BOLD);
            coolButton.setBackgroundDrawable(AppCompatResources.getDrawable(getApplicationContext(), R.drawable.round_shape)); // Use AppCompatResources!
            coolButton.setElevation(3);
            coolButton.setTranslationZ(5);
            coolButton.setGravity(Gravity.CENTER);
            coolButton.setLayoutParams(layoutParams);
            coolButton.setBackgroundColor(Color.argb(255,244,244,244));

            linearLayout.addView(usernameTextView);
            linearLayout.addView(cityTextView);
            linearLayout.addView(schoolTextView);
            linearLayout.addView(classTextView);
            linearLayout.addView(coolButton);

            builder.setView(linearLayout);

            TextView title = new TextView(this);
            title.setText(getString(R.string.userinfo));
            title.setTypeface(typeface, Typeface.BOLD);
            title.setTextSize(20);
            title.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT));
            title.setGravity(Gravity.CENTER);
            builder.setCustomTitle(title);

            Dialog dialog = builder.create();
            dialog.show();

            coolButton.setOnClickListener(view -> dialog.cancel());
       });
    }

    private void changeUserName(String name) {
        if (name.equals("")) {
            LunchLinkUtilities.makeToast(getApplicationContext(), getString(R.string.name_cannot_be_null));
            return;
        }
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();

        assert user != null;
        user.updateProfile(profileUpdates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        LunchLinkUtilities.makeToast(getApplicationContext(), getString(R.string.name_changed));
                    }
                });
    }

    private void showNameInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setBackground(AppCompatResources.getDrawable(getApplicationContext(), R.drawable.round_shape));
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(15,15,15,15);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(10, 10, 10,15);

        LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        buttonLayoutParams.setMargins(25,5,25,5);

        LinearLayout buttonLayout = new LinearLayout(this);
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonLayout.setGravity(Gravity.CENTER);
        buttonLayout.setPadding(15,15,15,15);

        AppCompatButton continueButton = new AppCompatButton(this);
        continueButton.setText(getString(R.string.continue_));
        continueButton.setTextSize(18);
        continueButton.setTypeface(typeface, Typeface.BOLD);
        continueButton.setBackgroundDrawable(AppCompatResources.getDrawable(getApplicationContext(), R.drawable.round_shape)); // Use AppCompatResources!
        continueButton.setElevation(5);
        continueButton.setTranslationZ(9);
        continueButton.setGravity(Gravity.CENTER);
        continueButton.setLayoutParams(buttonLayoutParams);
        continueButton.setPadding(15,15,15,15);
        continueButton.setBackgroundColor(Color.argb(255,244,244,244));

        AppCompatButton cancelButton = new AppCompatButton(this);
        cancelButton.setText(getString(R.string.cancel));
        cancelButton.setTextSize(18);
        cancelButton.setTypeface(typeface, Typeface.BOLD);
        cancelButton.setBackgroundDrawable(AppCompatResources.getDrawable(getApplicationContext(), R.drawable.round_shape)); // Use AppCompatResources!
        cancelButton.setElevation(5);
        cancelButton.setTranslationZ(9);
        cancelButton.setGravity(Gravity.CENTER);
        cancelButton.setLayoutParams(buttonLayoutParams);
        cancelButton.setPadding(15,15,15,15);
        cancelButton.setBackgroundColor(Color.argb(255,244,244,244));

        buttonLayout.addView(cancelButton);
        buttonLayout.addView(continueButton);

        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        linearLayout.addView(input);
        linearLayout.addView(buttonLayout);

        TextView title = new TextView(this);
        title.setGravity(Gravity.CENTER);
        title.setText(getString(R.string.enter_name));
        title.setTypeface(typeface, Typeface.BOLD);
        title.setTextSize(20);

        builder.setCustomTitle(title);
        builder.setView(linearLayout);

        AlertDialog dialog = builder.create();

        continueButton.setOnClickListener(view -> {
            String name = input.getText().toString();
            changeUserName(name);
        });
        cancelButton.setOnClickListener(view -> dialog.cancel());

        dialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        typeface = ResourcesCompat.getFont(this, R.font.aldrich);

        setContentView(R.layout.activity_settings);

        // Set up all widgets
        setUpWidgets();
    }
}
