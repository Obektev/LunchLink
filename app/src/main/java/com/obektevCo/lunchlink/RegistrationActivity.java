package com.obektevCo.lunchlink;

import static com.obektevCo.lunchlink.FirebaseIntegration.getNamesFromDBC;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.concurrent.TimeUnit;

public class RegistrationActivity extends AppCompatActivity {

    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private final String TAG = "123123213212121";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        int flag = getIntent().getIntExtra("registration_flag", 0);


        switch (flag) {
            case 1:
                goProfileInfoLayout();
                break;
            case 2:
                goCityChoosingLayout();
                break;
            default:

                mAuth.setLanguageCode("ru");

                EditText phone_number_input = findViewById(R.id.mobile_phone_input);
                AppCompatButton continue_button = findViewById(R.id.continue_button);

                continue_button.setOnClickListener(view -> {
                    String phone_number = LunchLinkUtilities.formatPhoneNumber(phone_number_input.getText().toString()); // Format input phone number to E.164 Format
                    sendSMSCode(phone_number);
                });

                PhoneNumberFormattingTextWatcher textWatcher = new PhoneNumberFormattingTextWatcher();
                phone_number_input.addTextChangedListener(textWatcher);

                phone_number_input.setOnKeyListener((view, keyCode, keyevent) -> {
                    //If the keyevent is a key-down event on the "enter" button
                    if ((keyevent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        String phone_number = LunchLinkUtilities.formatPhoneNumber(phone_number_input.getText().toString()); // Format input phone number to E.164 Format
                        sendSMSCode(phone_number);
                        return true;
                    }
                    return false;
                });
                break;
        }
    }

    private void sendSMSCode(String phone_number) {
        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override // Auto verification of device, not SMS-Code!
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);

                signInWithPhoneAuthCredential(credential);
            }

            @Override // Wrong phone number, recaptcha and so on
            public void onVerificationFailed(@NonNull FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    LunchLinkUtilities.makeToast(getApplicationContext(), "Invalid phone number or reCaptcha hasn't been finished!");
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                } else if (e instanceof FirebaseAuthMissingActivityForRecaptchaException) {
                    // reCAPTCHA verification attempted with null Activity
                }
                // Show a message and update the UI
            }

            @Override // Code sent successfully → go to next window with verify button
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                LunchLinkUtilities.makeToast(getApplicationContext(), getString(R.string.code_sent));
                goVerifyLayout(); // Go next window with verify button
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
            }
        };

        // Send SMS-code
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phone_number)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // (optional) Activity for callback binding
                        // If no activity is passed, reCAPTCHA verification can not be used.
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    private void goVerifyLayout() {
        setContentView(R.layout.activity_registration_verify);

        AppCompatButton verify_button = findViewById(R.id.verify_button);
        verify_button.setOnClickListener(view -> {
            EditText code_input = findViewById(R.id.code_input);
            String sms_code = code_input.getText().toString().trim();
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, sms_code);
            signInWithPhoneAuthCredential(credential);
        });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");

                        FirebaseUser user = task.getResult().getUser();
                        LunchLinkUtilities.makeToast(getApplicationContext(), getString(R.string.success));
                        assert user != null;

                        goProfileInfoLayout();

                    } else {
                        // Sign in failed, display a message and update the UI
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                            LunchLinkUtilities.makeToast(getApplicationContext(), getString(R.string.wrong_code));
                        }
                    }
                });
    }

    private void goProfileInfoLayout() {
        setContentView(R.layout.activity_registration_account_info);
        EditText name_input = findViewById(R.id.name_input);
        AppCompatButton continue_button = findViewById(R.id.continue_button);
        continue_button.setOnClickListener(view -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name_input.getText().toString())
                    .setPhotoUri(Uri.parse("https://i.imgur.com/7kMtTFQ.jpeg"))
                    .build();

            assert user != null;
            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            LunchLinkUtilities.makeToast(getApplicationContext(), getString(R.string.name_changed));
                            goCityChoosingLayout();

                            Log.d(TAG, "User profile updated.");
                        }
                    });
        });
    }

    private void goCityChoosingLayout() {
        setContentView(R.layout.activity_registration_school);
        String[] items = { getString(R.string.vitebsk), getString(R.string.minsk), getString(R.string.gomel), getString(R.string.grodno), getString(R.string.mogilev), getString(R.string.brest) };
        AutoCompleteTextView autoCompleteTextView = findViewById(R.id.auto_complete_textview1);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.list_item, items);

        autoCompleteTextView.setAdapter(arrayAdapter);

        autoCompleteTextView.setOnItemClickListener((adapterView, view, position, l) -> {
            String city_name = adapterView.getItemAtPosition(position).toString();
            setupSchoolsChoosing(city_name);
            TextInputLayout layout1 = findViewById(R.id.second_textinput);
            layout1.setVisibility(View.VISIBLE);
            LunchLinkUtilities.makeToast(getApplicationContext(), getString(R.string.got_schools));
        });

    }

    private void setupSchoolsChoosing(String chosenCity) {
        getNamesFromDBC(String.format("cities/%s/schools", chosenCity), schoolNames -> {
            Log.d("Got schools", schoolNames.toString());
            AutoCompleteTextView autoCompleteTextView = findViewById(R.id.auto_complete_textview2);
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.list_item, schoolNames);

            autoCompleteTextView.setAdapter(arrayAdapter);

            autoCompleteTextView.setOnItemClickListener((adapterView, view, position, l) -> {
                String schoolName = adapterView.getItemAtPosition(position).toString();
                TextInputLayout textInputLayout = findViewById(R.id.third_textinput);
                textInputLayout.setVisibility(View.VISIBLE);
                LunchLinkUtilities.makeToast(getApplicationContext(), getString(R.string.got_classes));
                setupClassChoosing(chosenCity, schoolName);
            });
        });
    }


    private void setupClassChoosing(String city_name, String school_name) {

        getNamesFromDBC(String.format("cities/%s/schools/%s/classes", city_name, school_name), classes_names -> {

            AutoCompleteTextView autoCompleteTextView = findViewById(R.id.auto_complete_textview3);

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.list_item, classes_names);

            autoCompleteTextView.setAdapter(arrayAdapter);

            autoCompleteTextView.setOnItemClickListener((adapterView, view, position, l) -> {
                String class_name = adapterView.getItemAtPosition(position).toString();
                AppCompatButton continue_button = findViewById(R.id.continue_button);
                continue_button.setVisibility(View.VISIBLE);
                continue_button.setOnClickListener(v -> {
                    FirebaseIntegration.setUserCity(getApplicationContext(), city_name);
                    FirebaseIntegration.setUserSchool(getApplicationContext(), school_name); // Set user school in firebase and UserSettings ←
                    FirebaseIntegration.setUserClass(getApplicationContext(), class_name);

                    finishActivity(0);

                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                });
            });
        });
    }
}
