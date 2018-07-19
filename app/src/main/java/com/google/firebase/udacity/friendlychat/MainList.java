package com.google.firebase.udacity.friendlychat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainList extends AppCompatActivity {

    private FirebaseDatabase mfirdata;
    private DatabaseReference mdataReferance;
    private ChildEventListener childEventListener;
    private FirebaseAuth mfirebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private List<UserPro> userPro1 = new ArrayList<>();
    private ListView listView;
    private UserAdapter userAdapter;
    //private FirebaseStorage mFirebaseStorage;
    //private StorageReference photoReferance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list);
        listView =(ListView) findViewById(R.id.listView);
        mfirdata = FirebaseDatabase.getInstance();
        mfirebaseAuth = FirebaseAuth.getInstance();
        mdataReferance = mfirdata.getReference().child("users");
        List<UserPro> pros = new ArrayList<>();
        userAdapter = new UserAdapter(MainList.this,R.layout.user_item,pros);
        listView.setAdapter(userAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserDetails.chatWith = userPro1.get(position).getUserName();
                if(UserDetails.username.equals(UserDetails.chatWith))
                {
                    Toast.makeText(MainList.this,"فى حد عاقل يكلم نفسه !",Toast.LENGTH_SHORT).show();
                }
                else {
                    startActivity(new Intent(MainList.this, MainActivity.class));
                }
            }
        });
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user!=null)
                {
                    onSignedInInitial(user.getDisplayName());
                }
                else
                {
                    onSignedOutCleanUp();
                    startActivityForResult(
                            AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setIsSmartLockEnabled(false)
                                .setAvailableProviders(Arrays.asList(
                                        new AuthUI.IdpConfig.EmailBuilder().build(),
                                        new AuthUI.IdpConfig.GoogleBuilder().build()
                                )).build()
                    ,1);
                }

            }
        };
    };
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1) {
            if (resultCode == RESULT_OK) {
                boolean flag = false;
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                String userMaill = firebaseUser.getEmail();
                for (int i=0 ; i<userPro1.size();i++)
                {
                    if(userPro1.get(i).getUserMail().equals(userMaill)) {
                        flag = true;
                        break;
                    }
                }
                if(flag == false)
                {
                    UserPro userProw = new UserPro(firebaseUser.getDisplayName(),firebaseUser.getEmail());
                    mdataReferance.push().setValue(userProw);
                }
            }
            else if (resultCode == RESULT_CANCELED)
                finish();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        return true;
    }
    private void onSignedInInitial(String username)
    {
        UserDetails.username = username;
        attachDatbaseReadListener();
    }
    private void onSignedOutCleanUp()
    {
        userAdapter.clear();
        detachdatabaseReadListener();
    }
    private void attachDatbaseReadListener()
    {
        if(childEventListener==null)
        {
            childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    UserPro userPro = dataSnapshot.getValue(UserPro.class);
                    userPro1.add(userPro);
                    userAdapter.add(userPro);
                  //  withoutCurrentUser(userPro.getUserMail());
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            mdataReferance.addChildEventListener(childEventListener);
        }
    }
//    private void withoutCurrentUser(String currentUserMail)
//    {
//        for(int i=0;i<userPro1.size();i++)
//        {
//            if(!userPro1.get(i).getUserMail().equals(currentUserMail))
//            {
//                userPro2.add(userPro1.get(i));
//            }
//        }
//    }
    private void detachdatabaseReadListener()
    {
        if(childEventListener!=null) {
            mdataReferance.removeEventListener(childEventListener);
            childEventListener = null;
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.sign_out_menu:
                AuthUI.getInstance().signOut(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mfirebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (authStateListener != null) {
            mfirebaseAuth.removeAuthStateListener(authStateListener);
        }
        detachdatabaseReadListener();
        userAdapter.clear();
    }
}
