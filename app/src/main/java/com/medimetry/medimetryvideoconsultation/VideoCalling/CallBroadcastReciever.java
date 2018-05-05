package com.medimetry.medimetryvideoconsultation.VideoCalling;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

//import com.medimetry.appmedi.Xmpp.Xmpp_Connection;

//import org.jivesoftware.smack.packet.Message;

/**
 * Created by Pardeep onnn 12/11/2016.
 */

public class CallBroadcastReciever extends BroadcastReceiver
{


    public  static boolean isCallViaPhone=false;

    @Override
    public void onReceive(Context context, Intent intent) {



        if (intent != null)
        {
            try {
                String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
                Log.e("----", "" + state);


                if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                    isCallViaPhone=true;




                } else if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                   isCallViaPhone=true;
//                    if (CallingActivity.getInstance() != null)
//                    {
//                        CallingActivity.getInstance().videoTrack.setEnabled(false);
//                        CallingActivity.getInstance().audioTrack.setEnabled(false);
//                        CallingActivity.getInstance().remoteMediaStream.audioTracks.get(0).setEnabled(false);
//                        CallingActivity.getInstance().remoteMediaStream.videoTracks.get(0).setEnabled(false);
//                        Xmpp_Connection.sendMessage(CallingActivity.getInstance().getChildKey, Message.Type.headline,"onHold");
//                    }


                } else if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {


                    isCallViaPhone=false;
//                    Log.e("CallActivty",""+CallingActivity.getInstance());
//                    if (CallingActivity.getInstance() != null)
//                    {
//                        CallingActivity.getInstance().videoTrack.setEnabled(true);
//                        CallingActivity.getInstance().audioTrack.setEnabled(true);
//
//                        CallingActivity.getInstance().remoteMediaStream.audioTracks.get(0).setEnabled(true);
//                        CallingActivity.getInstance().remoteMediaStream.videoTracks.get(0).setEnabled(true);
//                        Xmpp_Connection.sendMessage(CallingActivity.getInstance().getChildKey, Message.Type.headline,"holdBack");
//
//                        if (CallingActivity.getInstance().mNM != null)
//                        {
//                         CallingActivity.getInstance().mNM.cancelAll();
//                        }
//
//                    }

                }
            }catch (Exception e)

            {
                Log.e("--Exce",""+e);
            }
        }


    }
}
