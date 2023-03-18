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

public class RegisterActivity extends AppCompatActivity {

    EditText emailRegText;
    EditText passRegText;
    Button regBttn;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailRegText = findViewById(R.id.emailReg);
        passRegText = findViewById(R.id.passReg);
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
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    } else{
                        Toast.makeText(RegisterActivity.this, "Error in registration: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}