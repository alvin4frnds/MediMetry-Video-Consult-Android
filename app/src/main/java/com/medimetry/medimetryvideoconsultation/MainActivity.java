package com.medimetry.medimetryvideoconsultation;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.VideoView;
import com.medimetry.medimetryvideoconsultation.VideoCalling.Service_IncomingCall;
import org.json.JSONArray;
import org.json.JSONObject;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<Data_UserName> data_userNames;
    List<Data_UserName> data_filteredData;

    Toolbar toolbar_activity;
    RecyclerView recyclerview;
    SharedPrefrenceClass sharedPrefrenceClass;
    EditText edittextSearch;

    private VideoView vidView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_main);
        toolbar_activity=(Toolbar)findViewById(R.id.toolbar_activity);
        setSupportActionBar(toolbar_activity);
        getSupportActionBar().setTitle("Available Connections");
        recyclerview=(RecyclerView)findViewById(R.id.recyclerview);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        recyclerview.setLayoutManager(linearLayoutManager);
        access_permission();
       data_filteredData=new ArrayList<Data_UserName>();





        data_userNames=new ArrayList<Data_UserName>();
        sharedPrefrenceClass=new SharedPrefrenceClass(this);
        Log.e("---",""+sharedPrefrenceClass.getUserId());
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Please Wait..");
        progressDialog.show();
        RetrofitInitClass.RetroInit(this).getAffiliateUser("70921", "3qws", new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
             String getString=new String(((TypedByteArray)response.getBody()).getBytes());
                progressDialog.dismiss();
                try
                {

                    JSONObject jsonObject=new JSONObject(getString);
                    int isAffiliate=jsonObject.getInt("isAffiliate");
                    if(isAffiliate==1)
                    {

                        JSONArray jsonArray=jsonObject.getJSONArray("users");
                                for(int i=0;i<jsonArray.length();i++) {
                                    JSONObject objectUsers = jsonArray.getJSONObject(i);
                                    String id = objectUsers.getString("ID");
                                    String display_name = objectUsers.getString("display_name");
                                    Log.e("Display Name",""+display_name);
                                    String email = objectUsers.getString("user_email");
                                    String login = objectUsers.getString("user_login");
                                    data_userNames.add(new Data_UserName(id, display_name, login));
                                }
                    }

                    recyclerview.setAdapter(new Baseadapter_List(MainActivity.this,data_userNames));


                }
                catch (Exception e)
                {
                    Log.e("Error",""+e);
                    Toast.makeText(MainActivity.this,"Something went wrong from server",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void failure(RetrofitError retrofitError) {

                Log.e("Error",""+retrofitError);
            }
        });


        sharedPrefrenceClass.setLogedInId("70921");
        startService(new Intent(MainActivity.this, Service_IncomingCall.class));


        // Custom code
//        VideoView vidView = (VideoView)findViewById(R.id.myVideo);
//        Uri vidUri = Uri.parse("http://pc.medimetry.in/video-stream/");
//        vidView.setVideoURI(vidUri);
//        vidView.start();
    }

    public void access_permission() {
        try {

            //if not granted request them to grant
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
//                      ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 2);
//                      ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, 3);
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED

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
