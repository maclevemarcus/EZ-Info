package com.example.macleve.bergurudgndin;

import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
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

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.UUID;
import java.util.*;


import static android.app.Activity.RESULT_OK;
import static android.widget.NumberPicker.OnScrollListener.SCROLL_STATE_IDLE;

public class AddAnnouncementFragment extends Fragment implements View.OnClickListener{

    private static final int PICK_IMAGE_REQUEST = 1 ;
    private static final int PICKFILE_RESULT_CODE = 1 ;
    private EditText mAnnounceTitle, mDate, mDescription;
    private Button mCreateButton,mChoosefile;
    private FloatingActionButton mImageEdit;
    private DatabaseReference mDatabase;
    private StorageReference storageReference;
    private FirebaseStorage storage;
    private Uri mImageUri, mFileUri;
    private StorageTask mUploadTask;
    private ImageView mImageView;
    private ScrollView mScrollView;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private FirebaseAuth mAuth;
    private Context context;
    private static String usertype;
    private RadioButton radioStaff,radioParent;
    private LinearLayout radio;
    private EditText uploadfile;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_add_announcement, container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getActivity().getApplicationContext();
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //link to xml
        mAnnounceTitle = view.findViewById(R.id.announceet);
        mDate = view.findViewById(R.id.dateet);
        mDescription = view.findViewById(R.id.descriptionet);
        mImageEdit = view.findViewById(R.id.imgEdit);
        mImageView = view.findViewById(R.id.imageView);
        mImageView.setVisibility(View.INVISIBLE);//ADJUST LATER
        mScrollView = view.findViewById(R.id.scrollview);

        mCreateButton = view.findViewById(R.id.createEventBtn);
        //radio button
        radioStaff= view.findViewById(R.id.radioschool);
        radioParent = view.findViewById(R.id.radioOutside);
        radio = view.findViewById(R.id.radio);
        radio.setVisibility(View.INVISIBLE);//ADJUST LATER
        mImageEdit.setVisibility(View.INVISIBLE);//ADJUST LATER

        //upload file text
        uploadfile =view.findViewById(R.id.uploadfile);
        //button
        mChoosefile = view.findViewById(R.id.choosefile);
        //add listener
        mCreateButton.setOnClickListener(this);
        mImageEdit.setOnClickListener(this);
        mChoosefile.setOnClickListener(this);

        mDate.setOnClickListener(new View.OnClickListener(){
            @Override
            public  void onClick(View view){
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        getActivity(),
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();


            }
        });
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month +1;
                String date = dayOfMonth +"/"+ month +"/"+ year;

                mDate.setText(date);
            }
        };

        //when scrolling button is set to hide
        mScrollView.setOnScrollChangeListener(new ScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > 0 ||scrollY<0 || scrollX >0 && mImageEdit.isShown())
                {
                    mImageEdit.hide();
                }
                else{
                    mImageEdit.show();
                }
            }
        });

        //******TO GET USER TYPE(PARENT OR STAFF)********
        mAuth.getCurrentUser().getUid();
        mDatabase.child("Staff").child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    Staff staff = dataSnapshot.getValue(Staff.class);
                    usertype = staff.getUsertype();
                }else{
                    usertype="parent";
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //FOR PICKING FILE
    private void chooseFile(){
        //Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        //intent.setType("file/*");
        //startActivityForResult(intent,PICKFILE_RESULT_CODE);
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select File"), PICKFILE_RESULT_CODE);

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case PICKFILE_RESULT_CODE:
                if(resultCode==RESULT_OK){
                    String FilePath = data.getData().getPath();
                    mFileUri = data.getData();
                    uploadfile.setText(FilePath);
                }
                break;
        }
    }
    //FOR PICKING IMAGE
    /*private void chooseImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), PICK_IMAGE_REQUEST);
        //uploadImage();
        //mBtnUploadIMG.setVisibility(View.VISIBLE);
    }*/
    /*
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() !=null)
        {
            mImageUri = data.getData();
            Picasso.with(context).load(mImageUri).fit()
                    .centerCrop().into(mImageView);//using picasso to load image
        }
    }*/
    //to get extension for our file
    private String getFileExtension(Uri uri){
        ContentResolver cR = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
    //upload image method
    private void createAnnouncement(){
        //mDatabase.child("Event").child()timestamp()

        //final String announceID = UUID.randomUUID().toString();
        final String announceID = String.valueOf((System.currentTimeMillis()/1000));//creating unique id using timestamp(data+time convert to number)
        if(mFileUri !=null){

            final StorageReference ref = storageReference.child("files").child("post").child("announcement").child(announceID + "." +getFileExtension(mFileUri));


            mUploadTask = ref.putFile(mFileUri)//save image into storage reference
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot task) {

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    // mProgressBar.setProgress(0);
                                }
                            }, 1000);//delay reset of progressbar in sec

                            Toast.makeText(context,"Upload Successful",Toast.LENGTH_LONG).show();

                            //get url from the storage reference and assign to uri
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Uri downloadUrl = uri;

                                    Toast.makeText(getActivity().getBaseContext(), "Upload success! URL - " + downloadUrl.toString() , Toast.LENGTH_SHORT).show();

                                    String postID = announceID;
                                    String postTitle = mAnnounceTitle.getText().toString().trim();
                                    String postDesc = mDescription.getText().toString().trim();
                                    String postDate = mDate.getText().toString().trim();
                                    String postType = "Announcement";//for testing
                                    String postFileUrl = downloadUrl.toString();
                                    //String infoType = "in school";//testing
                                    String infoType = String.valueOf(radioStaff.getText());//testing

                                    Announcement post = new Announcement(postID,postTitle,postDesc,postDate,postType,postFileUrl,infoType);
                                    mDatabase.child("Announcement").child(postID).setValue(post);

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
                            //mProgressBar.setProgress((int) progress);
                        }
                    });

        }else if(mImageUri==null){//use previous img url reference, in other word can update using old img without having to choose new img
            Toast.makeText(context, "No File selected", Toast.LENGTH_SHORT).show();

            //StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(currentimg);
            //final StorageReference ref = storageReference.child("images").child("post").child("announcement").child(announceID + ".jpg");

            Toast.makeText(context,"Upload Successful",Toast.LENGTH_LONG).show();

            String postID = announceID;
            String postTitle = mAnnounceTitle.getText().toString().trim();
            String postDesc = mDescription.getText().toString().trim();
            String postDate = mDate.getText().toString().trim();
            String postType = "Announcement";//for testing
            String postImgUrl = "Empty URL";
            String infoType = "in school";//testing

            Announcement post = new Announcement(postID,postTitle,postDesc,postDate,postType,postImgUrl,infoType);
            mDatabase.child("Announcement").child(postID).setValue(post);

        }
    }

    //method to replace current loaded fragment to another fragment
    public void replaceFragment(Fragment someFragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, someFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    @Override
    public void onClick(View v) {
        //Fragment fragment = null;
        if( v == mCreateButton){
            createAnnouncement();
            Intent intent = getActivity().getIntent();
            intent.putExtra("usertype",usertype);
            AnnouncementListFragment fragment = new AnnouncementListFragment();
            replaceFragment(fragment);
        }else if( v == mImageEdit){
            //chooseImage();
            chooseFile();
        }else if( v == mChoosefile){
            //chooseImage();
            chooseFile();
        }
    }
}

