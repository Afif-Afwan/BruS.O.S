package com.example.fyp_02;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class HealthComplications extends AppCompatActivity {

    TextView addHealthComplicationsButton;

    EditText healthComplications, description;

    String healthComplicationsString, descriptionString;

    // user id
    String userID;

    // firebase authentication
    FirebaseAuth firebaseAuth;

    // Firestore instance
    FirebaseFirestore mFirestore;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_complications);

        addHealthComplicationsButton = findViewById(R.id.addHealthComplicationButton);

        healthComplications = findViewById(R.id.healthComplications);
        description = findViewById(R.id.descriptionHealthComp);

        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();
        mFirestore = FirebaseFirestore.getInstance();


        addHealthComplicationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                healthComplicationsString = healthComplications.getText().toString().trim();
                descriptionString = description.getText().toString().trim();

                uploadHealthComplications(healthComplicationsString, descriptionString);

            }
        });


    }
    public void uploadHealthComplications(String health, String desc){
        Map<String, Object> doc = new HashMap<>();
        doc.put("Health", health);
        doc.put("Description", desc);

        mFirestore.collection("users").document(userID).collection("Health Complications")
                .document(health).set(doc).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(HealthComplications.this, "Data Uploaded Successfully!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(HealthComplications.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
