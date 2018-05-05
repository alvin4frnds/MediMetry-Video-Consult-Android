package com.medimetry.medimetryvideoconsultation;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.squareup.okhttp.OkHttpClient;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * Created by Pardeep on 7/12/2016.
 */
public class RetrofitInitClass
{
    static RestAdapter restAdapter;
    static RetroInterface retroInterface;
    static OkHttpClient okClient;


    public static RetroInterface RetroInit(Context cont)
    {



        try {
            okClient=new OkHttpClient();
            okClient.setReadTimeout(90, TimeUnit.SECONDS);
            okClient.setConnectTimeout(90, TimeUnit.SECONDS);
            okClient.setWriteTimeout(90, TimeUnit.SECONDS);
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream instream = cont.getResources().openRawResource(R.raw.crtf);
            java.security.cert.Certificate ca;
            try {
                ca = cf.generateCertificate(instream);
            } finally {
                // caInput.close();
            }

            KeyStore kStore = KeyStore.getInstance(KeyStore.getDefaultType());
            kStore.load(null, null);
            kStore.setCertificateEntry("ca", ca);

            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            ;
            tmf.init(kStore);

            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, tmf.getTrustManagers(), null);

            okClient.setSslSocketFactory(context.getSocketFactory());
//            okClient.setHostnameVerifier(new HostnameVerifier() {
//                @Override
//                public boolean verify(String hostname, SSLSession session) {
//                    return true;
//                }
//            });
        }catch (Exception E)
        {
            Log.e("---",""+E);
        }

//

//

        restAdapter=new RestAdapter.Builder().setClient(new OkClient(okClient)).setEndpoint(cont.getResources().getString(R.string.base_url)).build();
        //restAdapter=new RestAdapter.Builder().setEndpoint(cont.getResources().getString(R.string.base_url)).setRequestInterceptor(new MyRetrofitInterceptor(sharedPrefrenceClass.getHeader().trim())).build();

        restAdapter.setLogLevel(RestAdapter.LogLevel.FULL);
        retroInterface=restAdapter.create(RetroInterface.class);
        return retroInterface;
    }















}
