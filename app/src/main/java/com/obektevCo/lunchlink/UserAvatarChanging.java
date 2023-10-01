package com.obektevCo.lunchlink;

import android.app.Activity;
import android.net.Uri;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import gun0912.tedimagepicker.builder.TedImagePicker;


public class UserAvatarChanging {
    // Initialize Firebase Storage
    static FirebaseStorage storage = FirebaseStorage.getInstance();
    static FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    // Get a reference to the storage location where you want to upload the image
    static StorageReference storageRef = storage.getReference().child("avatars").child(user.getUid() + ".jpg");

    public static void chooseImage(Activity activity) {
        TedImagePicker.with(activity)
                .start(uri -> {
                    activity.findViewById(R.id.loadingIcon).setVisibility(View.VISIBLE);
                    uploadImage(uri, activity);
                });
    }
    private static void uploadImage(Uri imageUri, Activity activity){
        // Upload the image to Firebase Storage
        storageRef.putFile(imageUri)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Image upload is successful.
                        // Now you can update the user's profile with the new avatar URL.
                        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            // 'uri' is the URL of the uploaded image. Save this URL to the user's profile using Firebase Authentication's updateProfile method.
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setPhotoUri(uri)
                                    .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            // User profile updated successfully.
                                            // You can now use Glide to load the updated avatar image into the ImageView for preview.
                                            FirebaseIntegration.setAvatarURL(user.getPhotoUrl());
                                            LunchLinkUtilities.makeToast(activity, activity.getString(R.string.avatar_changed));
                                            activity.findViewById(R.id.loadingIcon).setVisibility(View.GONE);
                                        } else {
                                            LunchLinkUtilities.makeToast(activity, activity.getString(R.string.something_went_wrong));
                                            activity.findViewById(R.id.loadingIcon).setVisibility(View.GONE);
                                        }
                                    });
                        });
                    } else {
                        LunchLinkUtilities.makeToast(activity, activity.getString(R.string.something_went_wrong));
                        activity.findViewById(R.id.loadingIcon).setVisibility(View.GONE);
                    }
                });
    }

}
