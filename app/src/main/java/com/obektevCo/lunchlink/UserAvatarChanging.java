package com.obektevCo.lunchlink;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import gun0912.tedimagepicker.builder.TedImagePicker;


public class UserAvatarChanging {
    // Initialize Firebase Storage
    static FirebaseStorage storage = FirebaseStorage.getInstance();
    static FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    // Get a reference to the storage location where you want to upload the image
    static StorageReference storageRef = storage.getReference().child("avatars").child(currentUser.getUid() + ".jpg");

    public static void chooseImage(Activity activity) {
        TedImagePicker.with(activity)
                .start(uri -> {
                    uploadImage(uri, activity);
                });
    }
    private static void uploadImage(Uri imageUri, Context context){
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

                            currentUser.updateProfile(profileUpdates)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            // User profile updated successfully.
                                            // You can now use Glide to load the updated avatar image into the ImageView for preview.
                                            LunchLinkUtilities.makeToast(context, context.getString(R.string.avatar_changed));
                                        } else {
                                            // Failed to update user profile.
                                        }
                                    });
                        });
                    } else {
                        // Image upload failed.
                    }
                });
    }
}
