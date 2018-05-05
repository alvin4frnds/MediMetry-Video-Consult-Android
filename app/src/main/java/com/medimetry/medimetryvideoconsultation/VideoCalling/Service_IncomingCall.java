package com.medimetry.medimetryvideoconsultation.VideoCalling;//package com.medimetry.appmedi.VideoCalling;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.medimetry.medimetryvideoconsultation.Xmpp.Xmpp_Connection;
//import org.jivesoftware.smack.tcp.XMPPTCPConnection;

/**
 * Created by Pardeep on 11/25/2016.
 */

public class Service_IncomingCall extends Service
{


    Xmpp_Connection xmpp_connection;
    //XMPPTCPConnection tcpConnection;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        xmpp_connection=new Xmpp_Connection(this);
        xmpp_connection.createConnection();
        return Service.START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }


    @Override
    public void onDestroy() {

        super.onDestroy();

    }
}
