package com.obektevCo.lunchlink;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LunchLinkUtilities {
    public static void makeToast(Context context, String announcement) {
        Toast.makeText(context, announcement, Toast.LENGTH_SHORT).show();
    }

    public interface OnDateGotListener {
        void onDateGot(String date);
    }
    public static void getDate(Context context, OnDateGotListener listener) {
        HTTPRequests httpRequests = new HTTPRequests();
        Call<Map<String, String>> date_call = httpRequests.jsonPlaceHolderApi.getDate();

        date_call.enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                assert response.body() != null;
                String date_string = response.body().get("date");
                assert date_string != null;
                String new_date_string = date_string.substring(3, 5) +
                        "/" + date_string.substring(0, 2) + "/" +
                        date_string.substring(8, 10);

                listener.onDateGot(new_date_string);
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                listener.onDateGot(null);
                LunchLinkUtilities.makeToast(context, context.getString(R.string.unable_to_get_date));
            }
        });
    }
    public static String parseDay(Context context, int day_id) {
        switch (day_id) {
            case 1:
                return context.getString(R.string.monday);
            case 2:
                return context.getString(R.string.tuesday);
            case 3:
                return context.getString(R.string.wednesday);
            case 4:
                return context.getString(R.string.thursday);
            case 5:
                return context.getString(R.string.friday);
            case 6:
                return context.getString(R.string.thursday);
            case 7:
                return context.getString(R.string.sunday);
            default:
                return context.getString(R.string.sunday);
        }
    }
    public static String parseMealTitle(Context context, String meal_name) {
        switch (meal_name) {
            case "BREAKFAST":
                return context.getString(R.string.breakfast);
            case "DIET":
                return context.getString(R.string.diet);
            case "BRUNCH":
                return context.getString(R.string.snack);
            case "CUSTOM":
                return context.getString(R.string.custom_meal);
            case "LUNCH":
                return context.getString(R.string.lunch);
            default:
                return "unknown";
        }
    }

    public static String formatPhoneNumber(String phone_number) {
        phone_number = phone_number.replace(" ", "").replace("(", "").
                replace(")", "").replace("-", "");
        if (!phone_number.contains("+"))  // Plus is necessary! E.164 format Example: +375333013593
            phone_number = '+' + phone_number;

        return phone_number;
    }

    interface onMenuImageUploadedListener {
        void onImageUploaded();
    }
    public static void uploadMenuImage(Uri imageUri, Activity activity, onMenuImageUploadedListener listener){

        LunchLinkUtilities.getDate(activity, date -> {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            FirebaseIntegration.getUserInfo(userInfo -> {

                String menuPath = String.format("menu/%s/%s", userInfo.get("cityName"), userInfo.get("schoolName"));
                StorageReference storageRef = storage.getReference().child(menuPath).child(date.replace("/", "\\") + ".jpg");
                storageRef.putFile(imageUri)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Image upload is successful.
                                storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                    makeToast(activity, activity.getString(R.string.uploaded));
                                    listener.onImageUploaded();
                                });
                            } else {
                                makeToast(activity, activity.getString(R.string.something_went_wrong));
                            }
                            ImageView loadingIcon = activity.findViewById(R.id.loadingIcon);
                            loadingIcon.setVisibility(View.GONE);
                        });
            });
        });
    }
}

