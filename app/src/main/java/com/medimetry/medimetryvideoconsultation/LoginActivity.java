package com.medimetry.medimetryvideoconsultation;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    Button btnVerify;
    Context context = LoginActivity.this;
    EditText edtPh;
    int success = 0;
    String strMsg = "";
    ArrayList<String> strings;
    public static String verifiCode = "";
    public static String strPh = "";
    TextView txtTerms, txtPrivacy;
    SharedPrefrenceClass sharedPrefrenceClass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        access_permission();


        sharedPrefrenceClass = new SharedPrefrenceClass(this);
        if (sharedPrefrenceClass.isLogin()) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            setContentView(R.layout.login_activity);
            //setupActionBar();

            btnVerify = (Button) findViewById(R.id.btnVerify);
            btnVerify.setOnClickListener(this);
            edtPh = (EditText) findViewById(R.id.edtPh);
            txtTerms = (TextView) findViewById(R.id.txtTerms);
            txtPrivacy = (TextView) findViewById(R.id.txtPrivacy);
            txtTerms.setOnClickListener(this);
            txtPrivacy.setOnClickListener(this);
        }


    }

    @Override
    public void onClick(View view) {
        if (edtPh.getText().toString().equals("")) {
            Toast.makeText(LoginActivity.this, "Please Enter Your Contact Number", Toast.LENGTH_LONG).show();
        } else {

            strPh = edtPh.getText().toString();
            final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setMessage("Please Wait");
            progressDialog.show();
            RetrofitInitClass.RetroInit(LoginActivity.this).getVerificationCodeV512("" + edtPh.getText().toString(), "91", new Callback<Response>() {
                @Override
                public void success(Response response, Response response2) {
                    progressDialog.dismiss();
                    try {
                        String getResponse = new String(((TypedByteArray) response.getBody()).getBytes());
                        JSONObject jsonObject = new JSONObject(getResponse);
                        String success = jsonObject.getString("success");
                        if (success.equals("1")) {
                            verifiCode = "" + jsonObject.getString("code");
                            Intent intent = new Intent(LoginActivity.this, VerificationActivity.class);
                            intent.putExtra("otp", "" + jsonObject.getString("code"));
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Server Side Issue", Toast.LENGTH_LONG).show();
                        }


                    } catch (Exception e) {
                        Log.e("Exception", "" + e);
                    }
                }

                @Override
                public void failure(RetrofitError retrofitError) {

                }
            });
        }
    }


    public void access_permission() {
        try {

            //if not granted request them to grant
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
//                      ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 2);
//                      ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, 3);
            if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED

                    )
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS,
                        Manifest.permission.RECEIVE_SMS,
                        Manifest.permission.GET_ACCOUNTS,
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO,


                }, 0);
        } catch (Exception e) {
            Toast.makeText(this, "Something Went Wrong, Please Manually  Check The Permissions", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            //grant all permissions
            case 0:


                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults.length > 0 && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        //    Toast.makeText(LoginActivity.this, "Permission Set", Toast.LENGTH_LONG).show();

                        Log.e("Permission", "" + permissions[i]);

                    }
                    break;


                }
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        }
    }
}