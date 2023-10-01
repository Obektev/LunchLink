package com.obektevCo.lunchlink;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseIntegration {


    public static void setUserSchool(Context context, String school_name) {
        FirebaseFirestore db = FirebaseFirestore.getInstance(); // DON'T USE STATIC DB, IT'S MEMORY LEAK
        CollectionReference usersRef = db.collection("users");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); // much better to get user here to avoid null user before registration

        if (user != null) {
            String userId = user.getUid();

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
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); // much better to get user here to avoid null user before registration

        if (user != null) {
            String userId = user.getUid();

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
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); // much better to get user here to avoid null user before registration

        if (user != null) {
            String userId = user.getUid();

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

    // setUserName: we don't need any parameters because we are linking user's firebase actual name to name in "users"
    public static void setUserName() {
        FirebaseFirestore db = FirebaseFirestore.getInstance(); // DON'T USE STATIC DB, IT'S MEMORY LEAK
        CollectionReference usersRef = db.collection("users");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); // much better to get user here to avoid null user before registration

        if (user != null) {
            String userId = user.getUid();

            // Create a reference to the "users" collection and the document for the current user
            DocumentReference userDocRef = usersRef.document(userId);

            // Create a data object with the user's class name
            Map<String, Object> userData = new HashMap<>();
            userData.put("userName", user.getDisplayName());

            // Set the data in the Firestore document
            userDocRef.set(userData, SetOptions.merge());
        }
    }
    public static void setUserPhoneNumber() {
        FirebaseFirestore db = FirebaseFirestore.getInstance(); // DON'T USE STATIC DB, IT'S MEMORY LEAK
        CollectionReference usersRef = db.collection("users");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); // much better to get user here to avoid null user before registration

        if (user != null) {
            String userId = user.getUid();

            // Create a reference to the "users" collection and the document for the current user
            DocumentReference userDocRef = usersRef.document(userId);

            // Create a data object with the user's class name
            Map<String, Object> userData = new HashMap<>();
            userData.put("userPhoneNumber", user.getPhoneNumber());

            // Set the data in the Firestore document
            userDocRef.set(userData, SetOptions.merge());
        }
    }

    public static void setAvatarURL(Uri photoUrl) {
        FirebaseFirestore db = FirebaseFirestore.getInstance(); // DON'T USE STATIC DB, IT'S MEMORY LEAK
        CollectionReference usersRef = db.collection("users");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); // much better to get user here to avoid null user before registration

        if (user != null) {
            String userId = user.getUid();

            // Create a reference to the "users" collection and the document for the current user
            DocumentReference userDocRef = usersRef.document(userId);

            // Create a data object with the user's class name
            Map<String, Object> userData = new HashMap<>();
            userData.put("avatarURL", photoUrl);

            // Set the data in the Firestore document
            userDocRef.set(userData, SetOptions.merge());
        }
    }

    public interface OnSchoolNamesLoadedListener {
        void onSchoolNamesLoaded(List<String> schoolNames);
    }
    public static void getNamesFromCollection(String collectionPath, OnSchoolNamesLoadedListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference namesRef = db.collection(collectionPath);

        namesRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<String> names = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    names.add(document.getId());
                }
                listener.onSchoolNamesLoaded(names);
            } else {
                //Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });
    }
    public static void getUserInfo(onUserInfoGotListener listener) {
        Map<String, String> result = new HashMap<>();
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
                        result.put("cityName", cityName);
                        result.put("schoolName", schoolName);
                        result.put("className", className);
                        result.put("userName", user.getDisplayName());
                        listener.onInfoGot(result);
                    }
                });
    }
    interface onUserInfoGotListener {
        void onInfoGot(Map<String, String> userInfo);
    }

    interface menuGotListener {
        void onMenuGot(Uri image_ulr);
    }

    // getMenuFromDB: gets URL to image and backs to MainActivity, where pastes into ImageView
    public static void getMenuFromDB(Context context, menuGotListener listener) {
        FirebaseIntegration.getUserInfo(userInfo -> {
            LunchLinkUtilities.getDate(context, date -> {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                String menuPath = String.format("menu/%s/%s", userInfo.get("cityName"), userInfo.get("schoolName"));
                StorageReference storageRef = storage.getReference().child(menuPath).child(date.replace("/", "\\") + ".jpg");

                // Check if file exists
                storageRef.getMetadata().addOnSuccessListener(storageMetadata -> {
                    // File exists, get the download URL
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Pass the image URL back to the listener
                        listener.onMenuGot(uri);
                    }).addOnFailureListener(exception -> {
                        // Handle any errors during URL retrieval
                        listener.onMenuGot(null); // Pass null to indicate failure
                    });
                }).addOnFailureListener(exception -> {
                    // File does not exist, handle this case
                    listener.onMenuGot(null); // Pass null to indicate failure
                });
            });
        });
    }
    interface  deletingMenuListener {
        void onMenuDeleted();
    }
    public static void deleteMenuFromDB(Context context, deletingMenuListener listener) {
        FirebaseIntegration.getUserInfo(userInfo -> {
            LunchLinkUtilities.getDate(context, date -> {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                String menuPath = String.format("menu/%s/%s", userInfo.get("cityName"), userInfo.get("schoolName"));
                StorageReference storageRef = storage.getReference().child(menuPath).child(date.replace("/","\\") + ".jpg");

                // Delete the file
                storageRef.delete().addOnSuccessListener(aVoid -> {
                    LunchLinkUtilities.makeToast(context, context.getString(R.string.menu_image_deleted));
                    listener.onMenuDeleted();
                }).addOnFailureListener(exception -> {
                    LunchLinkUtilities.makeToast(context, context.getString(R.string.something_went_wrong));
                });
            });
        });
    }

}
