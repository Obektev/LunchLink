package com.obektevCo.lunchlink;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
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

                        String userName = user.getDisplayName();

                        if (date == null) {
                            LunchLinkUtilities.makeToast(activity, activity.getString(R.string.unable_to_get_date));
                            LunchLinkUtilities.getDate(activity, date1 -> LunchLinkUtilities.makeToast(activity, activity.getString(R.string.date_got)));
                        } else {

                            String mealPath = String.format("cities/%s/schools/%s/classes/%s/%s/%s", cityName, schoolName, className, date, mealName);

                            Map<String, Object> putData = new HashMap<>();
                            Map<String, String> info = new HashMap<>();
                            info.put(user.getUid(), userName);
                            putData.put("user_names", info);

                            putOrderData(activity, putData, mealPath, "orderself");

                        }
                    }
                });
    }

    @SuppressWarnings("unchecked")
    private static void putOrderData(Activity activity, Map<String, Object> putData, String mealPath, String args) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        ImageView loadingIcon = activity.findViewById(R.id.loadingIcon);

        db.document(mealPath).get()
                .addOnSuccessListener(documentSnapshot1 -> {
                    //if (documentSnapshot1.exists()) {
                    //if (documentSnapshot1.get("user_names") != null) { // there was a problem: if no order - update doesn't work, b.s. we need to put data first.
                    Map<String, String> previousOrders = (Map<String, String>) documentSnapshot1.get("user_names");

                    if (previousOrders != null) {
                        if (args.equals("orderself")) {
                            for (Map.Entry<String, String> user_ : previousOrders.entrySet()) {
                                if (user_.getKey().contains(user.getUid())) {
                                    showAlreadyOrderedDialog(activity, mealPath);
                                    loadingIcon.setVisibility(View.GONE);
                                    return;
                                }
                            }
                        }
                        ((Map<String, String>) putData.get("user_names")).putAll(previousOrders);
                    }
                    db.document(mealPath).set(putData)
                            .addOnSuccessListener(aVoid -> {
                                loadingIcon.setVisibility(View.GONE);
                                LunchLinkUtilities.makeToast(activity, activity.getString(R.string.order_created));
                            })
                            .addOnFailureListener(e -> {
                                LunchLinkUtilities.makeToast(activity, activity.getString(R.string.something_went_wrong));
                                loadingIcon.setVisibility(View.GONE);
                            });
                                           /*} else {
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
                                       } */
                });
    }

    private static void showAlreadyOrderedDialog(Activity activity, String mealPath) {
        Typeface typeface = ResourcesCompat.getFont(activity, R.font.aldrich);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LinearLayout linearLayout = new LinearLayout(activity);
        linearLayout.setBackground(AppCompatResources.getDrawable(activity, R.drawable.round_shape));
        linearLayout.setBackgroundColor(activity.getColor(R.color.background));
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
        iwantButton.setBackgroundColor(activity.getColor(R.color.light));

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
        cancelButton.setBackgroundColor(activity.getColor(R.color.light));

        buttonLayout.addView(cancelButton);
        buttonLayout.addView(iwantButton);

        EditText input = new EditText(activity);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint(R.string.enter_person_name);
        input.setHintTextColor(activity.getColor(R.color.semi_text));
        input.setTextColor(activity.getColor(R.color.semi_text));

        TextView description = new TextView(activity);
        description.setGravity(Gravity.CENTER);
        description.setText(activity.getString(R.string.if_you_want_to_order_to_another_person));
        description.setTextColor(activity.getColor(R.color.semi_text));
        description.setBackgroundColor(activity.getColor(R.color.background));
        description.setTypeface(typeface, Typeface.BOLD);
        description.setTextSize(20);

        linearLayout.addView(description);
        linearLayout.addView(input);
        linearLayout.addView(buttonLayout);

        TextView title = new TextView(activity);
        title.setGravity(Gravity.CENTER);
        title.setText(activity.getString(R.string.you_already_ordered));
        title.setTextColor(activity.getColor(R.color.text_main));
        title.setBackgroundColor(activity.getColor(R.color.background));
        title.setTypeface(typeface, Typeface.BOLD);
        title.setTextSize(20);

        builder.setCustomTitle(title);
        builder.setView(linearLayout);

        AlertDialog dialog = builder.create();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        iwantButton.setOnClickListener(view -> {
            String inputString = input.getText().toString();
            if (inputString.isEmpty()) {
                LunchLinkUtilities.makeToast(activity, activity.getString(R.string.name_cannot_be_null));
                return;
            }
            Map<String, Object> putData = new HashMap<>();
            Map<String, String> userInfo = new HashMap<>();
            userInfo.put("ordered_by_" + user.getDisplayName() + "_to_" + inputString, inputString);
            putData.put("user_names", userInfo);

            putOrderData(activity, putData, mealPath, "orderanother");
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
            db.document(mealPath).addSnapshotListener((documentSnapshot, e) -> {
                Log.e("DOCUMENT 245", documentSnapshot.toString());
                loadingImageView.setVisibility(View.GONE);
                if (e!=null) {
                    return;
                }
                if (documentSnapshot.get("user_names") != null) {
                    createUserCards(activity, documentSnapshot, parentLayout, mealPath);
                } else {
                    loadingImageView.setVisibility(View.GONE);
                }
            });
        });

    }
    private static void createUserCards(Activity activity, DocumentSnapshot documentSnapshot, ViewGroup parentLayout, String mealPath) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        ImageView loadingImageView = activity.findViewById(R.id.loadingIcon);
        loadingImageView.setVisibility(View.VISIBLE);

        Map<String,String> usersInfo = (Map<String, String>) documentSnapshot.get("user_names");

        if (usersInfo.isEmpty()) {
            loadingImageView.setVisibility(View.GONE);
            if (parentLayout.getChildCount() == 1) {
                activity.recreate();
            }
            return;
        }

        parentLayout.removeAllViews(); // When update, we need to delete previous cards


        for (Map.Entry<String, String> entry : usersInfo.entrySet()) {

            if (entry.getKey().contains("ordered_by_")) { // check if current meal was ordered by another person
                Map<String, String> userRef = new HashMap<>();
                String replacement = entry.getKey().replace("ordered_by_", activity.getString(R.string.ordered_by) + ' ');
                userRef.put("userName", entry.getValue());
                userRef.put("phoneNumber", replacement);
                userRef.put("uid", entry.getKey());
                UserCardGenerator.createUserCard(activity, parentLayout, userRef, mealPath);
                loadingImageView.setVisibility(View.GONE);
                continue;
            }

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
                        UserCardGenerator.createUserCard(activity, parentLayout, userRef, mealPath);
                        loadingImageView.setVisibility(View.GONE);
                    });


        }
    }

    public static void removeOrder(Activity activity, String uid, String mealPath) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Step 1: Retrieve the existing map
        db.document(mealPath).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Map<String, Object> data = documentSnapshot.getData();

                        // Step 2: Remove the key-value pair from the map
                        if (data != null) {
                            Map<String, Object> userNames = (Map<String, Object>) data.get("user_names");
                            if (userNames != null) {
                                userNames.remove(uid);

                                // Step 3: Update the document with the modified map
                                db.document(mealPath)
                                        .update("user_names", userNames)
                                        .addOnSuccessListener(unused -> {
                                            LunchLinkUtilities.makeToast(activity, activity.getString(R.string.order_canceled));
                                            //activity.recreate();
                                        })
                                        .addOnFailureListener(e -> LunchLinkUtilities.makeToast(activity, activity.getString(R.string.something_went_wrong)));
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> LunchLinkUtilities.makeToast(activity, activity.getString(R.string.something_went_wrong)));
    }

    interface onMenuGotListener {
        void onMenuGot(Map<String, List<String>> menu);
    }

    @SuppressLint("DefaultLocale")
    public static void getMenuFromDB(Context context, onMenuGotListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseIntegration.getUserInfo(userInfo -> {
            if (userInfo == null) {
                LunchLinkUtilities.makeToast(context, context.getString(R.string.something_went_wrong));
                return;
            }

            String documentPath = String.format("cities/%s/schools/%s", userInfo.get("cityName"), userInfo.get("schoolName"));

            db.document(documentPath)
                    .addSnapshotListener((documentSnapshot, e) -> {
                        assert documentSnapshot != null;
                        if (documentSnapshot.exists()) {
                            listener.onMenuGot((Map<String, List<String>>) documentSnapshot.get("menu"));
                        }
                    });
        });
    }
}
