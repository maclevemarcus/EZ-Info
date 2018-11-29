package com.example.macleve.bergurudgndin;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.support.v4.app.Fragment;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.support.constraint.Constraints.TAG;

public class ParentsMainFragment extends Fragment implements View.OnClickListener {


    private LinearLayout mProfileLayout, mEventLayout, mAnnoucementLayout, mFavoriteLayout, mInfoLayout;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private Context context;
    private static String usertype;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View v =inflater.inflate(R.layout.parents_main, container,false);

        //create instances to the database
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        context = getActivity().getApplicationContext();


        //******1)TO GET USER TYPE(PARENT OR STAFF)********
        mDatabase.child("Staff").child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    Staff staff = dataSnapshot.getValue(Staff.class);
                    usertype = staff.getUsertype();

                }else{
                    usertype = "parent";
                }
                Log.d("USER",usertype);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //link to xml
        mProfileLayout = view.findViewById(R.id.profileLayout);
        mEventLayout = view.findViewById(R.id.eventLayout);
        mAnnoucementLayout = view.findViewById(R.id.announceLayout);
        mFavoriteLayout = view.findViewById(R.id.favoriteLayout);
        mInfoLayout = view.findViewById(R.id.infoLayout);


        //add listener
        mProfileLayout.setOnClickListener(this);
        mEventLayout.setOnClickListener(this);
        mAnnoucementLayout.setOnClickListener(this);
        mFavoriteLayout.setOnClickListener(this);
        mInfoLayout.setOnClickListener(this);

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
        Fragment fragment = null;
        Intent intent = getActivity().getIntent();
        if( v == mInfoLayout){
            startActivity(new Intent(context, UserListActivity.class));

        }else if( v == mProfileLayout){
            //how to destroy fragment
            //getFragmentManager().beginTransaction().remove(MainActivity.this).commitAllowingStateLoss();

            fragment = new ProfileFragment();
            replaceFragment(fragment);
            //setcheck for profile on nav menu when profile cardview is clicked
            //index 1 on menu
            NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
            navigationView.getMenu().getItem(1).setChecked(true);//setchecked at index 1 on menu
        } else if( v == mEventLayout) {
            //getFragmentManager().beginTransaction().remove(MainActivity.this).commitAllowingStateLoss();
            //finish();

            //cant start because ur calling a fragment
            intent.putExtra("usertype",usertype);
            fragment = new EventListFragment();
            replaceFragment(fragment);
            NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
            navigationView.getMenu().getItem(2).setChecked(true);//setchecked at index 2 on menu
        } else if( v == mAnnoucementLayout){
            //finish();
            //cant start because ur calling a fragment
            intent.putExtra("usertype",usertype);
            fragment = new AnnouncementListFragment();
            replaceFragment(fragment);
            NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
            navigationView.getMenu().getItem(3).setChecked(true);//setchecked at index 2 on menu
        }else if( v == mFavoriteLayout){
            startActivity(new Intent(context, MainMenu.class));
        }
    }

}
