package com.example.iertest2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class ChangePassword extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseFirestore firebaseFirestore;
    String userID;

    EditText mOldPassword, mNewPassword, mConfirmNewPassword;
    String oldPassword, newPassword, confirmNewPassword;
    Button mConfirmChange;
    TextView mResetForgottenPassword;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        user = firebaseAuth.getCurrentUser();
        userID = firebaseAuth.getCurrentUser().getUid();

        mOldPassword = findViewById(R.id.oldPassword);
        mNewPassword = findViewById(R.id.newPassword);
        mConfirmNewPassword = findViewById(R.id.confirmNewPassword);

        mConfirmChange = findViewById(R.id.confirmChange);

        mResetForgottenPassword = findViewById(R.id.resetForgottenOldPassword);




        DocumentReference documentReference;

        documentReference = firebaseFirestore.collection("users").document(userID);


        //Incase User press the Forgot password button
        mResetForgottenPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText resetMail = new EditText(v.getContext());
                final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                passwordResetDialog.setTitle("Password Reset");
                passwordResetDialog.setMessage("Please enter your email to receive the reset link");
                passwordResetDialog.setView(resetMail);
                passwordResetDialog.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //This code extract the email entered, and sends an email to reset the link.
                        String mail = resetMail.getText().toString();
                        firebaseAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(ChangePassword.this, "Reset Link is sent!", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ChangePassword.this, "Error. Reset Link is not sent!",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                passwordResetDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                passwordResetDialog.show();
            }
        });

        mConfirmChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                newPassword = mNewPassword.getText().toString().trim();
                confirmNewPassword = mConfirmNewPassword.getText().toString().trim();

                if (newPassword.equals(confirmNewPassword)){
                    user.updatePassword(newPassword);
                    Toast.makeText(ChangePassword.this, "Password is successfully updated!", Toast.LENGTH_LONG).show();
                }

            }
        });


    }
}