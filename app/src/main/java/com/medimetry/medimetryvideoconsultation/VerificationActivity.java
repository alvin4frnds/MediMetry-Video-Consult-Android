package com.medimetry.medimetryvideoconsultation;
//
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsMessage;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Scanner;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
/**
 * Created by waheguru on 17/07/15.
 */
public class VerificationActivity extends ActionBarActivity implements View.OnClickListener{
    Button btnContinue;
    public static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    SMSReciever1 smsReciever;
    EditText edtVerfifyCode;
    int code;
    String strVerificationCode="",strname="";
    boolean isTimeOut=false,isOTPFinish=false;
    TextView txtResend,txtErrorMsg;
    Context context=VerificationActivity.this;
    String SENDER_ID = "424047797757"; //Debug
    //String SENDER_ID = "604921230377"; //production
    String regid;

 boolean isSmsVerfy=false;
    TextView veri_title;
    String phnNumber="";


    String getCountryCode;
    int loopTime=60000;
    int count=60;
    int countOpos=0;

    Handler handler;
    Runnable runnable;

    SharedPrefrenceClass sharedPrefrenceClass;
    TextView veri_phone;
    TextView timerText;
    TextView veri_enterOtp;
    TextView veri_notrecieved;
    ProgressBar pb;
    Dialog dl;
    boolean isFromRegister;
    String email;
    String phoneNumber;
    String name;
    Boolean aBoolean=false;
    class SMSReciever1 extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
                Bundle bundle = intent.getExtras();           //---get the SMS message passed in---
                SmsMessage[] msgs = null;
                String msg_from;
                if (bundle != null){
                    //---retrieve the SMS message received---
                    try{
                        Object[] pdus = (Object[]) bundle.get("pdus");
                        msgs = new SmsMessage[pdus.length];
                        for(int i=0; i<msgs.length; i++){
                            msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                            msg_from = msgs[i].getOriginatingAddress();
                            String msgBody = msgs[i].getMessageBody();
                            if(isSmsVerfy==true && msgBody.contains("Verification Code"))
                            {
                                updateUI(msgBody);
                                handler.removeCallbacks(runnable);
                                pb.setVisibility(View.GONE);
                                timerText.setVisibility(View.GONE);
                            }
                            if(isTimeOut==false && msgBody.contains("Verification Code")){
                                isTimeOut=true;

                                handler.removeCallbacks(runnable);

                                pb.setVisibility(View.GONE);
                                timerText.setVisibility(View.GONE);
                                updateUI(msgBody);
                            }else{
                                if (pd != null && pd.isShowing())
                                    pd.dismiss();
                            }
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void updateUI(String msgBody){
        if (pd != null && pd.isShowing())
            pd.dismiss();

        try
        {
            if(dl!=null && dl.isShowing())
            {
                dl.dismiss();
            }
        }
        catch (Exception e)
        {

        }

        Scanner in = new Scanner(msgBody).useDelimiter("[^0-9]+");
        code = in.nextInt();
        LoginActivity.verifiCode= String.valueOf(code);
        edtVerfifyCode.setText(String.valueOf(code));

        if(LoginActivity.verifiCode.equals(String.valueOf(code))){
           // new GetSuccessCode().execute();
            getSuccessCode();
        }else{

        }
    }
    //Dialog dl;
    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        smsReciever=new SMSReciever1();
        setContentView(R.layout.code_verification);
        setupActionBar();
        edtVerfifyCode=(EditText)findViewById(R.id.edtVerifyCode);
        txtResend=(TextView)findViewById(R.id.txtResend);
        txtErrorMsg=(TextView)findViewById(R.id.txtErrormsg);
        btnContinue=(Button)findViewById(R.id.btnContinue);
        btnContinue.setVisibility(View.GONE);
        txtResend.setVisibility(View.VISIBLE);
        sharedPrefrenceClass=new SharedPrefrenceClass(this);
        txtResend.setOnClickListener(this);
        veri_phone=(TextView)findViewById(R.id.veri_phone);
        isFromRegister=getIntent().getBooleanExtra("isFromRegsiter",false);
        if(isFromRegister)
        {
            email=getIntent().getStringExtra("email");
            name=getIntent().getStringExtra("name");

        }



        if(isFromRegister)
        {
            phnNumber=getIntent().getStringExtra("phone");
            getCountryCode=getIntent().getStringExtra("countryCode");
        }
        else
        {
            phnNumber=LoginActivity.strPh;
            getCountryCode=getIntent().getStringExtra("country");
        }


        veri_title=(TextView)findViewById(R.id.veri_title);
        veri_title.setText("Code has been sent to you on your \n Mobile number 91"+" "+phnNumber);

        veri_phone=(TextView)findViewById(R.id.veri_phone);
        veri_enterOtp=(TextView)findViewById(R.id.veri_enterOtp);
        veri_notrecieved=(TextView)findViewById(R.id.veri_notrecieved);
        timerText=(TextView)findViewById(R.id.timerText);
        veri_notrecieved.setVisibility(View.GONE);
      pb=(ProgressBar)findViewById(R.id.pb);


        edtVerfifyCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    int count=s.length();
                    if(count>2)
                    {
                        btnContinue.setVisibility(View.VISIBLE);

                    }
                    else
                    {
                        btnContinue.setVisibility(View.GONE);
                    }
                }
                catch (Exception e)
                {

                }
            }
        });



//        dl= NotificationReciever.getProgressIndicator(context);
//        dl.show();
//        pd = new ProgressDialog(VerificationActivity.this);
//        pd.setMessage("Reading SMS...");
//        pd.setCancelable(false);
//        pd.show();
        btnContinue.setOnClickListener(this);
        IntentFilter filter = new IntentFilter(SMS_RECEIVED);
        filter.setPriority(999999999);
        registerReceiver(smsReciever, filter);

