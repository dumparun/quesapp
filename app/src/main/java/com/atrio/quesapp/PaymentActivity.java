package com.atrio.quesapp;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.atrio.quesapp.checksumkit.ChecksumGeneration;
import com.atrio.quesapp.checksumkit.ChecksumVerification;
import com.atrio.quesapp.model.UserDetail;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.paytm.pgsdk.PaytmClientCertificate;
import com.paytm.pgsdk.PaytmMerchant;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import java.util.HashMap;
import java.util.Map;

public class PaymentActivity extends AppCompatActivity {
    Button btn_pay;
    TextView tv_amount;
    String amt_pay;
    private FirebaseAuth mAuth;
    FirebaseUser user;
    String currentdeviceid,checksum;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        btn_pay=(Button)findViewById(R.id.btn_pay);
        tv_amount=(TextView)findViewById(R.id.tv_amount);

        amt_pay=tv_amount.getText().toString();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        mAuth = FirebaseAuth.getInstance();

    /*    ChecksumGeneration callgen= new ChecksumGeneration();

        callgen.checksumgenmethod();*/

        ChecksumVerification  callverify=new ChecksumVerification();
        callverify.chcksumverrifymethod();
        Log.i("Checklog455666",""+checksum);

        currentdeviceid = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        user = FirebaseAuth.getInstance().getCurrentUser();


