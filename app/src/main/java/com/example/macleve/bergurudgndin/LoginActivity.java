package com.example.macleve.bergurudgndin;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mEmail, mPassword;
    private Button mLoginButton;
    private TextView mRegisterTv;
    private FirebaseAuth mAuth;

    private static String usertype;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //create instance
        mAuth = FirebaseAuth.getInstance();
        //link to xml
        mEmail = findViewById(R.id.emailET);
        mPassword = findViewById(R.id.passwordET);
        mLoginButton = findViewById(R.id.btnLogin);
        mRegisterTv = findViewById(R.id.registerTV);

        //set listener
        mLoginButton.setOnClickListener(this);
        mRegisterTv.setOnClickListener(this);

    }

    private void login(){
        String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter email email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    finish();
                    //startActivity(new Intent(LoginActivity.this, MainMenu.class));
                    startActivity(new Intent(LoginActivity.this, SplashScreen.class));

                }else{
                    Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();
        if(mAuth.getCurrentUser()!=null){
            //already login
           // Intent intent = new Intent(LoginActivity.this, MainMenu.class);
            Intent intent = new Intent(LoginActivity.this, SplashScreen.class);
            finish();
            startActivity(intent);
        }
    }


    @Override
    public void onClick(View v) {
        if(v == mLoginButton){
            login();
        }else if (v == mRegisterTv){
            finish();
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        }
    }
}