recurFunc(false);
txtResend.setVisibility(View.GONE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isTimeOut) {
                    btnContinue.setVisibility(View.VISIBLE);
                    txtResend.setVisibility(View.VISIBLE);
                    veri_notrecieved.setVisibility(View.VISIBLE);
                    isTimeOut = true;
//                    if (pd != null && pd.isShowing())
//                        pd.dismiss();
                    handler.removeCallbacks(runnable);
                    timerText.setVisibility(View.GONE);
                    pb.setVisibility(View.GONE);
                 //   Toast.makeText(context, "Please fill verification code you got otherwise resend code", Toast.LENGTH_LONG).show();


                }
            }
        }, loopTime);
    }

    private void setupActionBar()
    {
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        ActionBar bar = getSupportActionBar();
//        bar.setTitle("Login");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode== KeyEvent.KEYCODE_BACK)
        {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(smsReciever);
    }
    int success=0,userId=0,isExistingUser=0;





    public void getSuccessCode()
    {
        final ProgressDialog progressDialog=new ProgressDialog(VerificationActivity.this);
        progressDialog.setMessage("Please Wait..");
        progressDialog.show();
        strVerificationCode=edtVerfifyCode.getText().toString();
        if(isFromRegister)
        {
            phnNumber=getIntent().getStringExtra("phone");
            getCountryCode=getIntent().getStringExtra("countryCode");
        }
        else
        {
            phnNumber=LoginActivity.strPh;
            getCountryCode=getIntent().getStringExtra("country");
        }
        RetrofitInitClass.RetroInit(VerificationActivity.this).getStartedV512("" + phnNumber, "91","" + strVerificationCode, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                String getResponseString = new String(((TypedByteArray) response.getBody()).getBytes());

                progressDialog.dismiss();
                try {
                    JSONObject jobj = new JSONObject(getResponseString);
                    if (jobj.getInt("success") == 1) {
                        success = 1;
                        userId = jobj.getInt("userId");
                        isExistingUser = jobj.getInt("signup");
                        strname = jobj.getString("name");
                        sharedPrefrenceClass.setUserId(""+userId);
                        sharedPrefrenceClass.setLogin(true);
                        Intent intent=new Intent(VerificationActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();




                    }


                }catch (Exception e)
                {

                }
            }

            @Override
            public void failure(RetrofitError retrofitError) {
            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnContinue:
                if (edtVerfifyCode.getText().toString().equals("")) {
                    Toast.makeText(context, "Please fill verification code you got otherwise resend code", Toast.LENGTH_LONG).show();
                } else {
                    if (LoginActivity.verifiCode.equals(edtVerfifyCode.getText().toString())) {
                        getSuccessCode();
                    } else {
                        Toast.makeText(context, "Verification code you entered is wrong,please check it again.", Toast.LENGTH_LONG).show();
                    }
                }

                break;
            case R.id.txtResend:
                if(!isOTPFinish) {
                        btnContinue.setVisibility(View.GONE);
                        txtResend.setVisibility(View.GONE);
                        isSmsVerfy=true;
                        //new GetVerifyCode().execute();
                        unregisterReceiver(smsReciever);
                        IntentFilter filter = new IntentFilter(SMS_RECEIVED);
                        filter.setPriority(999999999);
                        registerReceiver(smsReciever, filter);

                        getVerificationCode();
                    } else {
                        Toast.makeText(context, "Check internet connection", Toast.LENGTH_LONG).show();
                    }
                }
        }
















    public void getVerificationCode()
    {
        RetrofitInitClass.RetroInit(this).getVerificationCodeV512("" +phnNumber,"91", new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {


                String responseString = new String(((TypedByteArray) response.getBody()).getBytes());
               try {
                   JSONObject jobj=new JSONObject(responseString);
                if(jobj.getInt("success")==1){
                    success=1;
                    dl.dismiss();
                    LoginActivity.verifiCode=jobj.getString("code");
                    recurFunc(true);
                    veri_notrecieved.setVisibility(View.GONE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

//                                if (dl != null && dl.isShowing())
//                                    dl.dismiss();
                            btnContinue.setVisibility(View.VISIBLE);
                            handler.removeCallbacks(runnable);
                            txtResend.setVisibility(View.VISIBLE);
                            veri_notrecieved.setVisibility(View.VISIBLE);
                            timerText.setVisibility(View.GONE);
                            pb.setVisibility(View.GONE);
                            //    Toast.makeText(context, "Please fill verification code you got otherwise resend code", Toast.LENGTH_LONG).show();
                            }

                    }, loopTime);

            }else{
                if(dl!=null || dl.isShowing())
                    dl.dismiss();
                isOTPFinish=true;
                txtResend.setTextColor(Color.DKGRAY);
                txtErrorMsg.setText("Max OTP attempts reached,please contact medimetry");
                    Toast.makeText(VerificationActivity.this,"Max Otp Recevied", Toast.LENGTH_LONG).show();
            }


               }catch (Exception e)
               {
               }
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                dl.dismiss();
            }
        });
    }







    public void recurFunc(boolean aBoolean)
    {
        if(aBoolean)
        {
            count=60;
            countOpos=0;
        }
        timerText.setVisibility(View.VISIBLE);
        pb.setVisibility(View.VISIBLE);
         handler = new Handler();
         runnable = new Runnable() {
            public void run() {

count--;
                countOpos++;
                if(count<1)
                {
                    handler.removeCallbacks(runnable);
                }

                DecimalFormat decimalFormat=new DecimalFormat("00");

                timerText.setText("Waiting For Sms 0:"+decimalFormat.format(count));

                pb.setProgress(countOpos);
                handler.postDelayed(this, 1000);
            }
        };
        runnable.run();


    }










}

