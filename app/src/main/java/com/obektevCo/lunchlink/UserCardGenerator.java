package com.obektevCo.lunchlink;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
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
    public static void createUserCard(Activity activity, ViewGroup parentLayout, Map<String, String> userRef, String mealPath) {

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
        cardView.setElevation(15);
        cardView.setTranslationZ(6);
        cardView.setBackgroundColor(activity.getColor(R.color.background));
        cardView.setBackground(AppCompatResources.getDrawable(activity, R.drawable.round_shape));

        // Create ImageView for avatar
        ImageView avatarImageView = new ImageView(activity);
        FrameLayout.LayoutParams avatarParams = new FrameLayout.LayoutParams(
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, activity.getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, activity.getResources().getDisplayMetrics())
        );
        avatarParams.gravity = Gravity.START | Gravity.CENTER_VERTICAL;
        avatarParams.setMargins((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, activity.getResources().getDisplayMetrics()), 0, 0, 0);
        avatarImageView.setLayoutParams(avatarParams);
        setAvatarImage(avatarImageView, activity, userRef.get("avatarURL"));
        avatarImageView.setContentDescription(activity.getString(R.string.user_card));
        avatarImageView.setElevation(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, activity.getResources().getDisplayMetrics()));
        avatarImageView.setTranslationZ(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, activity.getResources().getDisplayMetrics()));

        // Create TextView for order name
        TextView userNameTextView = new TextView(activity);
        LinearLayout.LayoutParams orderNameParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        orderNameParams.setMargins((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, activity.getResources().getDisplayMetrics()), 0, 0, 0);
        userNameTextView.setLayoutParams(orderNameParams);
        userNameTextView.setText(userName);
        userNameTextView.setTextColor(activity.getColor(R.color.text_main));
        userNameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        Typeface typeface = ResourcesCompat.getFont(activity, R.font.aldrich);
        userNameTextView.setTypeface(typeface);
        userNameTextView.setGravity(Gravity.CENTER_VERTICAL);

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
        removeImageView.setOnClickListener(v -> showDeleteConfirmationDialog(activity, mealPath, userRef.get("uid")));

        // Add views to CardView
        FrameLayout frameLayout = new FrameLayout(activity);
        frameLayout.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        frameLayout.addView(avatarImageView);
        frameLayout.addView(removeImageView);

        cardView.addView(frameLayout);
        cardView.addView(userNameTextView);

        cardView.setOnClickListener(view -> {
            setUserCardInfoDialog(activity, userRef.get("phoneNumber"), userRef.get("uid"), userRef.get("userName"));
        });

        // Add CardView to parent layout
        //parentLayout.removeView(activity.findViewById(R.id.spacer));
        parentLayout.addView(cardView,0);
    }

    private static void setAvatarImage(ImageView avatarImageView, Activity activity, String photoUrl) {
        // Get the current user from Firebase Authentication
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // Get the user's photo URL from the Firebase User object
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

    private static void showDeleteConfirmationDialog(Activity activity, String mealPath, String uid) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(activity.getString(R.string.are_u_sure_to_delete))
                .setPositiveButton(activity.getString(R.string.delete), (dialog, id) -> {
                    OrderMealUtil.removeOrder(activity, uid, mealPath);
                    dialog.cancel();
                })
                .setNegativeButton(activity.getString(R.string.cancel), (dialog, id) -> dialog.dismiss());
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
        linearLayout.setBackgroundColor(context.getColor(R.color.roundshape));

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(10, 10, 10,15);

        TextView phoneNumberTextView = new TextView(context);
        phoneNumberTextView.setLayoutParams(layoutParams);
        phoneNumberTextView.setTypeface(typeface);
        phoneNumberTextView.setText(String.format("%s: %s", context.getString(R.string.phone_number), phoneNumber));
        phoneNumberTextView.setTextColor(context.getColor(R.color.light_text));
        phoneNumberTextView.setTextSize(18);

        TextView userIDTextView = new TextView(context);
        userIDTextView.setLayoutParams(layoutParams);
        userIDTextView.setTypeface(typeface);
        userIDTextView.setText(String.format("%s: %s", context.getString(R.string.user_id), uid));
        userIDTextView.setTextColor(context.getColor(R.color.light_text));
        userIDTextView.setTextSize(18);

        AppCompatButton coolButton = new AppCompatButton(context);
        coolButton.setText(context.getString(R.string.cool));
        coolButton.setTextSize(18);
        coolButton.setTextColor(context.getColor(R.color.light_text));
        coolButton.setTypeface(typeface, Typeface.BOLD);
        coolButton.setBackgroundDrawable(AppCompatResources.getDrawable(context, R.drawable.round_shape)); // Use AppCompatResources!
        coolButton.setBackgroundColor(context.getColor(R.color.light));
        coolButton.setElevation(5);
        coolButton.setTranslationZ(5);
        coolButton.setGravity(Gravity.CENTER);
        coolButton.setLayoutParams(layoutParams);

        linearLayout.addView(phoneNumberTextView);
        linearLayout.addView(userIDTextView);
        linearLayout.addView(coolButton);

        builder.setView(linearLayout);

        TextView title = new TextView(context);
        title.setText(userName);
        title.setLayoutParams(layoutParams);
        title.setTextColor(context.getColor(R.color.text_main));
        title.setTypeface(typeface, Typeface.BOLD);
        title.setBackgroundColor(context.getColor(R.color.background));
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
