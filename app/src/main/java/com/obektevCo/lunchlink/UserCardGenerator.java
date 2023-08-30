package com.obektevCo.lunchlink;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Map;

public class UserCardGenerator {
    public static void createUserCard(Activity activity, ViewGroup parentLayout, Map<String, String> userRef) {

        String userName = userRef.get("userName");
        // Create CardView
        CardView cardView = new CardView(activity);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 90, activity.getResources().getDisplayMetrics())
        );
        cardParams.setMargins(10, 10, 10, 10);
        cardView.setLayoutParams(cardParams);

        cardView.setRadius(15);
        cardView.setElevation(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, activity.getResources().getDisplayMetrics()));
        cardView.setTranslationZ(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, activity.getResources().getDisplayMetrics()));
        cardView.setBackgroundColor(Color.WHITE);

        // Create ImageView for avatar
        ImageView avatarImageView = new ImageView(activity);
        FrameLayout.LayoutParams avatarParams = new FrameLayout.LayoutParams(
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, activity.getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, activity.getResources().getDisplayMetrics())
        );
        avatarParams.gravity = Gravity.START | Gravity.CENTER_VERTICAL;
        avatarParams.setMargins((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, activity.getResources().getDisplayMetrics()), 0, 0, 0);
        avatarImageView.setLayoutParams(avatarParams);
        setAvatarImage(avatarImageView, activity);
        avatarImageView.setContentDescription(activity.getString(R.string.user_card));
        avatarImageView.setElevation(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, activity.getResources().getDisplayMetrics()));
        avatarImageView.setTranslationZ(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, activity.getResources().getDisplayMetrics()));

        // Create TextView for order name
        TextView orderNameTextView = new TextView(activity);
        LinearLayout.LayoutParams orderNameParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        orderNameParams.setMargins((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, activity.getResources().getDisplayMetrics()), 0, 0, 0);
        orderNameTextView.setLayoutParams(orderNameParams);
        orderNameTextView.setText(userName);
        orderNameTextView.setTextColor(Color.BLACK);
        orderNameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        Typeface typeface = ResourcesCompat.getFont(activity, R.font.aldrich);
        orderNameTextView.setTypeface(typeface);
        orderNameTextView.setGravity(Gravity.CENTER_VERTICAL);

        // Create ImageView for remove button
        ImageView removeImageView = new ImageView(activity);
        FrameLayout.LayoutParams removeParams = new FrameLayout.LayoutParams(
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, activity.getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, activity.getResources().getDisplayMetrics())
        );
        removeParams.gravity = Gravity.END | Gravity.CENTER_VERTICAL;
        removeParams.setMargins(0, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, activity.getResources().getDisplayMetrics()), 0);
        removeImageView.setLayoutParams(removeParams);
        removeImageView.setImageResource(R.drawable.baseline_remove_circle_outline_24);
        removeImageView.setContentDescription(activity.getString(R.string.user_card));
        removeImageView.setElevation(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, activity.getResources().getDisplayMetrics()));
        removeImageView.setTranslationZ(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, activity.getResources().getDisplayMetrics()));
        removeImageView.setOnClickListener(v -> showDeleteConfirmationDialog(activity, cardView));

        // Add views to CardView
        FrameLayout frameLayout = new FrameLayout(activity);
        frameLayout.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        frameLayout.addView(avatarImageView);
        frameLayout.addView(removeImageView);

        cardView.addView(frameLayout);
        cardView.addView(orderNameTextView);

        cardView.setOnClickListener(view -> {
            setUserCardInfoDialog(activity.getApplicationContext(), userRef.get("phoneNumber"), userRef.get("uid"), userRef.get("userName"));
        });

        // Add CardView to parent layout
        //parentLayout.removeView(activity.findViewById(R.id.spacer));
        parentLayout.addView(cardView,0);
    }

    private static void setAvatarImage(ImageView avatarImageView, Activity activity) {
        // Get the current user from Firebase Authentication
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // Get the user's photo URL from the Firebase User object
            Uri photoUrl = currentUser.getPhotoUrl();
            if (photoUrl != null) {
                // Load the user's avatar image into the ImageView using Glide
                Glide.with(activity.getApplicationContext())
                        .load(photoUrl)
                        .apply(RequestOptions.circleCropTransform()) // Optional: Apply circle crop for circular images
                        .into(avatarImageView);
            } else {
                // If the user does not have a photo URL, you can set a default avatar image here
                // For example, load a default image from resources:
                Glide.with(activity.getApplicationContext())
                        .load(R.drawable.user_avatar)
                        .apply(RequestOptions.circleCropTransform())
                        .into(avatarImageView);
            }
        }
    }

    private static void showDeleteConfirmationDialog(Activity activity, CardView cardView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("Are you sure you want to delete this card?")
                .setPositiveButton("Delete", (dialog, id) -> {
                    // Delete the card (remove it from the parent layout)
                    ViewGroup parent = (ViewGroup) cardView.getParent();
                    if (parent != null) {

                        //parent.removeView(cardView); // TODO: CHECK THIS
                    }
                })
                .setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private static void setUserCardInfoDialog(Context context, String phoneNumber, String uid, String userName) {
        Typeface typeface = ResourcesCompat.getFont(context, R.font.aldrich);

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setPadding(15,15,15,15);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setBackground(AppCompatResources.getDrawable(context, R.drawable.round_shape));

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(10, 10, 10,15);

        TextView phoneNumberTextView = new TextView(context);
        phoneNumberTextView.setLayoutParams(layoutParams);
        phoneNumberTextView.setTypeface(typeface);
        phoneNumberTextView.setText(String.format("%s: %s", context.getString(R.string.phone_number), phoneNumber));
        phoneNumberTextView.setTextSize(18);

        TextView userIDTextView = new TextView(context);
        userIDTextView.setLayoutParams(layoutParams);
        userIDTextView.setTypeface(typeface);
        userIDTextView.setText(String.format("%s: %s", context.getString(R.string.user_id), uid));
        userIDTextView.setTextSize(18);

        AppCompatButton coolButton = new AppCompatButton(context);
        coolButton.setText(context.getString(R.string.cool));
        coolButton.setTextSize(18);
        coolButton.setTypeface(typeface, Typeface.BOLD);
        coolButton.setBackgroundDrawable(AppCompatResources.getDrawable(context, R.drawable.round_shape)); // Use AppCompatResources!
        coolButton.setElevation(3);
        coolButton.setTranslationZ(5);
        coolButton.setGravity(Gravity.CENTER);
        coolButton.setLayoutParams(layoutParams);
        coolButton.setBackgroundColor(Color.argb(255,244,244,244));

        linearLayout.addView(phoneNumberTextView);
        linearLayout.addView(userIDTextView);
        linearLayout.addView(coolButton);

        builder.setView(linearLayout);

        TextView title = new TextView(context);
        title.setText(userName);
        title.setTypeface(typeface, Typeface.BOLD);
        title.setTextSize(22);
        title.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        title.setGravity(Gravity.CENTER);
        builder.setCustomTitle(title);

        Dialog dialog = builder.create();
        dialog.show();

        coolButton.setOnClickListener(view -> dialog.cancel());
    }
}
