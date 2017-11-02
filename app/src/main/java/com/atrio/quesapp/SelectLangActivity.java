package com.atrio.quesapp;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.atrio.quesapp.model.UserDetail;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class SelectLangActivity extends AppCompatActivity {
    Button btn_eng,btn_malya;
    private FirebaseAuth mAuth;
    FirebaseUser user;
    private DatabaseReference db_ref;
    private FirebaseDatabase db_instance;
    String currentdeviceid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_lang);
        btn_eng=(Button)findViewById(R.id.btn_eng);
         btn_malya=(Button)findViewById(R.id.btn_malya);

        mAuth = FirebaseAuth.getInstance();
        db_instance = FirebaseDatabase.getInstance();
        db_ref = db_instance.getReference("UserDetail");

        user = mAuth.getCurrentUser();

        currentdeviceid = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        user = mAuth.getCurrentUser();
        Log.i("currentdeviceuser2",""+user);

        ConnectivityManager connMgr = (ConnectivityManager) getApplicationContext().getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null) {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
        }else{

            try{
                checkuser();
            }catch (NullPointerException e){

                Log.i("Exception33", e.getMessage());
            }

            btn_eng.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SelectLangActivity.this,SubjectActivity.class);
                    intent.putExtra("sub","English");
                    startActivity(intent);

                    //startActivity(new Intent(SelectLangActivity.this,SubjectActivity.class));
                }
            });
            btn_malya.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(SelectLangActivity.this,SubjectActivity.class);
                    intent.putExtra("sub","Malayalam");
                    startActivity(intent);

                    // startActivity(new Intent(SelectLangActivity.this,MalayalamActivity.class));

                }
            });

        }


    }

    private void checkuser() throws NullPointerException{

        if (user == null){

            throw new NullPointerException("user is null");
        }else{
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            Query query_realtimecheck = rootRef.child("UserDetail").orderByChild("emailId").equalTo(user.getEmail());
            Log.i("Querry66", "" + query_realtimecheck);
            query_realtimecheck.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    UserDetail userDetail = dataSnapshot.getValue(UserDetail.class);
                    String deviceid = userDetail.getDeviceId();
                   /* Toast.makeText(SelectLangActivity.this, "add" + deviceid, Toast.LENGTH_SHORT).show();
                    Toast.makeText(SelectLangActivity.this, "addcurrent" + currentdeviceid, Toast.LENGTH_SHORT).show();*/
                    if (deviceid.equals(currentdeviceid)) {
                        //Toast.makeText(SelectLangActivity.this, "add" + deviceid, Toast.LENGTH_SHORT).show();

                    } else {
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(SelectLangActivity.this, "You are logged in other device", Toast.LENGTH_SHORT).show();
                        //Toast.makeText(SelectLangActivity.this, "addelse" + deviceid, Toast.LENGTH_SHORT).show();


                    }


                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

//                    Toast.makeText(SubjectActivity.this,""+dataSnapshot.getValue(),Toast.LENGTH_SHORT).show();
                   // Toast.makeText(SelectLangActivity.this, "change" + currentdeviceid, Toast.LENGTH_SHORT).show();
                    UserDetail userDetail = dataSnapshot.getValue(UserDetail.class);
                    String deviceid = "data";
                    deviceid =   userDetail.getDeviceId();
                    //Toast.makeText(SelectLangActivity.this, "changecurrent" + deviceid, Toast.LENGTH_SHORT).show();
                    if (!deviceid.equals("data")){

                        if (deviceid.equals(currentdeviceid)) {
                           // Toast.makeText(SelectLangActivity.this, "chabgeif", Toast.LENGTH_SHORT).show();
                        } else {
                            mAuth.signOut();
                            //Toast.makeText(SelectLangActivity.this, "changeelse", Toast.LENGTH_SHORT).show();
                            Toast.makeText(SelectLangActivity.this, "You are logged in other device", Toast.LENGTH_SHORT).show();
                            Intent isend = new Intent(SelectLangActivity.this, LoginActivity.class);
                            isend.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(isend);
                            finish();


                        }
                    }



                    //Toast.makeText(SubjectActivity.this,"change"+dataSnapshot.getChildrenCount(),Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(SelectLangActivity.this, "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        }


    }
}
