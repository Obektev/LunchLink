package com.obektevCo.lunchlink;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FirestorePopulationActivity extends AppCompatActivity {

    private static final String TAG = "FirestorePopulation";

    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        db = FirebaseFirestore.getInstance();

        aboba("Витебск");
        //aboba("Минск");
        //aboba("Гомель");
        //aboba("Могилёв");
        //aboba("Брест");
        //aboba("Гродно");
    }

    void aboba(String cityName) {
        Map<String, String> test = new HashMap<>();
        test.put("test", "test");
        db.document("cities/" + cityName).set(test);

        for (int i = 1;  i <= 30; i++) {
            db.document("cities/" + cityName + "/schools/" + "школа " + i).set(test);
            Log.d("aboba", "cities/" + cityName + "/schools/" + "школа " + i + "/classes/");
            createClasses("cities/" + cityName + "/schools/" + "школа " + i + "/classes/");
        }
        for (int i = 1;  i <= 10; i++) {
            db.document("cities/" + cityName + "/schools/" + "гимназия " + i).set(test);
            Log.d("aboba", "cities/" + cityName + "/schools/" + "гимназия " + i + "/classes/");
            createClasses("cities/" + cityName + "/schools/" + "гимназия " + i + "/classes/");
        }
    }

    void createClasses(String path) {
        for (int i = 1; i <= 11; i++) {
            Map<String, String> test = new HashMap<>();
            test.put("test", "test");

            db.document(path + i + 'А').set(
                    test
            );
            db.document(path + i + 'Б').set(
                    test
            );
            db.document(path + i + 'В').set(
                    test
            );
            db.document(path + i + 'Г').set(
                    test
            );
            db.document(path + i + 'Д').set(
                    test
            );
            db.document(path + i + 'Е').set(
                    test
            );

        }
    }

}