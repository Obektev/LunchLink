package com.obektevCo.lunchlink;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

;

public class SettingsActivity extends AppCompatActivity {

    private interface TextDialogListener { // An interface to make listener
        void onTextEntered(String text);
    }

    private void showTextDialogueWindow(TextDialogListener listener) {
        // Set up builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.enter_text));

        // Set up the input
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String result = input.getText().toString();
            listener.onTextEntered(result); // Notify the listener with the entered text
        });

        builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setUpWidgets() {
        AppCompatButton sign_out_button = findViewById(R.id.sign_out_button);
        sign_out_button.setOnClickListener(view -> {
            // Start main activity to close all application
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("SIGN_OUT", true);
            startActivity(intent);
        });

        AppCompatButton change_avatar_button = findViewById(R.id.change_avatar_button);
        change_avatar_button.setOnClickListener(view -> UserAvatarChanging.chooseImage(SettingsActivity.this));

        AppCompatButton change_username_button = findViewById(R.id.change_username_button);
        change_username_button.setOnClickListener(view -> {
            showNameInputDialog();
        });
    }

    private void changeUserName(String name) {
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

        // Set up the input
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        builder.setView(input);
        // Set up the buttons
        builder.setPositiveButton("OK", (dialog, which) -> {
            String name = input.getText().toString();
            // Do something with the entered name
            changeUserName(name);
        });
        builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.cancel());

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Set up all widgets
        setUpWidgets();
    }
}
