import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    public static final String TAG = "TAG";

    EditText mFullName, mEmail, mIc,
            mPhone, mPassword, mCPassword;
    Button mRegisterBtn;
    TextView loginTxt;
    Spinner mDay, mMonth, mYear;
    ProgressBar mProgress;
    FirebaseFirestore fStore;
    FirebaseAuth firebaseAuth;
    String fullName, eMail, DOB, icNumber,
            phoneNumber, password, confirmPassword,
            userID, day, month, year;

    boolean leapYear;
    
    String encryptedPassword = " ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // EditText

        mFullName = findViewById(R.id.fullNameRegis);
        mEmail = findViewById(R.id.eMailRegis);
        mIc = findViewById(R.id.icNumberRegis);
        mPhone = findViewById(R.id.phoneNumberRegis);
        mPassword = findViewById(R.id.passwordRegis);
        mCPassword = findViewById(R.id.confirmPasswordRegis);

        // Buttons

        mRegisterBtn = findViewById(R.id.registerButton);
        loginTxt = findViewById(R.id.registerLoginBtn);

        //Spinners
        mDay                        = (Spinner) findViewById(R.id.spinnerDay);
        ArrayAdapter<String> arrayAdapterDay = new ArrayAdapter<String>(Register.this,
                android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.day));
        arrayAdapterDay.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mDay.setAdapter(arrayAdapterDay);

        mMonth                      = (Spinner) findViewById(R.id.spinnerMonth);
        ArrayAdapter<String> arrayAdapterMonth = new ArrayAdapter<String>(Register.this,
                android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.month));
        arrayAdapterMonth.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mMonth.setAdapter(arrayAdapterMonth);

        mYear                       = (Spinner) findViewById(R.id.spinnerYear);
        ArrayAdapter<String> arrayAdapterYear = new ArrayAdapter<String>(Register.this,
                android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.year));
        arrayAdapterYear.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mYear.setAdapter(arrayAdapterYear);

        // Progress Bar
        mProgress = findViewById(R.id.regisProgress);

        // Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                fullName = mFullName.getText().toString();
                eMail = mEmail.getText().toString().trim();
                icNumber = mIc.getText().toString().trim();
                phoneNumber = mPhone.getText().toString().trim();
                password = mPassword.getText().toString().trim();
                confirmPassword = mCPassword.getText().toString().trim();
                day = mDay.getSelectedItem().toString();
                month = mMonth.getSelectedItem().toString();
                year = mYear.getSelectedItem().toString();

                if (TextUtils.isEmpty(fullName)){
                    mFullName.setError("Your full name is required!");
                    return;
                }

                if (TextUtils.isEmpty(eMail)){
                    mEmail.setError("eMail required!");
                    return;
                }


                dateChecker();

                if (TextUtils.isEmpty(icNumber)){
                    mIc.setError("Your IC number is required!");
                    return;
                }

                if (icNumber.length() != 8){
                    mIc.setError("Your IC number is incomplete.");
                    Toast.makeText(Register.this, "Please input your IC number without the dash.", Toast.LENGTH_LONG).show();
                    return;
                }

                if (TextUtils.isEmpty(phoneNumber)){
                    mPhone.setError("Your phone number is required!");
                    return;
                }

                if (phoneNumber.length() != 7){
                    mPhone.setError("Please input Brunei-registered number.");
                }

                if (TextUtils.isEmpty(password)){
                    mPassword.setError("Password required!");
                    return;
                }

                if (password.length() < 6){
                    mPassword.setError("Password must be no less than 6 characters");
                    return;
                }

                if (confirmPassword.length() < 6){
                    mCPassword.setError("Password must be no less than 6 characters");
                    return;
                }

                if (password.equals(confirmPassword)){
                    passwordEncrypting();
                    mProgress.setVisibility(View.VISIBLE);
                    firebaseAuth.createUserWithEmailAndPassword(eMail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(Register.this, "User Created", Toast.LENGTH_SHORT).show();
                                userID = firebaseAuth.getCurrentUser().getUid();

                                DocumentReference documentReference;
                                documentReference = fStore.collection("users").document(userID);
                                //Saves data to a collection called users, where the documents are icNumber.

                                Map<String,Object> user = new HashMap<>();
                                user.put("Full Name", fullName);
                                user.put("Email", eMail);
                                user.put("Date of Birth", DOB);
                                user.put("IC Number", icNumber);
                                user.put("Phone Number (+673)", "+673 " + phoneNumber);
                                user.put("Password", encryptedPassword);



                                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "onSuccess: user profile is created for: " + icNumber);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG,"onFailure: " + e.toString());
                                    }
                                });

                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            } else {
                                Toast.makeText(Register.this, "Error!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                mProgress.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                }
            }
        });

        loginTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Login.class));
            }
        });
    }
    
    private void passwordEncrypting() {
        Crypto crypto = new BasicCrypto();
        String dataCrypting = password;
        String encryptedData = new String(crypto.encrypt(dataCrypting.getBytes()));
        encryptedPassword = encryptedData;
//        Toast.makeText(Register.this, "Password is " + encryptedPassword, Toast.LENGTH_SHORT).show();
    }

    private void dateChecker() {

        day = mDay.getSelectedItem().toString();
        month = mMonth.getSelectedItem().toString();
        year = mYear.getSelectedItem().toString();

        int intDay = Integer.parseInt(day);
        int intMonth = Integer.parseInt(month);
        int intYear = Integer.parseInt(year);

        int noOfDays = 0;


        switch (intMonth){
            case 1:

            case 3:

            case 5:

            case 7:

            case 8:

            case 10:

            case 12:
                noOfDays = 31;
                break;

            case 2:
                if ((intYear % 4) == 0) {
                    if ((intYear % 100) != 0) {
                        leapYear = true;
                    } else {
                        leapYear = false;
                        if ((intYear % 400) == 0) {
                            leapYear = true;
                        } else {
                            leapYear = false;
                        }
                    }
                } else {
                    leapYear = false;
                }
                if (leapYear == true) {
                    noOfDays = 29;
                    break;
                } else {
                    noOfDays = 28;
                    break;
                }


            case 4:

            case 6:

            case 9:

            case 11:
                noOfDays = 30;
                break;

            default:
                System.out.println("Error");
        }

        System.out.println(noOfDays);

        if (intDay <= noOfDays){
            DOB = day + " - " + month + " - " + year;

            System.out.println(DOB);
        } else {
            System.out.println("Error");
            Toast.makeText(Register.this, "Error! Please check your dates", Toast.LENGTH_LONG).show();
        }


    }

}
