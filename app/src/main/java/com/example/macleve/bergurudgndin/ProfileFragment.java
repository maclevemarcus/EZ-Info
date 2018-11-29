package com.example.macleve.bergurudgndin;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static android.support.constraint.Constraints.TAG;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    private TextView mName, mEmail, mAddress,mContact,mRole;
    private Button mUpdateButton, mMainBtn, mChooseBtn;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private StorageTask mUploadTask;

    //private Uri filepath;
    private CircleImageView imageView;
    private final int PICK_IMAGE_REQUEST = 1;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private ProgressBar mProgressBar;
    private Uri mImageUri;
    private Context context;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        return inflater.inflate(R.layout.activity_profile, container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //create instances to the database
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        context = getActivity().getApplicationContext();

        //link to xml
        mUpdateButton = view.findViewById(R.id.updateBtn);
        //mMainBtn = view.findViewById(R.id.mainBtn);
        mChooseBtn = view.findViewById(R.id.chooseBtn);
        imageView = view.findViewById(R.id.imageview);
        mProgressBar = view.findViewById(R.id.progressbar);

        mEmail = view.findViewById(R.id.emailTxt);
        mAddress = view.findViewById(R.id.addressTxt);
        mName = view.findViewById(R.id.nameTxt);
        mContact = view.findViewById(R.id.phoneNo);
        mRole = view.findViewById(R.id.roletxt);

        mUpdateButton.setOnClickListener(this);
        //mMainBtn.setOnClickListener(this);

        mChooseBtn.setOnClickListener(this);

        //view
        mProgressBar.setVisibility(View.INVISIBLE);

        final String uid = mAuth.getUid();

        //read data from db and assign it to snapshot obj
        //***************FOR USER************************
        mDatabase.child("User").child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {//
                    // run some code
                    //direct load image to imageview using storageReference
                    storageReference.child("images/users/"+ uid).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // Got the download URL for image
                            Picasso.with(context).load(uri).into(imageView);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                        }
                    });

                    User user = snapshot.getValue(User.class);

                    String name = user.getName();
                    String email = user.getEmail();
                    String address = user.getAddress();
                    String contact = user.getContact();

                    mName.setText(name);
                    mEmail.setText(email);
                    mAddress.setText(address);
                    mContact.setText(contact);
                    mRole.setText("Parent");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //*******************FOR STAFF**************************
       mDatabase.child("Staff").child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {//hasChild(mAuth.getUid())
                    // run some code
                    //direct load image to imageview using storageReference
                    storageReference.child("images/users/"+ uid).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // Got the download URL for image
                            Picasso.with(context).load(uri).into(imageView);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                        }
                    });
                    Staff staff = dataSnapshot.getValue(Staff.class);

                    String name = staff.getName();
                    String email = staff.getEmail();
                    String address = staff.getAddress();
                    String contact = staff.getContact();
                    String usertype = staff.getUsertype();

                    mName.setText(name);
                    mEmail.setText(email);
                    mAddress.setText(address);
                    mContact.setText(contact);
                    mRole.setText(usertype);

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void chooseImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), PICK_IMAGE_REQUEST);

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() !=null)
        {
            mImageUri = data.getData();
            Picasso.with(context).load(mImageUri).into(imageView);//using picasso to load image
            final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
            //alertDialog.setTitle("Alert");
            alertDialog.setMessage("Save image?");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            uploadImage();
                            dialog.dismiss();
                        }
                    });
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            // Reload current fragment
                            ProfileFragment fragment = new ProfileFragment();
                            replaceFragment(fragment);
                            dialog.dismiss();
                        }
                    });
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Do something after 3s = 3000ms
                    alertDialog.show();
                }
            }, 1000);
        }
    }
    //method to refresh current loaded fragment
    public void refreshFragment(Fragment someFragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.detach(someFragment);
        transaction.attach(someFragment);
        transaction.commit();
    }
    //method to replace current loaded fragment to another fragment
    public void replaceFragment(Fragment someFragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, someFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    //upload image method
    private void uploadImage(){

        if(mImageUri !=null){

            final String uid = mAuth.getCurrentUser().getUid();
            final StorageReference ref = storageReference.child("images/users/"+ uid);

            mUploadTask = ref.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot task) {

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressBar.setProgress(0);
                                    // Do something after 3s = 3000ms
                                    mProgressBar.setVisibility(View.INVISIBLE);
                                }
                            }, 3000);//delay reset of progressbar in sec

                            Toast.makeText(context,"Upload Successful",Toast.LENGTH_LONG).show();

                            //GOT THE USER PROFILE PIC STORAGE URL
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Uri downloadUrl = uri;
                                    //Toast.makeText(getActivity().getBaseContext(), "Upload success! URL - " + downloadUrl.toString() , Toast.LENGTH_SHORT).show();

                                }
                            });

                        }

                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context,"Failed",Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                            mProgressBar.setVisibility(View.VISIBLE);
                            mProgressBar.setProgress((int) progress);
                            //to set the progressbar invisible after 5 sec
                        }
                    });
        }else{
            Toast.makeText(context, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onClick(View v) {

        String name = mName.getText().toString().trim();
        String email = mEmail.getText().toString().trim();
        String address = mAddress.getText().toString().trim();
        String contact = mContact.getText().toString().trim();
        String usertype = mRole.getText().toString().trim();

        if (v == mUpdateButton) {
            Intent intent = getActivity().getIntent();

            intent.putExtra("uid",mAuth.getUid());
            intent.putExtra("name",name);
            intent.putExtra("email",email);
            intent.putExtra("address",address);
            intent.putExtra("contact",contact);
            intent.putExtra("usertype",usertype);
            UpdateUserFragment fragment = new UpdateUserFragment();
            replaceFragment(fragment);
        }

        else if(v==mChooseBtn){
            chooseImage();
        }/*
        else if(v==mBtnUploadIMG){
            if(mUploadTask !=null && mUploadTask.isInProgress()){
                Toast.makeText(context, "Upload in progress", Toast.LENGTH_SHORT).show();
            }else{
                uploadImage();
            }
        }*/
    }

}
