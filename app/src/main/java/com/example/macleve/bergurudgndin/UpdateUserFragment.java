package com.example.macleve.bergurudgndin;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

import static android.support.constraint.Constraints.TAG;

public class UpdateUserFragment extends Fragment implements View.OnClickListener {

    private EditText mName, mEmail, mAddress,mContact;
    private Button mUpdateBtn, mDeleteBtn;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private Context context;
    private Dialog mDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View v= inflater.inflate(R.layout.activity_update_user, container,false);

        Button deleteBtn = v.findViewById(R.id.delButton);

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//if want to open other class will use intent thats when we need to call getactivity()
                Log.d("HAHA","MASUK");
                    mDialog = new Dialog(getActivity());
                    //mDialog.show();
                    ShowDeletePopUp();
                }

        });
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //create instances to the database
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        context = getActivity().getApplicationContext();
        //link to xml
        mName = view.findViewById(R.id.nameTxt);
        mEmail = view.findViewById(R.id.emailTxt);
        mAddress = view.findViewById(R.id.addressTxt);
        mContact = view.findViewById(R.id.contactEt);
        mUpdateBtn = view.findViewById(R.id.updateBtn);
        mUpdateBtn.setOnClickListener(this);

        Intent intent = getActivity().getIntent();
        String name = intent.getStringExtra("name");
        String email = intent.getStringExtra("email");
        String address = intent.getStringExtra("address");
        String contact = intent.getStringExtra("contact");
        //String usertype = intent.getStringExtra("usertype");

        //display current user info
        mName.setText(name);
        mEmail.setText(email);
        mAddress.setText(address);
        mContact.setText(contact);
    }
    private void updateUser() {
        Intent intent = getActivity().getIntent();
        //String uid = mAuth.getCurrentUser().getUid();//to delete own account
        String uid = intent.getStringExtra("uid");//can use the sent parameter to delete other(for admin)
        String name = mName.getText().toString().trim();
        String email = mEmail.getText().toString().trim();
        String address = mAddress.getText().toString().trim();
        String contact = mContact.getText().toString().trim();

        String usertype = intent.getStringExtra("usertype");
        if(!usertype.equalsIgnoreCase("staff")){//for update parent

            User user = new User(uid, name, email, address,contact);
            mDatabase.child("User").child(uid).setValue(user)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(context, "Update User Successful", Toast.LENGTH_SHORT).show();
                                }
                            }
                    }
                    );
        }else{//for staff update
            Staff staff = new Staff(uid, name, email, address,contact,usertype);
            mDatabase.child("Staff").child(uid).setValue(staff)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(context, "Update Staff Successful", Toast.LENGTH_SHORT).show();
                                }
                            }
                    }
                    );
        }

        ProfileFragment fragment = new ProfileFragment();
        replaceFragment(fragment);
        //setcheck for profile on nav menu when profile cardview is clicked
        NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
        //navigationView.getMenu().getItem(1).setChecked(true); 1st way
        navigationView.setCheckedItem(R.id.nav_profile);//2nd way
    }

    private void deleteUser(){
        Intent intent = getActivity().getIntent();
        //String userUid = mAuth.getCurrentUser().getUid();
        String uid = intent.getStringExtra("uid");

        String usertype = intent.getStringExtra("usertype");
        if(!usertype.equalsIgnoreCase("staff")) {//for delete parent
            mDatabase.child("User").child(uid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(context, "Delete User Successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(context, LoginActivity.class));
                    }
                }
            });
        }else{//delete staff
            mDatabase.child("Staff").child(uid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(context, "Delete Staff Successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(context, LoginActivity.class));
                    }
                }
            });
        }
    }
    //method to replace current loaded fragment to another fragment
    public void replaceFragment(Fragment someFragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, someFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void ShowDeletePopUp(){
        TextView txtclose;
        final EditText emailet,passet;
        Button deleteBtnD ;

        mDialog.setContentView(R.layout.deleteuserdialog);
        txtclose = mDialog.findViewById(R.id.txtclose);
        emailet = mDialog.findViewById(R.id.emailEt);
        passet = mDialog.findViewById(R.id.passEt);

        deleteBtnD = mDialog.findViewById(R.id.deleteBtnD);
        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        deleteBtnD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailet.getText().toString().trim();
                String pass = passet.getText().toString().trim();
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                // Get auth credentials from the user for re-authentication. The example below shows
                // email and password credentials but there are multiple possible providers,
                // such as GoogleAuthProvider or FacebookAuthProvider.
                AuthCredential credential = EmailAuthProvider
                        .getCredential(email, pass);

                // Prompt the user to re-provide their sign-in credentials
                user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                user.delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d(TAG, "User account deleted.");
                                                    Intent intent = new Intent(context,LoginActivity.class);
                                                    startActivity(intent);
                                                }
                                            }
                                        });
                            }
                        });
                deleteUser();
                mDialog.dismiss();
            }//end of onclick
        });

        mDialog.show();

    }
    @Override
    public void onClick(View v) {
        if (v == mUpdateBtn) {
            updateUser();
        }
            //to delete user from firebase authentication
           /* FirebaseUser user = mAuth.getCurrentUser();
            user.delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("message", "User account deleted.");
                            }
                        }
                    });*/
            //deleteUser();

    }
}
