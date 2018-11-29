package com.example.macleve.bergurudgndin;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mEmail, mPassword, mName, mAddress,mContact;
    private TextView mSignInText;
    private Button mRegisterButton;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private RadioButton radioStaff,radioParent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //create instance
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //link to xml
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mName = findViewById(R.id.name);
        mAddress = findViewById(R.id.address);
        mContact= findViewById(R.id.phoneNo);
        mSignInText = findViewById(R.id.SignInText);
        mRegisterButton = findViewById(R.id.RegisterButton);

        //add listener
        mSignInText.setOnClickListener(this);
        mRegisterButton.setOnClickListener(this);

        //radio button
        radioStaff= findViewById(R.id.radiostaff);
        radioParent = findViewById(R.id.radioparent);

    }
    /*if(uid.contains(("Staff"))){
        Log.d("TEST",uid);
    }*/

    private void register(){

        String email= mEmail.getText().toString().trim();
        String password= mPassword.getText().toString().trim();
        String name= mName.getText().toString().trim();
        String address= mAddress.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(address)) {
            Toast.makeText(this, "Please enter your address", Toast.LENGTH_SHORT).show();
            return;
        }

        //method to create use using firebaseauth
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    registerUserInfo();
                    finish();
                    startActivity(new Intent(RegisterActivity.this, MainMenu.class ));
                }
            }
        });

    }

    private void registerUserInfo(){
        String uid = mAuth.getCurrentUser().getUid();
        String email = mEmail.getText().toString().trim();
        String name = mName.getText().toString().trim();
        String address = mAddress.getText().toString().trim();
        String contact = mContact.getText().toString().trim();

        if(radioStaff.isChecked()) {
            // is checked
            String userType = "staff";
            Staff staff = new Staff(uid,name,email,address,contact,userType);
            mDatabase.child("Staff").child(uid).setValue(staff).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(RegisterActivity.this,"Register Success", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(RegisterActivity.this,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
        else if(radioParent.isChecked()) {
            //String userType = "staff";
            User user = new User(uid,name,email,address,contact);
            mDatabase.child("User").child(uid).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(RegisterActivity.this,"Register Success", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(RegisterActivity.this,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        // not checked

    }
    @Override
    public void onClick(View v) {
        if( v == mRegisterButton){
            register();
        }else if( v == mSignInText){
            finish();
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        }
    }

}
