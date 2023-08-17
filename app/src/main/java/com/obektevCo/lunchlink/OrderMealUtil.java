package com.obektevCo.lunchlink;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class OrderMealUtil {

    public static void createOrder(Activity context, String date, String mealName, ViewGroup parentLayout) {
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
                            LunchLinkUtilities.makeToast(context, context.getString(R.string.unable_to_get_date));
                            LunchLinkUtilities.getDate(context, date1 -> LunchLinkUtilities.makeToast(context, context.getString(R.string.date_got)));
                        } else {

                            String mealPath = String.format("cities/%s/schools/%s/classes/%s/%s/%s", cityName, schoolName, className, date, mealName);

                            Map<String, Object> put_data = new HashMap<>();
                            Map<String, String> userInfo = new HashMap<>();
                            userInfo.put(user.getUid(), userName);
                            put_data.put("user_names", FieldValue.arrayUnion(userInfo));

                            ImageView imageView = context.findViewById(R.id.loadingIcon);

                            db.document(mealPath).update(put_data)
                                    .addOnSuccessListener(aVoid -> {
                                        imageView.setVisibility(View.GONE);
                                        LunchLinkUtilities.makeToast(context, context.getString(R.string.order_created));
                                        UserCardGenerator.createUserCard(context, parentLayout, userName);
                                    })
                                    .addOnFailureListener(e -> {
                                        LunchLinkUtilities.makeToast(context, context.getString(R.string.something_went_wrong));
                                    });
                        }
                    }
                });
    }
}
