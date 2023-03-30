package com.test.logintesttwo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    EditText emailRegText;
    EditText passRegText;
    EditText firstNameText;
    EditText lastNameText;
    Button regBttn;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailRegText = findViewById(R.id.emailReg);
        passRegText = findViewById(R.id.passReg);
        firstNameText = findViewById(R.id.firstname);
        lastNameText = findViewById(R.id.lastname);
        regBttn = findViewById(R.id.signUp);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        regBttn.setOnClickListener(view ->{
            createUser();
        });
    }

    private void createUser(){
        String email = emailRegText.getText().toString();
        String password = passRegText.getText().toString();

        String first_name = firstNameText.getText().toString();
        String last_name = lastNameText.getText().toString();

        if (TextUtils.isEmpty(email)){
            emailRegText.setError("Please fill in the email address");
            emailRegText.requestFocus();
        }
        else if (TextUtils.isEmpty(password)) {
            passRegText.setError("Please fill in a password");
            passRegText.requestFocus();
        } else{
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(RegisterActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();

                        //Set display name
                        UserProfileChangeRequest userProfileChangeReq = new UserProfileChangeRequest.Builder()
                                .setDisplayName(first_name).build();

                        FirebaseUser user = task.getResult().getUser();
                        user.updateProfile(userProfileChangeReq);

                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    } else{
                        Toast.makeText(RegisterActivity.this, "Error in registration: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}