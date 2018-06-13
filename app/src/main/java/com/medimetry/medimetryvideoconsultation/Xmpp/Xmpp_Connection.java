package com.medimetry.medimetryvideoconsultation.Xmpp;//package com.medimetry.appmedi.Xmpp;


import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.util.Log;

import com.medimetry.medimetryvideoconsultation.SharedPrefrenceClass;
import com.medimetry.medimetryvideoconsultation.VideoCalling.CallBroadcastReciever;
import com.medimetry.medimetryvideoconsultation.VideoCalling.CallingActivity;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.iqregister.packet.Registration;
import org.jivesoftware.smackx.ping.android.ServerPingWithAlarmManager;
import org.jivesoftware.smackx.receipts.DeliveryReceipt;
import org.jivesoftware.smackx.receipts.DeliveryReceiptRequest;
import org.json.JSONObject;
import org.webrtc.IceCandidate;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Pardeep on 12/16/2016.
 */

public class Xmpp_Connection
{

    public static XMPPTCPConnection mConnection;
    SharedPrefrenceClass sharedPreferences;
//String service="ip-172-31-29-57.us-west-2.compute.internal";

//    String domain="35.166.123.52";
    String domain="35.163.223.87";
    String service="ip-172-31-37-244.us-west-2.compute.internal";

    int port=5222;
    Context context;
    String calleeName;

    //DbOp dbOp;
    public Xmpp_Connection(Context cont)
    {
        context=cont;
        sharedPreferences=new SharedPrefrenceClass(cont);
//        dbOp=new DbOp(cont);

    }

    public void createConnection() {


        new AsyncTaskClass().execute();


    }

    public void registerUser(XMPPTCPConnection xmpptcpConnection,String userName)
    {
        if (xmpptcpConnection != null)
        {
            if (xmpptcpConnection.isConnected())
            {

                    Log.e("---",""+mConnection.isConnected());
                    AccountManager accountManager = AccountManager.getInstance(mConnection);
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("username", userName);
                    map.put("name", userName);
                    map.put("password", userName);
                    map.put("email", "abc@gmail.com");
                    map.put("creationDate", ""+System.currentTimeMillis() / 1000L);
                    try {
                        Log.e("Support",""+accountManager.supportsAccountCreation());
                        accountManager.createAccount(userName, userName, map);
                    }catch (Exception e)
                    {
                        Log.e("Registeration Exception",""+e);
                    }
                }



            }
            else
            {
                createConnection();
            }
        }



    public static void loginUser(XMPPTCPConnection xmpptcpConnection,String userName)
    {
        try {
            if(xmpptcpConnection.isConnected()) {
                mConnection.login(userName, userName);
            }
            else
            {
                Log.e("Connection Broken","Connection Broken");
            }
        }
        catch (Exception e)
        {
            Log.e("Login Exception",""+e);
        }
    }


    public class AsyncTaskClass extends AsyncTask<Void,Void,Void>
    {

