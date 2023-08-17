package com.obektevCo.lunchlink;

import static com.obektevCo.lunchlink.LunchLinkUtilities.parseMealTitle;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.google.android.material.bottomappbar.BottomAppBar;

public class MealOrderActivity extends AppCompatActivity {

    private void setupWidgets() {
        // Title
        TextView meal_title = findViewById(R.id.meal_title);
        String mealName = getIntent().getStringExtra("meal_number");
        meal_title.setText(parseMealTitle(getApplicationContext(), mealName));

        // Date
        TextView date_text = findViewById(R.id.date_text);
        LunchLinkUtilities.getDate(getApplicationContext(), date -> date_text.setText(String.format("%s: %s", getString(R.string.date), date)));

        // Plus order button
        View meal_order_button = findViewById(R.id.add_order_button);
        meal_order_button.setOnClickListener(v -> {
            ImageView imageView = findViewById(R.id.loadingIcon);
            imageView.setVisibility(View.VISIBLE);
            OrderMealUtil.createOrder(MealOrderActivity.this,
                    date_text.getText().toString()
                            .replace(getString(R.string.date) + ':', "")
                            .replace("/","|") ,mealName,
                    findViewById(R.id.orders_list));
        });

        // Bottom app bar, info button usage
        BottomAppBar bottomAppBar = findViewById(R.id.bottomAppBar);
        bottomAppBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.info_button) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MealOrderActivity.this);
                builder.setMessage(getString(R.string.there_can_make_order) + parseMealTitle(MealOrderActivity.this, mealName))
                        .setNegativeButton(getString(R.string.cool), (dialog, id) -> dialog.dismiss());
                AlertDialog dialog = builder.create();

                dialog.show();
            }
            return false;
        });
    }

    private void getPreviousOrders() {
        String meal_name = getIntent().getStringExtra("meal_number");
        meal_name = parseMealTitle(getApplicationContext(), meal_name);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal);

        setupWidgets();
        setupLoadingIcon();
        // TODO: check if user has already ordered something, open dialog to make it possible to "Ivanov & Loban first meal".
        //  Alert this in another activities to! SERVER SIDE PLEASE
    }

    private void setupLoadingIcon() {
        ImageView imageView = findViewById(R.id.loadingIcon);
        imageView.setVisibility(View.GONE);

        AnimatedVectorDrawableCompat animatedVectorDrawableCompat = AnimatedVectorDrawableCompat.create(this, R.drawable.spin_loading);
        imageView.setImageDrawable(animatedVectorDrawableCompat);

        ObjectAnimator rotationAnimator = ObjectAnimator.ofFloat(imageView, "rotation", 0f, 360f);
        rotationAnimator.setDuration(2000); // Set the animation duration in milliseconds
        rotationAnimator.setRepeatCount(ValueAnimator.INFINITE);
        rotationAnimator.start();

    }

}
