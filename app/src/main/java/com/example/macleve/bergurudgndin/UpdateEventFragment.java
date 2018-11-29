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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.Calendar;

import static android.app.Activity.RESULT_OK;

public class UpdateEventFragment extends Fragment implements View.OnClickListener{

    private static final int PICK_IMAGE_REQUEST = 1 ;
    private EditText eventName, mVenue, mDate, mDescription, mEventID;
    private Button mCreateButton,mBtnUploadIMG;
    private FloatingActionButton mImageEdit;
    private DatabaseReference mDatabase;
    private StorageReference storageReference;
    private FirebaseStorage storage;
    private Uri mImageUri;
    private StorageTask mUploadTask;
    private ImageView mImageView;
    private ScrollView mScrollView;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private Context context;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_update_event, container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getActivity().getApplicationContext();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //link to xml
        mEventID = view.findViewById(R.id.eventIDet);
        mEventID.setVisibility(View.INVISIBLE);
        eventName = view.findViewById(R.id.eventet);
        mVenue = view.findViewById(R.id.venueet);
        mDate = view.findViewById(R.id.dateet);
        mDescription = view.findViewById(R.id.descriptionet);
        mImageEdit = view.findViewById(R.id.imgEdit);
        mImageView = view.findViewById(R.id.imageView);
        mScrollView = view.findViewById(R.id.scrollview);

        mCreateButton = view.findViewById(R.id.createEventBtn);

        //add listener
        mCreateButton.setOnClickListener(this);
        mImageEdit.setOnClickListener(this);

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

        Intent intent = getActivity().getIntent();
        final String eventid = intent.getStringExtra("updateeventid");
        String currentimage = intent.getStringExtra("currentimage");
        Picasso.with(context).load(currentimage).fit()
                .centerInside().into(mImageView);
       // String usertype = intent.getStringExtra("usertype");
        //Log.d("haha",usertype);

        // Read from the database
        mDatabase.child("Event").child(eventid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Event event = dataSnapshot.getValue(Event.class);

                if( event!=null){


                    String eventname = event.getPostTitle();
                    String eventvenue = event.getEventVenue();
                    String eventdate = event.getPostDate();
                    String eventdesc = event.getPostDesc();
                    //String par = String.valueOf(et.getPartNo());

                    mEventID.setText(eventid);
                    eventName.setText(eventname);
                    mVenue.setText(eventvenue);
                    mDate.setText(eventdate);
                    mDescription.setText(eventdesc);

                }//end of if
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(context, "Failed to read value.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void chooseImage(){
        Intent intent = new Intent();
        intent.setType("image/*");//setType, to get content based on type.eg: for document "file/*"
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), PICK_IMAGE_REQUEST);
        //mBtnUploadIMG.setVisibility(View.VISIBLE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() !=null)
        {
            mImageUri = data.getData();//assign img url
            Picasso.with(context).load(mImageUri).fit()
                    .centerCrop().into(mImageView);//using picasso to load image
        }
    }

    //to get extension for our file
    private String getFileExtension(Uri uri){
        ContentResolver cR = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
    //upload image method
    private void updateEventAndUploadImage(){
        //mDatabase.child("Event").child()
        if(mImageUri !=null){

            final String eventID = mEventID.getText().toString().trim();

            final StorageReference ref = storageReference.child("images").child("post").child("events").child(eventID + "." +getFileExtension(mImageUri));
            //mDatabase = FirebaseDatabase.getInstance().getReference("Event").child(eventID);//db ref for User table with the current user

            mUploadTask = ref.putFile(mImageUri)
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

                            final String pEventID = mEventID.getText().toString().trim();
                            final String pEventTitle = eventName.getText().toString().trim();
                            final String eventVenue = mVenue.getText().toString().trim();
                            final String pEventDate = mDate.getText().toString().trim();
                            final String pEventDesc = mDescription.getText().toString().trim();
                            final String pEventType = "Post";//for testing
                            final int eventparnum = 0;

                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Uri downloadUrl = uri;
                                    Event event = new Event(pEventID,pEventTitle,pEventDesc,pEventDate,pEventType,downloadUrl.toString(),eventVenue,eventparnum);

                                    Toast.makeText(getActivity().getBaseContext(), "Upload success! ", Toast.LENGTH_SHORT).show();
                                    //Update event
                                    mDatabase.child("Event").child(pEventID).setValue(event);

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
            Intent intent = getActivity().getIntent();
            final String currentimg = intent.getStringExtra("currentimage");//currentimg

            //Toast.makeText(context, "No file selected", Toast.LENGTH_SHORT).show();
            //final String eventID = mEventID.getText().toString().trim();
            StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(currentimg);
            //final StorageReference ref = storageReference.child("images").child("post").child("events").child(currentimg);

            Toast.makeText(context,"Upload Successful",Toast.LENGTH_LONG).show();

            final String pEventID = mEventID.getText().toString().trim();
            final String pEventTitle = eventName.getText().toString().trim();
            final String eventVenue = mVenue.getText().toString().trim();
            final String pEventDate = mDate.getText().toString().trim();
            final String pEventDesc = mDescription.getText().toString().trim();
            final String pEventType = "Post";//for testing
            final int eventparnum = 0;

            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Uri downloadUrl = uri;
                    Event event = new Event(pEventID,pEventTitle,pEventDesc,pEventDate,pEventType,downloadUrl.toString(),eventVenue,eventparnum);

                    Toast.makeText(getActivity().getBaseContext(), "Upload success! ", Toast.LENGTH_SHORT).show();
                    //Update event
                    mDatabase.child("Event").child(pEventID).setValue(event);
                }
            });
        }
    }


    //method to replace current loaded fragment to another fragment
    public void replaceFragment(Fragment someFragment) {
        getFragmentManager().beginTransaction().remove(UpdateEventFragment.this).commitAllowingStateLoss();//TRY TO FIX
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, someFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
   /* public void eventatt(){
        String eventID = mEventID.getText().toString().trim();
        final String eventname = eventName.getText().toString().trim();
        //creating empty object for specific event
        EventAttendance empty = new EventAttendance(eventID,eventname,0);
        mDatabase.child("EventAttendance").child(eventID).setValue(empty);
    }
         */

    @Override
    public void onClick(View v) {
        Fragment fragment = null;
        if( v == mCreateButton){
            //if(mUploadTask == null && mUploadTask.isInProgress()) {
            updateEventAndUploadImage();
           // eventatt();


            EventDetailFragment removefrag = new EventDetailFragment();
            getFragmentManager().beginTransaction().remove(removefrag).commitAllowingStateLoss();//FIX
            fragment = new EventListFragment();
            replaceFragment(fragment);


        }
        else if( v == mImageEdit){
            chooseImage();
        }
    }
}