        @Override
        protected Void doInBackground(Void... params) {
            XMPPTCPConnectionConfiguration connectionConfiguration = XMPPTCPConnectionConfiguration.builder()
                    .setServiceName(service)
                    .setHost(domain)
                    .setPort(port)
                    .setConnectTimeout(1000000)
                    .setCompressionEnabled(false)
                    .setDebuggerEnabled(true).setSendPresence(true)
                    .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                    .build();

            try {
                SASLAuthentication.blacklistSASLMechanism("SCRAM-SHA-1");
                SASLAuthentication.blacklistSASLMechanism("DIGEST-MD5");
                SASLAuthentication.unBlacklistSASLMechanism("PLAIN");
                mConnection = new XMPPTCPConnection(connectionConfiguration);
                mConnection.connect();

                mConnection.addConnectionListener(new ConnectionListener() {
                    @Override
                    public void connected(XMPPConnection connection) {
                        Log.e("Connected",""+connection.getHost());
                    }

                    @Override
                    public void authenticated(XMPPConnection connection, boolean resumed) {
                        Log.e("Authenticated",""+connection.getHost());

                    }

                    @Override
                    public void connectionClosed() {
                        Log.e("Connection Closed","Connection Closed");

                    }

                    @Override
                    public void connectionClosedOnError(Exception e) {
                        Log.e("Connection Close Error",""+e);

                       // createConnection();
                    }

                    @Override
                    public void reconnectionSuccessful() {
                        Log.e("Reconnection Success","Success");
                    }

                    @Override
                    public void reconnectingIn(int seconds) {

                    }

                    @Override
                    public void reconnectionFailed(Exception e) {
                        Log.e("Reconnection Failed",""+e);
                    }
                });



                ReconnectionManager.getInstanceFor(mConnection).enableAutomaticReconnection();
                ServerPingWithAlarmManager.getInstanceFor(mConnection).isEnabled();

                ProviderManager.addExtensionProvider(DeliveryReceipt.ELEMENT, DeliveryReceipt.NAMESPACE, new DeliveryReceipt.Provider());
                ProviderManager.addExtensionProvider(DeliveryReceiptRequest.ELEMENT, new DeliveryReceiptRequest().getNamespace(), new DeliveryReceiptRequest.Provider());

                ProviderManager.addExtensionProvider(DeliveryReceipt.ELEMENT,DeliveryReceipt.NAMESPACE,new DeliveryReceipt.Provider());
                ProviderManager.addExtensionProvider(DeliveryReceiptRequest.ELEMENT,new DeliveryReceiptRequest().getNamespace(), new DeliveryReceiptRequest.Provider());
//                DeliveryReceiptManager.getInstanceFor(mConnection).addReceiptReceivedListener(new ReceiptReceivedListener() {
//                    @Override
//                    public void onReceiptReceived(String fromJid, String toJid, String receiptId, Stanza receipt) {
//                    }
//                });



                mConnection.addAsyncStanzaListener(new StanzaListener() {
                    @Override
                    public void processPacket(Stanza packet) throws SmackException.NotConnectedException {


                       try
                       {
                           Registration registration=(Registration)packet;
                       }
                       catch (Exception e)
                       {
                       }


                        try {
                            Message message = (Message) packet;
                            String getMessageType = "" + message.getType();
                            if (getMessageType.equals("headline") && message.getBody().equals("busy")) {
                                if (CallingActivity.getInstance() != null) {
                                    CallingActivity.getInstance().finishActivity();
                                    try
                                    {
                                        ToneGenerator toneGenerator= new   ToneGenerator(AudioManager.STREAM_DTMF,ToneGenerator.MAX_VOLUME);
                                        toneGenerator.startTone(ToneGenerator.TONE_CDMA_CALLDROP_LITE, 1000);
                                    }
                                    catch (Exception e)
                                    {

                                    }

                                    Log.e("FInisgin","ADfdsf");
                                    Log.e("fdssddf",""+message.getBody());
                                }
                            }
                            else if(getMessageType.equals("groupchat"))
                            {
                                calleeName=message.getBody();
                                if(CallingActivity.getInstance()!=null)
                                {
                                    CallingActivity.getInstance().calleeName.setText(""+calleeName);
                                }
                            }
                            else if(getMessageType.equals("headline") && message.getBody().equalsIgnoreCase("docHold"))
                            {
                             CallingActivity.getInstance().callOnHold();
                            }
                            else if(getMessageType.equals("headline") && message.getBody().equalsIgnoreCase("docUnhold"))
                            {
                                CallingActivity.getInstance().holdBack();
                            }
                            else if(getMessageType.equals("normal"))
                            {


                                String getMessageBody=message.getBody().replace("&quot;","\"");
                                JSONObject jsonObject=new JSONObject(getMessageBody);
                                String getsdpMid=jsonObject.getString("sdpMid");
                                int sdpMlin=jsonObject.getInt("sdpMlin");
                                String getSdp=jsonObject.getString("sdp");

                                IceCandidate iceCandidate=new IceCandidate(getsdpMid,sdpMlin,getSdp);
                                CallingActivity.getInstance().setRemoteIceCandidates(iceCandidate);

                            }


                            else if(getMessageType.equals("headline")) {

                                if(!CallBroadcastReciever.isCallViaPhone) {
                                    Intent intent = new Intent(context, CallingActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.putExtra("receviedSdpContent", "" + message.getBody());
                                    intent.putExtra("userName", "" + calleeName);
                                    intent.putExtra("childKey", "" + message.getFrom());
                                    context.startActivity(intent);
                                }
                            }




                        }
                        catch (Exception e)
                        {
                        }





                    }
                },null);





                try {
                //    registerUser(mConnection, dbOp.getSessionId());
                   registerUser(mConnection, ""+sharedPreferences.getLogedInId());
                }catch (Exception e)
                {
                }
                try
                {
                    loginUser(mConnection,""+sharedPreferences.getLogedInId());
                }
                catch (Exception e)
                {

                }


            } catch (Exception e) {
            }

            return null;
        }
    }


    public static void sendMessage(String toUser,Message.Type type, String message)
    {
        toUser = toUser.indexOf("@") > 0 ? toUser : toUser+"@"+"ip-172-31-37-244.us-west-2.compute.internal";

        try
        {
            Message me=new Message();
            me.setTo(toUser+"@"+"ip-172-31-37-244.us-west-2.compute.internal");
            me.setSubject("postId");
            me.setType(type);
            me.setBody(message);
            mConnection.sendStanza(me);
            String deliveryReceiptId = DeliveryReceiptRequest.addTo(me);
//            DeliveryReceiptManager.getInstanceFor(mConnection).addReceiptReceivedListener(new ReceiptReceivedListener() {
//                @Override
//                public void onReceiptReceived(String fromJid, String toJid, String deliveryReceiptId, Stanza stanza) {
//                }
//            });

            Log.e("===",""+deliveryReceiptId);


        } catch (Exception e)
        {
        }
    }







    }







