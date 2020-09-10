import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.HashMap;
import java.util.Map;

public class EditEmailAndNumber extends AppCompatActivity {

    public static final String TAG = "TAG";

    EditText editNewEmail, editNewPhoneNumber;
    Button editnewEmailBtn, editNewPhoneNumberBtn, finishEditBtn;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser user;
    String userId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_email_and_number);

        editNewEmail = findViewById(R.id.editNewEmail);
        editnewEmailBtn = findViewById(R.id.editNewEmailBtn);

        editNewPhoneNumber = findViewById(R.id.editNewPhoneNumber);
        editNewPhoneNumberBtn = findViewById(R.id.editNewPhoneNumberBtn);

        finishEditBtn = findViewById(R.id.finishEditBtn);

        firebaseAuth        = FirebaseAuth.getInstance();
        firebaseFirestore   = FirebaseFirestore.getInstance();

        user = firebaseAuth.getCurrentUser();
        userId = firebaseAuth.getCurrentUser().getUid();

        final DocumentReference documentReference;

        documentReference = firebaseFirestore.collection("users").document(userId);

        editnewEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String newEmail = editNewEmail.getText().toString().trim();
                final EditText confirmEmailChange = new EditText(v.getContext());
                AlertDialog.Builder eMailValidationDialog = new AlertDialog.Builder(v.getContext());
                eMailValidationDialog.setTitle("Changing - Email");
                eMailValidationDialog.setMessage("Please retype your new email to change your email address");
                eMailValidationDialog.setView(confirmEmailChange);

                eMailValidationDialog.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newEmailConfirmation = confirmEmailChange.getText().toString();
                        if (newEmail.equals(newEmailConfirmation)){
                            user.updateEmail(newEmail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(EditEmailAndNumber.this, "Email has been successfully updated",Toast.LENGTH_SHORT).show();
                                documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                        documentReference.update(
                                                "Email", newEmail
                                        );
                                    }
                                });
                                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "onSuccess: user profile is updated for: " + newEmail);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG,"onFailure: " + e.toString());
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(EditEmailAndNumber.this, "Error! Please retry!",Toast.LENGTH_SHORT).show();
                            }
                        });

                        }
                        

                    }

                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

            }
        });

        editNewPhoneNumberBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String newPhoneNumber = editNewPhoneNumber.getText().toString().trim();
                final EditText confirmPhoneNumberChange = new EditText(v.getContext());
                AlertDialog.Builder phoneNumberValidationDialog = new AlertDialog.Builder(v.getContext());
                phoneNumberValidationDialog.setTitle("Changing - Phone Number");
                phoneNumberValidationDialog.setMessage("Please retype your phone number to change your phone number");
                phoneNumberValidationDialog.setView(confirmPhoneNumberChange);

                phoneNumberValidationDialog.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newPhoneNumberConfirmation = confirmPhoneNumberChange.getText().toString();
                        if (newPhoneNumber.equals(newPhoneNumberConfirmation)){
                            documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                @Override
                                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                    documentReference.update(
                                            "Phone Number", "+673 " + newPhoneNumber
                                    );
                                }
                            });
                        }
                    }
                    
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        
                    }
                });
                
            }
        });

        finishEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

    }
}
