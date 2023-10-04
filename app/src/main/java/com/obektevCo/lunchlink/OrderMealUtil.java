package com.obektevCo.lunchlink;

import android.app.Activity;
import android.app.Dialog;
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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderMealUtil {
    public static void createOrder(Activity activity, String date, String mealName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        ImageView loadingIcon = activity.findViewById(R.id.loadingIcon);

        assert user != null;
        loadingIcon.setVisibility(View.VISIBLE);
        db.collection("users")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        loadingIcon.setVisibility(View.GONE);
                        String cityName = documentSnapshot.getString("cityName");
                        String className = documentSnapshot.getString("className");
                        String schoolName = documentSnapshot.getString("schoolName");

                        String mealPath = String.format("cities/%s/schools/%s/classes/%s/%s/%s", cityName, schoolName, className, date, mealName);

                        loadingIcon.setVisibility(View.VISIBLE);
                        db.document(mealPath).get()
                                .addOnSuccessListener(documentSnapshot1 -> {
                                    loadingIcon.setVisibility(View.GONE);
                                    Map<String, Map<String, String>> previousOrders = (Map<String, Map<String, String>>) documentSnapshot1.get("user_names");

                                    // Check if user already ordered meal
                                    boolean alreadyOrdered = false;
                                    if (previousOrders != null) {
                                        loadingIcon.setVisibility(View.VISIBLE);

                                        for (Map.Entry<String, Map<String, String>> user_ : previousOrders.entrySet()) {
                                            if (user_.getKey().contains(user.getUid())) {
                                                alreadyOrdered = true;
                                                break;
                                            }
                                        }
                                        loadingIcon.setVisibility(View.GONE);
                                    }

                                    if (!alreadyOrdered && !mealName.equals("CUSTOM")) {
                                        Log.d("70 70", "70 70");
                                        Map<String, Object> putObject = new HashMap<>();
                                        Map<String, Map<String, String>> putData = new HashMap<>();
                                        Map<String, String> name_order = new HashMap<>();
                                        name_order.put(user.getDisplayName(), null);
                                        putData.put(user.getUid(), name_order);
                                        if (previousOrders != null) {
                                            putData.putAll(previousOrders);
                                        }

                                        putObject.put("user_names", putData);

                                        loadingIcon.setVisibility(View.VISIBLE);
                                        db.document(mealPath).set(putObject)
                                                .addOnSuccessListener(aVoid -> {
                                                    loadingIcon.setVisibility(View.GONE);
                                                    LunchLinkUtilities.makeToast(activity, activity.getString(R.string.order_created));
                                                })
                                                .addOnFailureListener(e -> {
                                                    LunchLinkUtilities.makeToast(activity, activity.getString(R.string.something_went_wrong));
                                                    loadingIcon.setVisibility(View.GONE);
                                                });
                                    }
                                    if (!alreadyOrdered && mealName.equals("CUSTOM")) {
                                        customMealOrder(activity, order -> {
                                            Map<String, Object> putObject = new HashMap<>();
                                            Map<String, Map<String, String>> putData = new HashMap<>();
                                            Map<String, String> name_order = new HashMap<>();
                                            name_order.put(user.getDisplayName(), order);
                                            putData.put(user.getUid(), name_order);
                                            if (previousOrders != null) {
                                                putData.putAll(previousOrders);
                                            }

                                            putObject.put("user_names", putData);

                                            loadingIcon.setVisibility(View.VISIBLE);
                                            db.document(mealPath).set(putObject)
                                                    .addOnSuccessListener(aVoid -> {
                                                        loadingIcon.setVisibility(View.GONE);
                                                        LunchLinkUtilities.makeToast(activity, activity.getString(R.string.order_created));
                                                        while (currentDialogs.size() > 0) {
                                                            currentDialogs.get(currentDialogs.size() - 1).cancel();
                                                            currentDialogs.remove(currentDialogs.size() - 1);
                                                        }
                                                        loadingIcon.setVisibility(View.GONE);
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        LunchLinkUtilities.makeToast(activity, activity.getString(R.string.something_went_wrong));
                                                        loadingIcon.setVisibility(View.GONE);
                                                    });
                                        });
                                    }
                                    if (alreadyOrdered && !mealName.equals("CUSTOM")) {
                                        orderAnotherPerson(activity, name -> {
                                            Map<String, Object> putObject = new HashMap<>();
                                            Map<String, Map<String, String>> putData = new HashMap<>();
                                            Map<String, String> name_order = new HashMap<>();

                                            String replacement = String.format("ordered_by_%s_to_%s", user.getDisplayName(), name);

                                            name_order.put(name, null);
                                            putData.put(replacement, name_order);
                                            putData.putAll(previousOrders);

                                            putObject.put("user_names", putData);

                                            loadingIcon.setVisibility(View.VISIBLE);
                                            db.document(mealPath).set(putObject)
                                                    .addOnSuccessListener(aVoid -> {
                                                        loadingIcon.setVisibility(View.GONE);
                                                        LunchLinkUtilities.makeToast(activity, activity.getString(R.string.order_created));
                                                        while (currentDialogs.size() > 0) {
                                                            currentDialogs.get(currentDialogs.size() - 1).cancel();
                                                            currentDialogs.remove(currentDialogs.size() - 1);
                                                        }
                                                        loadingIcon.setVisibility(View.GONE);
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        LunchLinkUtilities.makeToast(activity, activity.getString(R.string.something_went_wrong));
                                                        loadingIcon.setVisibility(View.GONE);
                                                    });
                                        });
                                    }
                                    if (alreadyOrdered && mealName.equals("CUSTOM")) {
                                        orderAnotherPerson(activity, name -> {
                                            Map<String, Object> putObject = new HashMap<>();
                                            Map<String, Map<String, String>> putData = new HashMap<>();
                                            Map<String, String> name_order = new HashMap<>();

                                            String replacement = String.format("ordered_by_%s_to_%s", user.getDisplayName(), name);
                                            customMealOrder(activity, order -> {
                                                name_order.put(name, order);
                                                putData.put(replacement, name_order);
                                                putData.putAll(previousOrders);

                                                putObject.put("user_names", putData);

                                                loadingIcon.setVisibility(View.VISIBLE);
                                                db.document(mealPath).set(putObject)
                                                        .addOnSuccessListener(aVoid -> {
                                                            loadingIcon.setVisibility(View.GONE);
                                                            LunchLinkUtilities.makeToast(activity, activity.getString(R.string.order_created));
                                                            while (currentDialogs.size() > 0) {
                                                                currentDialogs.get(currentDialogs.size() - 1).cancel();
                                                                currentDialogs.remove(currentDialogs.size() - 1);
                                                            }
                                                            loadingIcon.setVisibility(View.GONE);
                                                        })
                                                        .addOnFailureListener(e -> {
                                                            LunchLinkUtilities.makeToast(activity, activity.getString(R.string.something_went_wrong));
                                                            loadingIcon.setVisibility(View.GONE);
                                                        });
                                            });
                                        });
                                    }

                                });
                    }
                });
    }

    interface customOrderListener {
        void onOrderGot(String order);
    }
    private static List<Dialog> currentDialogs = new ArrayList<>();
    private static void customMealOrder(Activity activity, customOrderListener listener) {
        Typeface typeface = ResourcesCompat.getFont(activity, R.font.aldrich);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        TextView title = new TextView(activity);
        title.setTextSize(25);
        title.setTypeface(typeface, Typeface.BOLD);
        title.setText(activity.getString(R.string.order));
        title.setTextColor(activity.getColor(R.color.text_main));
        title.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        builder.setCustomTitle(title);

        LinearLayout linearLayout = new LinearLayout(activity);
        linearLayout.setBackground(AppCompatResources.getDrawable(activity, R.drawable.round_shape));
        linearLayout.setBackgroundColor(activity.getColor(R.color.background));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(15,15,15,15);
        linearLayout.setLayoutParams(layoutParams);

        TextView enterOrderTextView = new TextView(activity);
        enterOrderTextView.setTextColor(activity.getColor(R.color.light_text));
        enterOrderTextView.setTypeface(typeface);
        enterOrderTextView.setText(activity.getString(R.string.enter_order));
        enterOrderTextView.setTextSize(25);
        enterOrderTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        linearLayout.addView(enterOrderTextView);

        ConstraintLayout.LayoutParams editTextLayoutParams = new ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        editTextLayoutParams.setMargins(20, 10, 20, 10);
        EditText editText = new EditText(activity);
        editText.setTextColor(activity.getColor(R.color.light_text));
        editText.setTypeface(typeface);
        editText.setBackground(AppCompatResources.getDrawable(activity, R.drawable.round_shape));
        editText.setBackgroundColor(activity.getColor(R.color.button_color));
        editText.setElevation(5);
        editText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        editText.setTranslationZ(5);
        editText.setGravity(View.TEXT_ALIGNMENT_CENTER);
        editText.setLayoutParams(editTextLayoutParams);

        linearLayout.addView(editText);

        AppCompatButton continueButton = new AppCompatButton(activity);
        continueButton.setBackground(AppCompatResources.getDrawable(activity, R.drawable.round_shape));
        continueButton.setBackgroundColor(activity.getColor(R.color.button_color));
        continueButton.setTypeface(typeface);
        continueButton.setTextColor(activity.getColor(R.color.light_text));
        continueButton.setText(activity.getString(R.string.continue_));
        continueButton.setTextSize(20);
        continueButton.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        continueButton.setElevation(5);
        continueButton.setTranslationZ(5);
        continueButton.setLayoutParams(editTextLayoutParams); // fix

        linearLayout.addView(continueButton);
        builder.setView(linearLayout);
        Dialog dialog = builder.create();

        currentDialogs.add(dialog);

        continueButton.setOnClickListener(view -> {
            String order = editText.getText().toString();
            if (order.isEmpty()) {
                LunchLinkUtilities.makeToast(activity, activity.getString(R.string.order_empty));
            } else {
                listener.onOrderGot(order);
            }
        });

        dialog.show();
    }

    interface orderAnotherPersonListener {
        void onNameGot(String name);
    }
    private static void orderAnotherPerson(Activity activity, orderAnotherPersonListener listener) {
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
        currentDialogs.add(dialog);

        iwantButton.setOnClickListener(view -> {
            String userName = input.getText().toString();
            if (userName.isEmpty()) {
                LunchLinkUtilities.makeToast(activity, activity.getString(R.string.name_cannot_be_null));
            } else
                listener.onNameGot(userName);

        });
        cancelButton.setOnClickListener(view -> dialog.cancel());

        dialog.show();
    }

    public static void getPreviousOrders(Activity activity, String date, String mealName, ViewGroup parentLayout) {
        ImageView loadingImageView = activity.findViewById(R.id.loadingIcon);
        loadingImageView.setVisibility(View.VISIBLE);

        // Get user information to get mealPath
        FirebaseIntegration.getUserInfo(userInfo -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String mealPath = String.format("cities/%s/schools/%s/classes/%s/%s/%s", userInfo.get("cityName"), userInfo.get("schoolName"), userInfo.get("className"), date, mealName);

            // Set document listener to update
            db.document(mealPath).addSnapshotListener((documentSnapshot, e) -> {
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

        Map<String,Map<String, String>> usersInfo = (Map<String, Map<String, String>>) documentSnapshot.get("user_names");

        if (usersInfo.isEmpty()) {
            loadingImageView.setVisibility(View.GONE);
            if (parentLayout.getChildCount() == 1) {
                activity.recreate();
            }
            return;
        }

        parentLayout.removeAllViews(); // When update, we need to delete previous cards


        for (Map.Entry<String, Map<String, String>> entry : usersInfo.entrySet()) {

            if (entry.getKey().contains("ordered_by_")) { // check if current meal was ordered by another person
                Map<String, String> userRef = new HashMap<>();
                String replacement = entry.getKey()
                        .replace("ordered_by_", " " +
                                activity.getString(R.string.ordered_by) + " ")
                        .replace("_to_", " " + activity.getString(R.string.to_) + " ");
                Map<String, String> data = (Map<String, String>) entry.getValue();
                for (Map.Entry<String, String> entry1 : data.entrySet()) { // TODO: FIX THIS SHIT
                    userRef.put("userName", entry1.getKey());
                    userRef.put("order", entry1.getValue());
                }
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
                        Map<String, String> order = (Map<String, String>) entry.getValue();
                        userRef.put("order", order.get(documentSnapshot1.get("userName").toString())); // TODO: FIX THIS SHIT
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

}
