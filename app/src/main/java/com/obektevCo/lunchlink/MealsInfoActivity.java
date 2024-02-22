package com.obektevCo.lunchlink;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MealsInfoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mealsinfo);

        setupLoadingIcon();
        setupSchoolName();

        setupClassesInformation();


    }

    void setupClassesInformation() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        FirebaseIntegration.getUserInfo(userInfo -> {

            db.collection(String.format(
                    "cities/%s/schools/%s/classes",
                    userInfo.get("cityName"),
                    userInfo.get("schoolName")
                )
            ).get().addOnCompleteListener(task -> {

                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult().getDocuments()) {
                        String className = document.getId();

                        getClassInfo(String.format(
                                "cities/%s/schools/%s/classes/%s",
                                userInfo.get("cityName"),
                                userInfo.get("schoolName"),
                                className
                        ), info -> {
                            //Log.d("38 Line " + className, info.toString());
                            createClassCard(className, info);
                        });
                    }
                }
            });
        });
    }

    @SuppressLint("DefaultLocale")
    void createClassCard(String className, Map<String, Map<String, Map<String, String>>> information) {

        String customMealInfo = "";
        if (information.get("CUSTOM") != null)
            for (Map.Entry<String, Map<String, String>> var : information.get("CUSTOM").entrySet()) {
                customMealInfo += var.getValue().entrySet().iterator().next().getValue() + ' ';
            }

        Typeface typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.aldrich);

        LinearLayout parentLayout = findViewById(R.id.cardsLinearLayout);

        CardView classCardView = new CardView(getApplicationContext());

        ConstraintLayout.LayoutParams cardParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cardParams.setMargins(10, 10, 10, 10);

        classCardView.setLayoutParams(cardParams);

        classCardView.setMinimumHeight(460);
        classCardView.setBackground(AppCompatResources.getDrawable(getApplicationContext(), R.drawable.round_shape));
        classCardView.setBackgroundColor(getColor(R.color.background));
        classCardView.setPadding(25,25,25,25);
        classCardView.setElevation(10);
        classCardView.setTranslationZ(5);

        LinearLayout contentLinearLayout = new LinearLayout(getApplicationContext());
        contentLinearLayout.setOrientation(LinearLayout.VERTICAL);
        contentLinearLayout.setLayoutParams(cardParams);
        contentLinearLayout.setPadding(10, 0, 10, 0);
        contentLinearLayout.setGravity(Gravity.START);

        TextView classTextView = new TextView(getApplicationContext());
        classTextView.setText(className);
        classTextView.setTextSize(20);
        classTextView.setTextColor(getColor(R.color.semi_text));
        classTextView.setTypeface(typeface);

        contentLinearLayout.addView(classTextView);

        addMealContent(getString(R.string.breakfast), String.valueOf(information.get("BREAKFAST") == null ? 0 : information.get("BREAKFAST").size()), contentLinearLayout);
        addMealContent(getString(R.string.diet), String.valueOf(information.get("DIET") == null ? 0 : information.get("DIET").size()), contentLinearLayout);
        addMealContent(getString(R.string.lunch), String.valueOf(information.get("LUNCH") == null ? 0 : information.get("LUNCH").size()), contentLinearLayout);
        addMealContent(getString(R.string.snack), String.valueOf(information.get("BRUNCH") == null ? 0 : information.get("BRUNCH").size()), contentLinearLayout);
        addMealContent(getString(R.string.custom_meal), information.get("CUSTOM") == null ? "" : customMealInfo, contentLinearLayout);


        classCardView.addView(contentLinearLayout);

        parentLayout.addView(classCardView);
    }

    @SuppressLint("SetTextI18n")
    void addMealContent(String mealName, String content, LinearLayout parent) {

        Typeface typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.aldrich);

        LinearLayout mealLayout = new LinearLayout(getApplicationContext());
        mealLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mealLayout.setOrientation(LinearLayout.HORIZONTAL);

        TextView mealTitleTextView = new TextView(getApplicationContext());
        mealTitleTextView.setText(mealName + ' ');
        mealTitleTextView.setTextSize(20);
        mealTitleTextView.setTextColor(getColor(R.color.semi_text));
        mealTitleTextView.setTypeface(typeface);


        TextView mealContentTextView = new TextView(getApplicationContext());
        mealContentTextView.setText(content + ' ');
        mealContentTextView.setTextSize(20);
        mealContentTextView.setTextColor(getColor(R.color.semi_text));
        mealContentTextView.setTypeface(typeface);

        mealLayout.addView(mealTitleTextView);
        mealLayout.addView(mealContentTextView);

        parent.addView(mealLayout);
    }

    private void setupSchoolName() {
        TextView schoolName = findViewById(R.id.schoolTextView);

        loadingImageView.setVisibility(View.VISIBLE);

        FirebaseIntegration.getUserInfo(userInfo -> {
            loadingImageView.setVisibility(View.GONE);

            schoolName.setText(userInfo.get("schoolName"));

        });

    }

    /*
    order_type → user_id → user_name: meal_name
    Who wrote this shit ◄↕►
    TODO: delete this lines before commit
     */
    interface classInfoGotListener {
        void infoGotten(Map<String, Map<String, Map<String, String>>> info);
    }
    private void getClassInfo(String classPath, classInfoGotListener listener) {

        Map<String, Map<String, Map<String, String>>> result = new HashMap<>();

        LunchLinkUtilities.getDate(getApplicationContext(), gottenDate -> {
            final String date = "/" + gottenDate.replace("/", "|") + '/';
            // This pyramid is necessary because of asynchronous behaviour. How to do this other way idk ☻
            // Breakfast
            getMealInfo(classPath + date + "BREAKFAST", info -> {
                //Log.w("65 mealpath", classPath + date + "BREAKFAST");
                result.put("BREAKFAST", info);

                // Diet
                getMealInfo(classPath + date + "DIET", info1 -> {
                    result.put("DIET", info1);

                    // Lunch
                    getMealInfo(classPath + date + "LUNCH", info2 -> {
                        result.put("LUNCH", info2);

                        // Brunch
                        getMealInfo(classPath + date + "BRUNCH", info3 -> {
                            result.put("BRUNCH", info3);

                            // Custom
                            getMealInfo(classPath + date + "CUSTOM", info4 -> {
                                result.put("CUSTOM", info4);
                                listener.infoGotten(result);
                            });
                        });
                    });
                });
            });
        });
    }

    interface onMealInfoGotListener {
        void infoGotten(Map<String, Map<String, String>> info);
    }
    private void getMealInfo(String mealPath, onMealInfoGotListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.document(mealPath).get().addOnCompleteListener(task -> {
            listener.infoGotten((Map<String, Map<String, String>>) task.getResult().get("user_names"));
        });
    }

    ImageView loadingImageView;
    private void setupLoadingIcon() {
        loadingImageView = findViewById(R.id.loadingIcon);
        AnimatedVectorDrawableCompat animatedVectorDrawableCompat = AnimatedVectorDrawableCompat.create(this, R.drawable.spin_loading);
        loadingImageView.setImageDrawable(animatedVectorDrawableCompat);

        ObjectAnimator rotationAnimator = ObjectAnimator.ofFloat(loadingImageView, "rotation", 0f, 360f);
        rotationAnimator.setDuration(2000); // Set the animation duration in milliseconds
        rotationAnimator.setRepeatCount(ValueAnimator.INFINITE);
        rotationAnimator.start();

        loadingImageView.setVisibility(View.GONE);
    }

}