        ConnectivityManager connMgr = (ConnectivityManager) getApplicationContext().getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null) {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
        } else {
            try {
                checkuser();
            } catch (NullPointerException e) {

                Log.i("Exception33", e.getMessage());
            }
            mAuth = FirebaseAuth.getInstance();
            user = mAuth.getCurrentUser();
        }
        btn_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStartTransaction(v,checksum);

            }
        });

    }

    private void checkuser() {

        if (user == null) {

            throw new NullPointerException("user is null");
        } else {
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            Query query_realtimecheck = rootRef.child("UserDetail").orderByChild("emailId").equalTo(user.getEmail());
            Log.i("Querry66", "" + query_realtimecheck);
            query_realtimecheck.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    UserDetail userDetail = dataSnapshot.getValue(UserDetail.class);
                    String deviceid = userDetail.getDeviceId();
/*                    Toast.makeText(ReportActivity.this, "add" + deviceid, Toast.LENGTH_SHORT).show();
                    Toast.makeText(ReportActivity.this, "addcurrent" + currentdeviceid, Toast.LENGTH_SHORT).show();
                   */
                    if (deviceid.equals(currentdeviceid)) {
                        //   Toast.makeText(ReportActivity.this, "add" + deviceid, Toast.LENGTH_SHORT).show();

                    } else {
                        mAuth.signOut();
                        // Toast.makeText(ReportActivity.this, "addelse" + deviceid, Toast.LENGTH_SHORT).show();
                        Toast.makeText(PaymentActivity.this, "You are logged in other device", Toast.LENGTH_SHORT).show();


                    }


                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    //Toast.makeText(SubjectActivity.this,""+dataSnapshot.getValue(),Toast.LENGTH_SHORT).show();
                    //Toast.makeText(ReportActivity.this, "change" + currentdeviceid, Toast.LENGTH_SHORT).show();
                    UserDetail userDetail = dataSnapshot.getValue(UserDetail.class);
                    String deviceid = "data";
                    deviceid = userDetail.getDeviceId();
                    //Toast.makeText(ReportActivity.this, "changecurrent" + deviceid, Toast.LENGTH_SHORT).show();
                    if (!deviceid.equals("data")) {

                        if (deviceid.equals(currentdeviceid)) {
                            //      Toast.makeText(ReportActivity.this, "chabgeif", Toast.LENGTH_SHORT).show();
                        } else {
                            mAuth.signOut();
                            Toast.makeText(PaymentActivity.this, "You are logged in other device", Toast.LENGTH_SHORT).show();
                            Intent isend = new Intent(PaymentActivity.this, SplashActivity.class);
                            isend.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(isend);
                            finish();


                        }
                    } else {

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
                    Toast.makeText(PaymentActivity.this, "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        }




    }
    @Override
    protected void onStart(){
        super.onStart();
        //initOrderId();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    public void onStartTransaction(View view, String checksum) {
        Log.i("erroroccur40434","start");
        Log.i("Checklog455666",""+checksum);

        PaytmPGService Service = PaytmPGService.getStagingService();


        //Kindly create complete Map and checksum on your server side and then put it here in paramMap.

        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("MID" , "ATRIOD13361002889196");
        paramMap.put("ORDER_ID" , "TestMerchant000111007");
        paramMap.put("CUST_ID" , "QuessApp001");
        paramMap.put("INDUSTRY_TYPE_ID" , "Retail");
        paramMap.put("CHANNEL_ID" , "WAP");
        paramMap.put("TXN_AMOUNT" , "1.00");
        paramMap.put("WEBSITE" , "APP_STAGING");
        paramMap.put("CALLBACK_URL" , "https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp");
        paramMap.put("CHECKSUMHASH" , checksum);

    /*    paramMap.put("ORDER_ID", "TestOrder00011101");
        paramMap.put("MID","ATRIOD13361002889196");
        paramMap.put("CUST_ID","arpitapatel4462@gmail.com");
        paramMap.put("CHANNEL_ID", "WAP");
        paramMap.put("INDUSTRY_TYPE_ID", "Retail");
        paramMap.put("WEBSITE", "APP_STAGING");
        paramMap.put("TXN_AMOUNT", amt_pay);*/
  /*      paramMap.put("THEME", "");
        paramMap.put("EMAIL","");
        paramMap.put("MOBILE_NO","");*/

      PaytmOrder Order = new PaytmOrder(paramMap);

        PaytmMerchant Merchant = new PaytmMerchant(
                "https://pguat.paytm.com/paytmchecksum/paytmCheckSumGenerator.jsp",
                "https://pguat.paytm.com/paytmchecksum/paytmCheckSumVerify.jsp");


//        PaytmClientCertificate Certificate  = new PaytmClientCertificate(String inPassword ,String inFileName);

        Service.initialize(Order,null);

        Service.startPaymentTransaction(this, true, true,
                new PaytmPaymentTransactionCallback() {

                    @Override
                    public void someUIErrorOccurred(String inErrorMessage) {
                        Log.i("erroroccur404",""+inErrorMessage);
                        // Some UI Error Occurred in Payment Gateway Activity.
                        // // This may be due to initialization of views in
                        // Payment Gateway Activity or may be due to //
                        // initialization of webview. // Error Message details
                        // the error occurred.
                    }

                    @Override
                    public void onTransactionResponse(Bundle inResponse) {
                        Log.d("LOG", "Payment Transaction : " + inResponse);
                        Toast.makeText(getApplicationContext(), "Payment Transaction response "+inResponse.toString(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void networkNotAvailable() {
                        Log.i("erroroccur4044","nointernet");

                        // If network is not
                        // available, then this
                        // method gets called.
                    }

                    @Override
                    public void clientAuthenticationFailed(String inErrorMessage) {
                        Log.i("erroroccur40456",""+inErrorMessage);

                        // This method gets called if client authentication
                        // failed. // Failure may be due to following reasons //
                        // 1. Server error or downtime. // 2. Server unable to
                        // generate checksum or checksum response is not in
                        // proper format. // 3. Server failed to authenticate
                        // that client. That is value of payt_STATUS is 2. //
                        // Error Message describes the reason for failure.
                    }

                    @Override
                    public void onErrorLoadingWebPage(int iniErrorCode,
                                                      String inErrorMessage, String inFailingUrl) {
                        Log.i("erroroccur40486",""+inErrorMessage);
                        Log.i("erroroccur40487",""+iniErrorCode);

                        Log.i("erroroccur40488",""+inFailingUrl);

                    }

                    // had to be added: NOTE
                    @Override
                    public void onBackPressedCancelTransaction() {
                        // TODO Auto-generated method stub
                    }

                    @Override
                    public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
                        Log.i("erroroccur4045555",""+inErrorMessage);

                        Log.d("LOG", "Payment Transaction Failed " + inErrorMessage);
                        Toast.makeText(getBaseContext(), "Payment Transaction Failed ", Toast.LENGTH_LONG).show();
                    }

                });
    }
}
