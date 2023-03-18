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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText emailLoginText;
    EditText passLoginText;
    Button signInButton;
    Button regLoginBttn;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailLoginText = findViewById(R.id.emailLogin);
        passLoginText = findViewById(R.id.passLogin);
        signInButton = findViewById((R.id.login));
        regLoginBttn = findViewById(R.id.register);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //register button
        regLoginBttn.setOnClickListener(view ->{
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        //sign in button
        signInButton.setOnClickListener(view ->{
            signInUser();
        });



    }
    private void signInUser(){
        String email = emailLoginText.getText().toString();
        String password = passLoginText.getText().toString();

        if (TextUtils.isEmpty(email)){
            emailLoginText.setError("Please fill in the email address");
            emailLoginText.requestFocus();
        }
        else if (TextUtils.isEmpty(password)) {
            passLoginText.setError("Please fill in a password");
            passLoginText.requestFocus();
        } else{
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>(){
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(LoginActivity.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    } else{
                        Toast.makeText(LoginActivity.this, "Error in login: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}