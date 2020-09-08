import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class Profile extends AppCompatActivity {

    TextView mFullName, mDOB, mIcNo, mPhoneNum, mEmail;

    Button mLogoutBtn, mChangePasswordBtn, mEditBtn, mUpdatePPBtn;

    ImageView mProfilePicture;

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseFirestore firebaseFirestore;

    String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFullName = findViewById(R.id.fullNameTextView);
        mDOB = findViewById(R.id.dateOfBirthTextView);
        mIcNo = findViewById(R.id.icNumberTextView);
        mPhoneNum = findViewById(R.id.phoneNumberTextView);
        mEmail = findViewById(R.id.eMailTextView);
        mLogoutBtn = findViewById(R.id.logoutBtn);
        mChangePasswordBtn = findViewById(R.id.changePasswordBtn);
        mEditBtn = findViewById(R.id.editBtn);

        mProfilePicture = findViewById(R.id.profilePictureIV);
        mUpdatePPBtn = findViewById(R.id.updatePPBtn);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        user = firebaseAuth.getCurrentUser();
        userID = firebaseAuth.getCurrentUser().getUid();
        
        StorageReference profileReference = storageReference.child("useres/"+ firebaseAuth.getCurrentUser().getUid() + "/profile_image.jpg");
        profileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(mProfilePicture);
            }
        });

        DocumentReference documentReference;

        documentReference = firebaseFirestore.collection("users").document(userID);

        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                mFullName.setText(documentSnapshot.getString("Full Name"));
                mDOB.setText(documentSnapshot.getString("Date of Birth"));
                mIcNo.setText(documentSnapshot.getString("IC Number"));
                mPhoneNum.setText(documentSnapshot.getString("Phone Number (+673)"));
                mEmail.setText(documentSnapshot.getString("Email"));
            }
        });

        mChangePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText resetPassword = new EditText(v.getContext());
                resetPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                passwordResetDialog.setTitle("Change Password");
                passwordResetDialog.setMessage("Please enter a new password");
                passwordResetDialog.setView(resetPassword);
                passwordResetDialog.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newPassword = resetPassword.getText().toString();

                        user.updatePassword(newPassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(Profile.this, "Password Reset Successfully.", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Profile.this, "Password Reset Failed.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                passwordResetDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                passwordResetDialog.create().show();
            }
        });

//        mUpdatePPBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //Opens the gallary
//                Intent openGallaryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(openGallaryIntent, 1000);
//            }
//        });

        mEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), EditEmailAndNumber.class));
            }
        });

        mLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                startActivity(new Intent(getApplicationContext(),Login.class));
                finish();
            }
        });
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1000){
            Uri imageUri = data.getData();
//            mProfilePicture.setImageURI(imageUri);
            
            uploadImageToFirebase(imageUri);

            
        }
    }
    
    private void uploadImageToFirebase(Uri imageUri) {
        //upload image to firebase
        final StorageReference fileReference = storageReference.child("useres/"+ firebaseAuth.getCurrentUser().getUid() + "/profile_image.jpg");
        fileReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                Toast.makeText(MainActivity.this,"Profile Picture has been uploaded", Toast.LENGTH_LONG).show();
                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(mProfilePicture);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this,"Failed", Toast.LENGTH_LONG).show();

            }
        });
    }
}
