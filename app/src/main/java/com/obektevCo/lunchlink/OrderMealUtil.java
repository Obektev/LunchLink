package com.obektevCo.lunchlink;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
                        String uid = user.getUid();
                        String phoneNumber = user.getPhoneNumber();
                        String userName = user.getDisplayName();

                        Map<String, String> userRef = new HashMap<>(); // Used for userCardGenerator
                        userRef.put("userName", userName);
                        userRef.put("uid", uid);
                        userRef.put("phoneNumber", phoneNumber);

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
                                                           UserCardGenerator.createUserCard(activity, parentLayout, userRef);
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
                                                       UserCardGenerator.createUserCard(activity, parentLayout, userRef);
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

    public static void getPreviousOrders(Activity activity, String date, String mealName, ViewGroup parentLayout) {


        ImageView loadingImageView = activity.findViewById(R.id.loadingIcon);
        loadingImageView.setVisibility(View.VISIBLE);

        // Get user information to get mealPath
        FirebaseIntegration.getUserInfo(userInfo -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String mealPath = String.format("cities/%s/schools/%s/classes/%s/%s/%s", userInfo.get("cityName"), userInfo.get("schoolName"), userInfo.get("className"), date, mealName);

            // Get previous orders
            /*
            db.document(mealPath).get()
                    .addOnSuccessListener(documentSnapshot1 -> {

                        loadingImageView.setVisibility(View.GONE);

                        if (documentSnapshot1.exists())
                            createUserCards(activity, documentSnapshot1, parentLayout);
                        else
                            Log.d("Document", "Document does not exist");
                    })
                    .addOnFailureListener(e -> {
                        // Handle the failure
                        loadingImageView.setVisibility(View.GONE);
                        LunchLinkUtilities.makeToast(activity, activity.getString(R.string.something_went_wrong));
                    });

             */
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
            Log.e("dofidfh23gds", usersInfo.toString());
            db.collection("users")
                    .document(entry.getKey())
                    .get()
                    .addOnSuccessListener(documentSnapshot1 -> {
                        Map<String, String> userRef = new HashMap<>();
                        Log.e("gdfguhfdigudfhjgifdgh", documentSnapshot1.toString());
                        userRef.put("userName", Objects.requireNonNull(documentSnapshot1.get("userName")).toString());
                        userRef.put("phoneNumber", Objects.requireNonNull(documentSnapshot1.get("phoneNumber")).toString());
                        userRef.put("uid", entry.getKey());
                        UserCardGenerator.createUserCard(activity, parentLayout, userRef);
                    });


        }
    }

    public static void removeOrder(Activity activity) {

    }
}
