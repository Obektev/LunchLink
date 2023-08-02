package com.obektevCo.lunchlink;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.obektevCo.lunchlink.R;

public class UserCardGenerator {
    public static void createUserCard(Activity activity, ViewGroup parentLayout, String account_name) {

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
        orderNameTextView.setText(account_name);
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
        removeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog(activity, cardView);
            }
        });

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

        // Add CardView to parent layout
        //parentLayout.removeView(activity.findViewById(R.id.spacer));
        parentLayout.addView(cardView,0);
    }

    private static void setAvatarImage(ImageView avatarImageView, Context context) {
        // Get the current user from Firebase Authentication
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // Get the user's photo URL from the Firebase User object
            Uri photoUrl = currentUser.getPhotoUrl();
            Log.w("AVATAR AVATAR", photoUrl.toString());
            if (photoUrl != null) {
                // Load the user's avatar image into the ImageView using Glide
                Glide.with(context)
                        .load(photoUrl)
                        .apply(RequestOptions.circleCropTransform()) // Optional: Apply circle crop for circular images
                        .into(avatarImageView);
            } else {
                // If the user does not have a photo URL, you can set a default avatar image here
                // For example, load a default image from resources:
                Glide.with(context)
                        .load(R.drawable.user_avatar)
                        .apply(RequestOptions.circleCropTransform())
                        .into(avatarImageView);
            }
        }
    }

    private static void showDeleteConfirmationDialog(Activity activity, CardView cardView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("Are you sure you want to delete this card?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Delete the card (remove it from the parent layout)
                        ViewGroup parent = (ViewGroup) cardView.getParent();
                        if (parent != null) {
                            parent.removeView(cardView);
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
