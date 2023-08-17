package com.obektevCo.lunchlink;

import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseIntegration {


    public static void setUserSchool(Context context, String school_name) {
        FirebaseFirestore db = FirebaseFirestore.getInstance(); // DON'T USE STATIC DB, IT'S MEMORY LEAK
        CollectionReference usersRef = db.collection("users");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser(); // much better to get user here to avoid null user before registration

        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Create a reference to the "users" collection and the document for the current user
            DocumentReference userDocRef = usersRef.document(userId);

            // Create a data object with the user's school name
            Map<String, Object> userData = new HashMap<>();
            userData.put("schoolName", school_name);

            // Set the data in the Firestore document
            userDocRef.set(userData, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> {
                        LunchLinkUtilities.makeToast(context, context.getString(R.string.success));
                        UserSettings.setSchoolInMemory(context, school_name);
                    })
                    .addOnFailureListener(e -> {
                        // Handle the failure case
                        LunchLinkUtilities.makeToast(context, context.getString(R.string.something_went_wrong));
                        Log.e("Firestore", "Error saving school name: " + e.getMessage());
                    });
        }
    }
    public static void setUserCity(Context context, String city_name) {
        FirebaseFirestore db = FirebaseFirestore.getInstance(); // DON'T USE STATIC DB, IT'S MEMORY LEAK
        CollectionReference usersRef = db.collection("users");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser(); // much better to get user here to avoid null user before registration

        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Create a reference to the "users" collection and the document for the current user
            DocumentReference userDocRef = usersRef.document(userId);

            // Create a data object with the user's city name
            Map<String, Object> userData = new HashMap<>();
            userData.put("cityName", city_name);

            // Set the data in the Firestore document
            userDocRef.set(userData, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> {
                        LunchLinkUtilities.makeToast(context, context.getString(R.string.success));
                        UserSettings.setCityInMemory(context, city_name);
                    })
                    .addOnFailureListener(e -> {
                        // Handle the failure case
                        LunchLinkUtilities.makeToast(context, context.getString(R.string.something_went_wrong));
                        Log.e("Firestore", "Error saving city name: " + e.getMessage());
                    });
        }
    }

    public static void setUserClass(Context context, String class_name) {
        FirebaseFirestore db = FirebaseFirestore.getInstance(); // DON'T USE STATIC DB, IT'S MEMORY LEAK
        CollectionReference usersRef = db.collection("users");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser(); // much better to get user here to avoid null user before registration

        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Create a reference to the "users" collection and the document for the current user
            DocumentReference userDocRef = usersRef.document(userId);

            // Create a data object with the user's class name
            Map<String, Object> userData = new HashMap<>();
            userData.put("className", class_name);

            // Set the data in the Firestore document
            userDocRef.set(userData, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> {
                        LunchLinkUtilities.makeToast(context, context.getString(R.string.success));
                        UserSettings.setClassInMemory(context, class_name);
                    })
                    .addOnFailureListener(e -> {
                        // Handle the failure case
                        LunchLinkUtilities.makeToast(context, context.getString(R.string.something_went_wrong));
                        Log.e("Firestore", "Error saving class name: " + e.getMessage());
                    });
        }
    }
    public interface OnSchoolNamesLoadedListener {
        void onSchoolNamesLoaded(List<String> schoolNames);
    }
    public static void getNamesFromDBC(String collectionPath, OnSchoolNamesLoadedListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference schoolsRef = db.collection(collectionPath);

        schoolsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<String> schoolNames = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    schoolNames.add(document.getId());
                }
                listener.onSchoolNamesLoaded(schoolNames);
            } else {
                //Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });
    }

}
