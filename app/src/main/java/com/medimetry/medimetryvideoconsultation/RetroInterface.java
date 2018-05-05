package com.medimetry.medimetryvideoconsultation;


import java.util.List;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;

/**
 * Created by Rakesh on 31-05-2016.
 */
public interface RetroInterface {


    @GET("/wp-json/nativeusers/verify/v512/{mobile}/{countryCode}")
    public void getVerificationCodeV512(@Path("mobile")String contactNumber, @Path("countryCode") String countryCode, Callback<Response> responseCallback);

    @GET("/wp-json/nativeusers/started/v512/{mobile}/{countryCode}/{verify}")
    public void getStartedV512(@Path("mobile")String mobile,@Path("countryCode")String countryCode,@Path("verify")String verify, Callback<Response> responseCallback);



@GET("/wp-json/nativeusers/get-affiliate-user/{userId}/{deviceId}")
    public void getAffiliateUser(@Path("userId")String userId,@Path("deviceId")String deviceId,Callback<Response> responseCallback);






}
