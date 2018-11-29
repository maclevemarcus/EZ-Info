package com.example.macleve.bergurudgndin;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MainMenu extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private DrawerLayout drawer;
    private FirebaseAuth mAuth;
    private LinearLayout mProfileLayout, mEventLayout, mAnnoucementLayout, mFavoriteLayout, mInfoLayout;
    private DatabaseReference mDatabase;
    private ImageView userImg;
    private TextView userEmail,username;
    //private FirebaseStorage storage;
    //private StorageReference storageRef;
    private static String usertype;//static string to hold usertype
    private static String LoginUserType;//to avoid redirect to main menu when doing something else
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmenu);

        //storage = FirebaseStorage.getInstance();
        //storageRef = storage.getReference();
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        final NavigationView navigationView = findViewById(R.id.nav_view);

        //userImg = Header.findViewById(R.id.userImg);
        //userEmail = Header.findViewById(R.id.userEmail);
        //username= Header.findViewById(R.id.userName);
        navigationView.setNavigationItemSelectedListener(this);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer,toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

//******1)TO GET USER TYPE(PARENT OR STAFF)********
        mDatabase.child("Staff").child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    Staff staff = dataSnapshot.getValue(Staff.class);
                    usertype = staff.getUsertype();
                    LoginUserType= staff.getUsertype();
                }else{
                    usertype = "parents ";
                    LoginUserType= usertype;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //intent from splash screen(after login)
        Intent intent = getIntent();
        String loginUser = intent.getStringExtra("loginUser");
        String viewid = intent.getStringExtra("vieweventid");//*****EVENT ADAPTER: ONCLICK CV*******
        String delete = intent.getStringExtra("deleteeventid");//*****DELETE EVENT*******
        String deleteAnnounce = intent.getStringExtra("deleteAnnId");//*****DELETE ANNOUCEMENT*******
        String userid = intent.getStringExtra("uid");//*****UPDATE USER(PARENTS)*******
        String viewAnnounce = intent.getStringExtra("viewAnnId"); //*****ANNOUNCEMENT ADAPTER: ONCLICK CV*******
        if(savedInstanceState == null) {
            if(loginUser!=null){
                if (loginUser.equalsIgnoreCase("staff")) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new MainActivity()).commit();
                    navigationView.setCheckedItem(R.id.nav_main);
                } else {

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new ParentsMainFragment()).commit();
                    navigationView.setCheckedItem(R.id.nav_main);
                }
            }else if(viewid!=null){
                //*****EVENT ADAPTER: ONCLICK CV*******
                    String imgurl = intent.getStringExtra("imgurl");

                    intent.putExtra("vieweventid",viewid);
                    intent.putExtra("imgurl",imgurl);
                    intent.putExtra("usertype",usertype);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new EventDetailFragment()).commit();
                    navigationView.setCheckedItem(R.id.nav_event);

            }else if(delete!=null){
                //*****DELETE EVENT*******
                    mDatabase.child("Event").child(delete).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });
                    mDatabase.child("EventAttendance").child(delete).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                        }
                    });
                    String imgurl = intent.getStringExtra("deleteimgurl");
                    Log.d("IMAGE", imgurl);
                    //set url of image to storageref
                    StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imgurl);
                    // Delete the file
                    storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // File deleted successfully
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Uh-oh, an error occurred!
                        }
                    });

                    Toast.makeText(this, "Event has been deleted succesfully " , Toast.LENGTH_SHORT).show();
                    intent.putExtra("usertype",usertype);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new EventListFragment()).commit();

                    navigationView.setCheckedItem(R.id.nav_event);

            }else if(userid!=null){
                //*****UPDATE USER(PARENTS)*******
                String user = intent.getStringExtra("usertype");
                String name = intent.getStringExtra("name");
                String email = intent.getStringExtra("email");
                String address = intent.getStringExtra("address");
                String contact = intent.getStringExtra("contact");

                    intent.putExtra("uid",userid);
                    intent.putExtra("usertype",user);
                    intent.putExtra("name",name);
                    intent.putExtra("email",email);
                    intent.putExtra("address",address);
                    intent.putExtra("contact",contact);


                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new UpdateUserFragment()).commit();

                    navigationView.setCheckedItem(R.id.nav_logout);

            }else if(viewAnnounce!=null){

                intent.putExtra("viewAnnId",viewAnnounce);
                intent.putExtra("usertype",usertype);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new AnnouncementDetailFragment()).commit();
                navigationView.setCheckedItem(R.id.nav_announcement);
            }else if(deleteAnnounce!=null){
                //*****DELETE ANNOUCEMENT*******
                mDatabase.child("Announcement").child(deleteAnnounce).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });

                Toast.makeText(this, "Announcement has been deleted succesfully " , Toast.LENGTH_SHORT).show();
                intent.putExtra("usertype",usertype);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new AnnouncementListFragment()).commit();

                navigationView.setCheckedItem(R.id.nav_event);
            }
            else{//find other way later(this is for when user have slow internet)
                Toast.makeText(getApplicationContext(), "Slow internet connection, please wait" , Toast.LENGTH_SHORT).show();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (usertype.equalsIgnoreCase("staff")) {
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                    new MainActivity()).commit();
                            navigationView.setCheckedItem(R.id.nav_main);
                        } else {

                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                    new ParentsMainFragment()).commit();
                            navigationView.setCheckedItem(R.id.nav_main);
                        }

                    }
                }, 2300);
            }
        }//end of if instance





        //+++++++++++++++++++++++++++++++++
