import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EditEmailAndNumber extends AppCompatActivity {

    public static final String TAG = "TAG";

    EditText editNewEmail, editNewPhoneNumber;
    Button editnewEmailBtn, editNewPhoneNumberBtn, finishEditBtn;

    EditText confirmedNewEmail, confirmedNewPhoneNumber;
    Button confirmedNewEmailBtn, confirmedNewPhoneNumberBtn;
    TextView mChangePhoneNumber, mChangeEmail,
            mConfirmPhoneNumber, mConfirmEmail;

    LinearLayout firstLayout, secondLayout, thirdLayout, forthLayout;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser user;
    String userId;
    String newEmailString, confirmedNewEmailString, newNumberString, confirmedNewNumberString;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_email_and_number);

        mChangeEmail = findViewById(R.id.changeEmailTextView);
        firstLayout = findViewById(R.id.firstLinearLayout);
        editNewEmail = findViewById(R.id.editNewEmail);
        editnewEmailBtn = findViewById(R.id.editNewEmailBtn);

        mConfirmEmail = findViewById(R.id.confirmEmailTextView);
        secondLayout = findViewById(R.id.secondLinearLayout);
        confirmedNewEmail = findViewById(R.id.confirmNewEmail);
        confirmedNewEmailBtn = findViewById(R.id.confirmNewEmailBtn);


        mChangePhoneNumber = findViewById(R.id.changePhoneNumberTextView);
        thirdLayout = findViewById(R.id.thirdLinearLayout);
        editNewPhoneNumber = findViewById(R.id.editNewPhoneNumber);
        editNewPhoneNumberBtn = findViewById(R.id.editNewPhoneNumberBtn);

        mConfirmPhoneNumber = findViewById(R.id.confirmPhoneNumberTextView);
        forthLayout = findViewById(R.id.forthLinearLayout);
        confirmedNewPhoneNumber = findViewById(R.id.confirmNewPhoneNumber);
        confirmedNewPhoneNumberBtn = findViewById(R.id.confirmNewPhoneNumberBtn);


        finishEditBtn = findViewById(R.id.finishEditBtn);

        firebaseAuth        = FirebaseAuth.getInstance();
        firebaseFirestore   = FirebaseFirestore.getInstance();

        user = firebaseAuth.getCurrentUser();
        userId = firebaseAuth.getCurrentUser().getUid();

        final DocumentReference documentReference  = firebaseFirestore.collection("users").document(userId);

        editnewEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newEmailString = editNewEmail.getText().toString().trim();
                if(newEmailString.isEmpty()){
                    Toast.makeText(EditEmailAndNumber.this, "Please insert a new email you wish to use", Toast.LENGTH_LONG).show();
                } else {
                    hideEditingOfPhoneNo();
                }

            }
        });

        confirmedNewEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmedNewEmailString = confirmedNewEmail.getText().toString().trim();
                if(newEmailString.equals(confirmedNewEmailString)){
                    user.updateEmail(newEmailString).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(EditEmailAndNumber.this, "Email has been successfully updated",Toast.LENGTH_SHORT).show();
                            documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                @Override
                                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                    documentReference.update("Email", newEmailString);
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

        });

        editNewPhoneNumberBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newNumberString = editNewPhoneNumber.getText().toString().trim();
                if(newNumberString.isEmpty()){
                    Toast.makeText(EditEmailAndNumber.this, "Please insert a new number you wish to use", Toast.LENGTH_LONG).show();
                }else{
                    hideEditingOfEmail();
                }
            }
        });

        confirmedNewPhoneNumberBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmedNewNumberString = confirmedNewPhoneNumber.getText().toString().trim();
                if (newNumberString.equals(confirmedNewNumberString)){
                    documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                            documentReference.update("Phone Number", "+673 " + newNumberString);
                        }
                    });
                    Toast.makeText(EditEmailAndNumber.this, "Phone Number has been updated successfully", Toast.LENGTH_LONG).show();
                }

            }
        });





        finishEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

    }

    private void hideEditingOfEmail() {
        // Makes the application focus on phone number changing:
        mConfirmPhoneNumber.setVisibility(View.VISIBLE);
        forthLayout.setVisibility(View.VISIBLE);

        mChangeEmail.setVisibility(View.INVISIBLE);
        firstLayout.setVisibility(View.INVISIBLE);
        mConfirmEmail.setVisibility(View.INVISIBLE);
        secondLayout.setVisibility(View.INVISIBLE);
    }

    private void hideEditingOfPhoneNo() {
        // Makes the application focus on eMail changing:
        mConfirmEmail.setVisibility(View.VISIBLE);
        secondLayout.setVisibility(View.VISIBLE);

        mChangePhoneNumber.setVisibility(View.INVISIBLE);
        thirdLayout.setVisibility(View.INVISIBLE);
        mConfirmPhoneNumber.setVisibility(View.INVISIBLE);
        forthLayout.setVisibility(View.INVISIBLE);
    }
}
