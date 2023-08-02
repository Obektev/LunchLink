package com.obektevCo.lunchlink;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.ViewGroup;
import android.widget.Toast;

import com.obektevCo.lunchlink.R;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class LunchLinkUtilities {
    public static void makeToast(Context context, String announcement) {
        Toast.makeText(context, announcement, Toast.LENGTH_SHORT).show();
    }
    public static String getDate(Context context) {
        Date current_date = Calendar.getInstance().getTime();
        String date_string = "%s: %s"; // date: <date>
        date_string = String.format(date_string, context.getString(R.string.date), DateFormat.getDateInstance().format(current_date));
        return date_string;
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