/*
        if(savedInstanceState == null) {
            //Intent intent = getIntent();
            //intent.putExtra("usertype",usertype);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new MainActivity()).commit();
            navigationView.setCheckedItem(R.id.nav_main);
        }

*/
//to redirect user based on user type
/*            if(savedInstanceState == null){//to prevent profile fragment being reload when runtime change occur like rotating the device
                if(usertype==null) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (usertype.equalsIgnoreCase("staff")) {
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                        new MainActivity()).commit();
                                navigationView.setCheckedItem(R.id.nav_main);
                            } else {

                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                        new ParentsMainFragment()).commit();
                                navigationView.setCheckedItem(R.id.nav_main);
                            }

                        }
                    }, 2100);//delay reset of progressbar in sec

                }else{

                    if (usertype.equalsIgnoreCase("staff")) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                new MainActivity()).commit();
                        navigationView.setCheckedItem(R.id.nav_main);
                    } else {

                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                new ParentsMainFragment()).commit();
                        navigationView.setCheckedItem(R.id.nav_main);
                    }
                }
            }//end of if instance ==null
*/
        //*****DELETE EVENT*******
/*        String delete = intent.getStringExtra("deleteeventid");
        if(delete!=null){
            mDatabase.child("Event").child(delete).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                }
            });
            mDatabase.child("EventAttendance").child(delete).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                }
            });
            String imgurl = intent.getStringExtra("deleteimgurl");
            Log.d("IMAGE", imgurl);
            //set url of image to storageref
            StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imgurl);
            // Delete the file
            storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // File deleted successfully
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Uh-oh, an error occurred!
                }
            });

            Toast.makeText(this, "Event has been deleted succesfully " , Toast.LENGTH_SHORT).show();
            intent.putExtra("usertype",usertype);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new EventListFragment()).commit();

            navigationView.setCheckedItem(R.id.nav_event);
        }/*

        //*****UPDATE EVENT*******
  /*      String update = intent.getStringExtra("updateeventid");
        String currentimg = intent.getStringExtra("currentimage");

        if(update!=null){
            //Toast.makeText(this, "Event has been deleted succesfully " , Toast.LENGTH_SHORT).show();
            intent.putExtra("updateeventid",update);
            intent.putExtra("currentimg",currentimg);

            intent.putExtra("usertype",usertype);//to send usertype using intent
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new UpdateEventFragment()).commit();

            navigationView.setCheckedItem(R.id.nav_event);
        }*/
        //*****UPDATE USER(PARENTS)*******
 /*       String userid = intent.getStringExtra("uid");
        String user = intent.getStringExtra("usertype");
        String name = intent.getStringExtra("name");
        String email = intent.getStringExtra("email");
        String address = intent.getStringExtra("address");
        String contact = intent.getStringExtra("contact");

        if(userid!=null){

            intent.putExtra("uid",userid);
            intent.putExtra("usertype",user);
            intent.putExtra("name",name);
            intent.putExtra("email",email);
            intent.putExtra("address",address);
            intent.putExtra("contact",contact);

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new UpdateUserFragment()).commit();

            navigationView.setCheckedItem(R.id.nav_announcement);
        }/*

    /*    //*****EVENT ADAPTER: ONCLICK CV*******
        String viewid = intent.getStringExtra("vieweventid");
//        Log.d("VIEW", viewid);
        if(viewid!=null){
            intent.putExtra("vieweventid",viewid);
            intent.putExtra("usertype",usertype);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new EventDetailFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_event);
        }*/
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent = getIntent();

        switch (item.getItemId()){
            case R.id.nav_profile:
                /*.addToBackStack(null) will stack new called fragment on top of current fragment.
                   if the code is not written, when pressed back, the apps close because there is no older fragment*/
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProfileFragment()).commit();
                break;
            case R.id.nav_main:
                //REMOVE ALL FRAGMENTS
             /*   for (Fragment fragment:getSupportFragmentManager().getFragments()) {
                    getFragmentManager().beginTransaction().remove(fragment).commit();
                }*/
                if(usertype.equalsIgnoreCase("staff")) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new MainActivity()).commit();
                }else {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ParentsMainFragment()).commit();

                }
                break;
            case R.id.nav_event:

                if(usertype.equalsIgnoreCase("staff")) {
                    intent.putExtra("usertype", usertype);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new EventListFragment()).commit();

                    //send data using bundle() from activity to fragment || fragment to fragment
                            /* Bundle bundle = new Bundle();
                            bundle.putString("usertype", usertype);
                            EventListFragment fragobj = new EventListFragment();
                            fragobj.setArguments(bundle);

                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                    fragobj).commit();*/
                }else {
                    intent.putExtra("usertype", "parents");
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new EventListFragment()).commit();
                }
                break;

            case R.id.nav_announcement:

                if(usertype.equalsIgnoreCase("staff")) {
                    intent.putExtra("usertype", usertype);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new AnnouncementListFragment()).commit();
                }else {
                    intent.putExtra("usertype", "parents");
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new AnnouncementListFragment()).commit();
                }
                break;

            case R.id.nav_logout:
                mAuth.signOut();
                startActivity(new Intent(MainMenu.this, LoginActivity.class));
                finish();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed(){
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }

    //method to replace current loaded fragment to another fragment
    public void replaceFragment(Fragment someFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, someFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
