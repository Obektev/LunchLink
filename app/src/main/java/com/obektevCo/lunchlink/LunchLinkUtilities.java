package com.obektevCo.lunchlink;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

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
                Log.w("Date HTTP", t.toString());
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
                return context.getString(R.string.brunch);
            case "CUSTOM":
                return context.getString(R.string.custom_meal);
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

}

