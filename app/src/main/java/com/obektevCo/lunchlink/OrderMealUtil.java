package com.obektevCo.lunchlink;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.res.ResourcesCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderMealUtil {

    public static void createOrder(Activity activity, String date, String mealName, ViewGroup parentLayout) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        assert user != null;
        db.collection("users")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String cityName = documentSnapshot.getString("cityName");
                        String className = documentSnapshot.getString("className");
                        String schoolName = documentSnapshot.getString("schoolName");
                        //String avatarURL = documentSnapshot.getString("avatarURL");
                        //String uid = user.getUid();
                        //String phoneNumber = user.getPhoneNumber();
                        String userName = user.getDisplayName();

                        /*
                        Map<String, String> userRef = new HashMap<>(); // Used for userCardGenerator
                        userRef.put("userName", userName);
                        userRef.put("uid", uid);
                        userRef.put("phoneNumber", phoneNumber);
                        userRef.put("avatarURL", avatarURL);
                        */

                        if (date == null) {
                            LunchLinkUtilities.makeToast(activity, activity.getString(R.string.unable_to_get_date));
                            LunchLinkUtilities.getDate(activity, date1 -> LunchLinkUtilities.makeToast(activity, activity.getString(R.string.date_got)));
                        } else {

                            String mealPath = String.format("cities/%s/schools/%s/classes/%s/%s/%s", cityName, schoolName, className, date, mealName);

                            Map<String, Object> putData = new HashMap<>();
                            Map<String, String> userInfo = new HashMap<>();
                            userInfo.put(user.getUid(), userName);
                            putData.put("user_names", FieldValue.arrayUnion(userInfo));

                            ImageView loadingIcon = activity.findViewById(R.id.loadingIcon);
                            Log.w("mealPath", mealPath);
                            db.document(mealPath).get()
                                    .addOnSuccessListener(documentSnapshot1 -> {
                                       if (documentSnapshot1.exists()) {
                                           if (documentSnapshot1.get("user_names") != null) { // there was a problem: if no order - update doesn't work, b.s. we need to put data first.

                                               // TODO: check if user already orders meal:
                                               List<Map<String, String>> previousOrders = (List<Map<String, String>>) documentSnapshot1.get("user_names");
                                               for (Map<String, String> user_ : previousOrders) {
                                                   if (user_.containsKey(user.getUid())) {
                                                       showAlreadyOrderedDialog(activity);
                                                       loadingIcon.setVisibility(View.GONE);
                                                       return;
                                                   }
                                               }

                                               db.document(mealPath).update(putData)
                                                       .addOnSuccessListener(aVoid -> {
                                                           loadingIcon.setVisibility(View.GONE);
                                                           LunchLinkUtilities.makeToast(activity, activity.getString(R.string.order_created));
                                                           //UserCardGenerator.createUserCard(activity, parentLayout, userName);
                                                       })
                                                       .addOnFailureListener(e -> {
                                                           LunchLinkUtilities.makeToast(activity, activity.getString(R.string.something_went_wrong));
                                                           loadingIcon.setVisibility(View.GONE);
                                                       });
                                           } else {
                                               db.document(mealPath).set(putData)
                                                       .addOnSuccessListener(aVoid -> {
                                                           loadingIcon.setVisibility(View.GONE);
                                                           LunchLinkUtilities.makeToast(activity, activity.getString(R.string.order_created));
                                                           //UserCardGenerator.createUserCard(activity, parentLayout, userRef);
                                                       })
                                                       .addOnFailureListener(e -> {
                                                           LunchLinkUtilities.makeToast(activity, activity.getString(R.string.something_went_wrong));
                                                           loadingIcon.setVisibility(View.GONE);
                                                       });
                                           }
                                       } else {
                                           db.document(mealPath).set(putData)
                                                   .addOnSuccessListener(aVoid -> {
                                                       loadingIcon.setVisibility(View.GONE);
                                                       LunchLinkUtilities.makeToast(activity, activity.getString(R.string.order_created));
                                                       //UserCardGenerator.createUserCard(activity, parentLayout, userRef);
                                                   })
                                                   .addOnFailureListener(e -> {
                                                       LunchLinkUtilities.makeToast(activity, activity.getString(R.string.something_went_wrong));
                                                       loadingIcon.setVisibility(View.GONE);
                                                   });
                                           Log.w("Document", "Document doesn't exists");
                                       }
                            });
                        }
                    }
                });
    }

    private static void showAlreadyOrderedDialog(Activity activity) {
        Typeface typeface = ResourcesCompat.getFont(activity, R.font.aldrich);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LinearLayout linearLayout = new LinearLayout(activity);
        linearLayout.setBackground(AppCompatResources.getDrawable(activity, R.drawable.round_shape));
        linearLayout.setBackgroundColor(Color.WHITE); // TODO:
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(15,15,15,15);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(10, 10, 10,15);

        LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        buttonLayoutParams.setMargins(25,5,25,5);

        LinearLayout buttonLayout = new LinearLayout(activity);
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonLayout.setGravity(Gravity.CENTER);
        buttonLayout.setPadding(15,15,15,15);

        AppCompatButton iwantButton = new AppCompatButton(activity); // order to another person button
        iwantButton.setText(activity.getString(R.string.i_want));
        iwantButton.setTextColor(activity.getColor(R.color.semi_text));
        iwantButton.setTextSize(18);
        iwantButton.setTypeface(typeface, Typeface.BOLD);
        iwantButton.setBackgroundDrawable(AppCompatResources.getDrawable(activity, R.drawable.round_shape)); // Use AppCompatResources!
        iwantButton.setElevation(5);
        iwantButton.setTranslationZ(9);
        iwantButton.setGravity(Gravity.CENTER);
        iwantButton.setLayoutParams(buttonLayoutParams);
        iwantButton.setPadding(15,15,15,15);
        iwantButton.setBackgroundColor(Color.argb(255,244,244,244));

        AppCompatButton cancelButton = new AppCompatButton(activity);
        cancelButton.setText(activity.getString(R.string.cancel));
        cancelButton.setTextColor(activity.getColor(R.color.semi_text));
        cancelButton.setTextSize(18);
        cancelButton.setTypeface(typeface, Typeface.BOLD);
        cancelButton.setBackgroundDrawable(AppCompatResources.getDrawable(activity, R.drawable.round_shape)); // Use AppCompatResources!
        cancelButton.setElevation(5);
        cancelButton.setTranslationZ(9);
        cancelButton.setGravity(Gravity.CENTER);
        cancelButton.setLayoutParams(buttonLayoutParams);
        cancelButton.setPadding(15,15,15,15);
        cancelButton.setBackgroundColor(Color.argb(255,244,244,244));

        buttonLayout.addView(cancelButton);
        buttonLayout.addView(iwantButton);

        EditText input = new EditText(activity);
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        linearLayout.addView(input);
        linearLayout.addView(buttonLayout);

        TextView title = new TextView(activity);
        title.setGravity(Gravity.CENTER);
        title.setText(activity.getString(R.string.you_already_ordered));
        title.setTextColor(activity.getColor(R.color.semi_text));
        title.setTypeface(typeface, Typeface.BOLD);
        title.setTextSize(20);

        builder.setCustomTitle(title);
        builder.setView(linearLayout);

        AlertDialog dialog = builder.create();

        iwantButton.setOnClickListener(view -> {

        });
        cancelButton.setOnClickListener(view -> dialog.cancel());

        dialog.show();
    }

    // TODO: replace this logic to MealOrderActivity and leave here only getting info interface
    public static void getPreviousOrders(Activity activity, String date, String mealName, ViewGroup parentLayout) {
        ImageView loadingImageView = activity.findViewById(R.id.loadingIcon);
        loadingImageView.setVisibility(View.VISIBLE);

        // Get user information to get mealPath
        FirebaseIntegration.getUserInfo(userInfo -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String mealPath = String.format("cities/%s/schools/%s/classes/%s/%s/%s", userInfo.get("cityName"), userInfo.get("schoolName"), userInfo.get("className"), date, mealName);

            // Set document listener to update
            db.document(mealPath).addSnapshotListener((documentSnapshot1, e) -> {
                loadingImageView.setVisibility(View.GONE);
                if (e!=null) {
                    return;
                }
                if (documentSnapshot1 != null && documentSnapshot1.get("user_names") != null)
                    //Log.e("DOCUMENT SNAPSHOT", documentSnapshot1.toString());
                    createUserCards(activity, documentSnapshot1, parentLayout);
            });
        });

    }
    private static void createUserCards(Activity activity, DocumentSnapshot documentSnapshot, ViewGroup parentLayout) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        parentLayout.removeAllViews(); // When update, we need to delete previous cards
        List<Map<String,String>> usersInfo = (List<Map<String, String>>) documentSnapshot.get("user_names");

        if (usersInfo == null)
            return;

        for (Map<String, String> user_ : usersInfo) {
            Map.Entry<String, String> entry = user_.entrySet().iterator().next();

            ImageView loadingImageView = activity.findViewById(R.id.loadingIcon);
            loadingImageView.setVisibility(View.VISIBLE);
            Log.e("UserInfo snapshot", usersInfo.toString());
            db.collection("users")
                    .document(entry.getKey())
                    .get()
                    .addOnSuccessListener(documentSnapshot1 -> {

                        Map<String, String> userRef = new HashMap<>();
                        userRef.put("userName", documentSnapshot1.get("userName").toString());
                        userRef.put("phoneNumber", documentSnapshot1.get("userPhoneNumber").toString());
                        userRef.put("uid", entry.getKey());
                        if (documentSnapshot1.get("avatarURL") != null)
                            userRef.put("avatarURL", documentSnapshot1.get("avatarURL").toString());
                        UserCardGenerator.createUserCard(activity, parentLayout, userRef);
                        loadingImageView.setVisibility(View.GONE);
                    });


        }
    }

    public static void removeOrder(Activity activity) {

    }

}
