package com.obektevCo.lunchlink;

import static com.obektevCo.lunchlink.LunchLinkUtilities.parseMealTitle;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.obektevCo.lunchlink.R;
import com.google.android.material.bottomappbar.BottomAppBar;

public class MealOrderActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;

        // Title
        TextView meal_title = findViewById(R.id.meal_title);
        String meal_name = getIntent().getStringExtra("meal_number");
        meal_title.setText(parseMealTitle(getApplicationContext(), meal_name));

        // Date
        TextView date_text = findViewById(R.id.date_text);
        date_text.setText(LunchLinkUtilities.getDate(getApplicationContext()));

        // Plus order button
        View meal_order_button = findViewById(R.id.add_order_button);
        meal_order_button.setOnClickListener(v -> {

            FirebaseIntegration.test_push_data();
            UserCardGenerator.createUserCard(MealOrderActivity.this, findViewById(R.id.orders_list), user.getDisplayName());
        });

        // Bottom app bar, info button usage
        BottomAppBar bottomAppBar = findViewById(R.id.bottomAppBar);
        bottomAppBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.info_button) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MealOrderActivity.this);
                builder.setMessage("There you can make order for " + parseMealTitle(MealOrderActivity.this, meal_name))
                        .setNegativeButton("Cool!", (dialog, id) -> dialog.dismiss());
                AlertDialog dialog = builder.create();

                dialog.show();
            }
            return false;
        });
        // TODO: check if user has already ordered something, open dialog to make it possible to "Ivanov & Loban first meal".
        //  Alert this in another activities to! SERVER SIDE PLEASE

    }
}
