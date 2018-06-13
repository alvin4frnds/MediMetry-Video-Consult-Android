package com.medimetry.medimetryvideoconsultation.VideoCalling;//package com.medimetry.appmedi.VideoCalling;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.medimetry.medimetryvideoconsultation.R;
import com.medimetry.medimetryvideoconsultation.SharedPrefrenceClass;
import com.medimetry.medimetryvideoconsultation.Xmpp.Xmpp_Connection;

import org.jivesoftware.smack.packet.Message;
import org.webrtc.*;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Pardeep on 11/19/2016.
 */

public class CallingActivity extends AppCompatActivity implements View.OnClickListener {

//

    NotificationManagerCompat notMan;

    // DbOp dbOp;
    MediaStream remoteMediaStream;
    Handler handler;
    Runnable runnable;
    int minute = 0;
    int second = 0;
    PeerConnectionFactory peerConnectionFactory;
    ArrayList<PeerConnection.IceServer> iceServers;
    public boolean isOrigin = true;
    public PeerConnection peerConnection;
    boolean onetime = false;
    //    RestAdapter restAdapter;
//    RetroInterface retroInterface;
    VideoRenderer remoteRenderer;
    String getName;
    Boolean isActuallyStarted = false;
    String MAX_VIDEO_WIDTH_CONSTRAINT = "maxWidth";
    String MIN_VIDEO_WIDTH_CONSTRAINT = "minWidth";
    String MAX_VIDEO_HEIGHT_CONSTRAINT = "maxHeight";
    String MIN_VIDEO_HEIGHT_CONSTRAINT = "minHeight";
    String MAX_VIDEO_FPS_CONSTRAINT = "maxFrameRate";
    String MIN_VIDEO_FPS_CONSTRAINT = "minFrameRate";
    VideoRenderer videoRenderer = null;
    SessionDescription oneMore;
    SessionDescription localSessionDesc;


    SharedPrefrenceClass sharedPrefrenceClass;
    NotificationManagerCompat notificationManager_background;

    PeerConnectionObserver peerConnectionObserverClass;
    PeerSdpObserver sdpObserverClass;
    boolean isIncomingCall = false;

    GLSurfaceView glSurfaceView;
    public TextView calleeName;
    TextView callTimer;
    ImageView imageSpeaker;
    ImageView imageCamOption;
    ImageView imagePickCall;
    ImageView imageCutCall;
    ImageView stopVideo;

    int countSpeaker = 0;
    int countCamOptions = 0;
    int countVideoStop = 0;
    int pickCall = 0;


    boolean isCallCut = false;

    List<IceCandidate> listCandidates;
    String getSdp;

    String userName;
    String userId;
    String getSessionDesc;
    VideoSource videoSource;

    VideoTrack videoTrack;
    AudioTrack audioTrack;
    String getChildKey;
    ImageView speakerup;

    int countLoadSpeaker = 0;
    AudioManager audioManager;
    NotificationManager mNM;
    android.support.v4.app.NotificationCompat.Builder builder;
    LinearLayout callLayout;
    MediaStream localMediaStream;
    TextView drName;
    MediaPlayer mediaPlayer;
    //SharedPrefrenceClass sharedPrefrenceClass;
    public static CallingActivity callingActivity;

    public static CallingActivity getInstance() {
        return callingActivity;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                + WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                +WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                +WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        callingActivity = this;
        setContentView(R.layout.callerscreen);
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        sharedPrefrenceClass = new SharedPrefrenceClass(CallingActivity.this);

        // Log.e("isAudioplaying", "" + audioManager.isMusicActive());
        if (audioManager.isMusicActive()) {
            audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
        }

        speakerup = (ImageView) findViewById(R.id.speakerup);
        speakerup.setOnClickListener(this);
        calleeName = (TextView) findViewById(R.id.calleeName);
        callTimer = (TextView) findViewById(R.id.callTimer);
        imageSpeaker = (ImageView) findViewById(R.id.imageSpeaker);
        imageCamOption = (ImageView) findViewById(R.id.imageCamOption);
        imagePickCall = (ImageView) findViewById(R.id.imagePickCall);
        stopVideo = (ImageView) findViewById(R.id.stopVideo);
        imageCamOption.setOnClickListener(this);
        imageSpeaker.setOnClickListener(this);
        callLayout = (LinearLayout) findViewById(R.id.callLayout);
        callLayout.setVisibility(View.VISIBLE);
        stopVideo.setOnClickListener(this);
        drName = (TextView) findViewById(R.id.drName);
        imagePickCall.setOnClickListener(this);
        imageCutCall = (ImageView) findViewById(R.id.imageCutCall);
        imageCutCall.setOnClickListener(this);
        glSurfaceView = (GLSurfaceView) findViewById(R.id.glSurfaceView);
        boolean b = PeerConnectionFactory.initializeAndroidGlobals(this, true, true, true);

        listCandidates = new ArrayList<IceCandidate>();
        isIncomingCall = getIntent().getBooleanExtra("incomingCall", false);
//        sharedPrefrenceClass=new SharedPrefrenceClass(this);
//
//        dbOp=new DbOp(this);
        getSdp = getIntent().getStringExtra("receviedSdpContent");
        userId = getIntent().getStringExtra("userId");
        getChildKey = getIntent().getStringExtra("childKey");

//        Log.e("GetChildKey", "GetChildKey: " + getChildKey);


        calleeName.setText("" + getIntent().getStringExtra("userName"));
        drName.setText("" + getIntent().getStringExtra("userName"));
        updateCallStatus(getChildKey);


        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();

        }

        try {
            mediaPlayer.setDataSource(getApplicationContext(),
                    Uri.parse("android.resource://com.medimetry.medimetryvideoconsultation/" + R.raw.phone));
            mediaPlayer.prepare();
            mediaPlayer.start();
            // mediaPlayer.setLooping(true);
        } catch (Exception e) {
            Log.e("Exception", "" + e);
        }

        if (!b) {
            Toast.makeText(CallingActivity.this, "Video Chat Not Supported", Toast.LENGTH_LONG).show();
        } else {
            initComponents();


        }


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.speakerup:

                countLoadSpeaker++;
                // Log.e("Load Speaker ", "Pressed");
                if (countLoadSpeaker % 2 == 0) {
                    speakerup.setImageResource(R.mipmap.ic_speaker_up);
                    audioManager.setSpeakerphoneOn(false);
                } else {

                    audioManager.setMode(AudioManager.STREAM_VOICE_CALL);
                    audioManager.setSpeakerphoneOn(true);
                    speakerup.setImageResource(R.mipmap.ic_speakeroff);
                }

                break;

            case R.id.stopVideo:
                countVideoStop++;
                if (countVideoStop % 2 == 0) {
                    stopVideo.setImageResource(R.mipmap.ic_videoend);
                    videoTrack.setEnabled(true);
                } else {

                    stopVideo.setImageResource(R.mipmap.ic_videostart);
                    videoTrack.setEnabled(false);
                }
                break;
            case R.id.imageSpeaker:
                countSpeaker++;
                if (countSpeaker % 2 == 0) {

                    imageSpeaker.setImageResource(R.mipmap.ic_micoff);
                    audioTrack.setEnabled(true);
                } else {
                    imageSpeaker.setImageResource(R.mipmap.ic_micon);
                    audioTrack.setEnabled(false);

                }
                break;
            case R.id.imageCamOption:
                countCamOptions++;
                if (countCamOptions % 2 == 0) {
                    imageCamOption.setImageResource(R.mipmap.ic_backcam);
                    flipCam(false);
                } else {


                    flipCam(true);
//
//                    Log.e("Video Status",""+videoSource.state());
//                    if(videoSource!=null)
//                        videoSource.stop();
//
//
//
//                    peerConnection.removeStream(localMediaStream);
//                    videoSource=peerConnectionFactory.createVideoSource(VideoCapturer.create(VideoCapturerAndroid.getNameOfBackFacingDevice()),new MediaConstraints());
//
//
//
//                    AudioSource audioSource=peerConnectionFactory.createAudioSource(new MediaConstraints());
//                    videoTrack=peerConnectionFactory.createVideoTrack("ARDAMSv1",videoSource);
//                    audioTrack=peerConnectionFactory.createAudioTrack("ARDAMSa1",audioSource);
//
//
//                    localMediaStream.addTrack(videoTrack);
//                    localMediaStream.addTrack(audioTrack);
//
//                    videoSource.restart();
//                    videoTrack.setState(MediaStreamTrack.State.LIVE);
//
//                    localMediaStream=peerConnectionFactory.createLocalMediaStream("LocalMediaStream");
//                    peerConnection.addStream(localMediaStream);


                    imageCamOption.setImageResource(R.mipmap.ic_frontcam);
                }
                break;
            case R.id.imagePickCall:

                callLayout.setVisibility(View.GONE);
                peerConnection.createAnswer(sdpObserverClass, new MediaConstraints());
                imagePickCall.setVisibility(View.GONE);
                recurFunc();
                try {

                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                } catch (Exception e) {

                }


                break;
            case R.id.imageCutCall:
                peerConnection.close();
                finishActivity();


                break;
        }
    }


    public void initComponents() {


        peerConnectionFactory = new PeerConnectionFactory();
        iceServers = new ArrayList<PeerConnection.IceServer>();
//        iceServers.add(new PeerConnection.IceServer("stun:turn.medimetry.com:347);
//        iceServers.add(new PeerConnection.IceServer("turn:turn.medimetry.com","gorst","hero"));


        iceServers.add(new PeerConnection.IceServer(getResources().getString(R.string.stunUrl)));
        iceServers.add(new PeerConnection.IceServer(getResources().getString(R.string.turnUrl), getResources().getString(R.string.turnUsername), getResources().getString(R.string.turnPassword)));


        MediaConstraints mediaConstraints = new MediaConstraints();
        mediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("offerToReceiveAudio", "true"));
        mediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair("offerToReceiverVideo", "true"));


        mediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair(MIN_VIDEO_WIDTH_CONSTRAINT, "800"));
        mediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair(MAX_VIDEO_WIDTH_CONSTRAINT, "1280"));
        mediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair(MIN_VIDEO_HEIGHT_CONSTRAINT, "480"));
        mediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair(MAX_VIDEO_HEIGHT_CONSTRAINT, "720"));
        mediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair(MIN_VIDEO_FPS_CONSTRAINT, "2"));
        mediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair(MAX_VIDEO_FPS_CONSTRAINT, "10"));
        mediaConstraints.optional.add(new MediaConstraints.KeyValuePair("googCpuOveruseDetection", "false"));


        videoSource = peerConnectionFactory.createVideoSource(VideoCapturer.create(CameraEnumerationAndroid.getNameOfFrontFacingDevice()), new MediaConstraints());
        AudioSource audioSource = peerConnectionFactory.createAudioSource(new MediaConstraints());
        videoTrack = peerConnectionFactory.createVideoTrack("ARDAMSv0", videoSource);
        audioTrack = peerConnectionFactory.createAudioTrack("ARDAMSa0", audioSource);
        localMediaStream = peerConnectionFactory.createLocalMediaStream("LocalMediaStream");

        VideoRendererGui.setView(glSurfaceView, new Runnable() {
            @Override
            public void run() {
                // Log.e("This is working","Working");
            }
        });
        videoTrack.setEnabled(true);
        audioTrack.setEnabled(true);


        try {
            remoteRenderer = VideoRendererGui.createGui(0, 0, 100, 100, RendererCommon.ScalingType.SCALE_ASPECT_FIT, true);
            videoRenderer = VideoRendererGui.createGui(70, 70, 30, 30, RendererCommon.ScalingType.SCALE_ASPECT_FIT, true);
            videoTrack.addRenderer(videoRenderer);


        } catch (Exception e) {
            // Log.e("Video Renderer",""+e);
        }

        localMediaStream.addTrack(videoTrack);
        localMediaStream.addTrack(audioTrack);


        sdpObserverClass = new PeerSdpObserver();
        peerConnectionObserverClass = new PeerConnectionObserver();
        peerConnection = peerConnectionFactory.createPeerConnection(iceServers, mediaConstraints, peerConnectionObserverClass);
        peerConnection.addStream(localMediaStream);

        if (peerConnection.getRemoteDescription() == null) {
            peerConnection.setRemoteDescription(sdpObserverClass, new SessionDescription(SessionDescription.Type.OFFER, getSdp));
        }
    }


    public class PeerConnectionObserver implements PeerConnection.Observer {

        @Override
        public void onSignalingChange(PeerConnection.SignalingState signalingState) {
            // Log.e("On Signal Change", "" + signalingState.name());

        }

        @Override
        public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {

            // Log.e("Ice Connection Change", "" + iceConnectionState.name());

            if (iceConnectionState.name().equals("CONNECTED")) {
                //  Log.e("Connected","Connected");
            } else if (iceConnectionState.name().equalsIgnoreCase("DISCONNECTED")) {
                peerConnection.close();

                finish();
            }
        }

        @Override
        public void onIceConnectionReceivingChange(boolean b) {

        }

        @Override
        public void onIceGatheringChange(final PeerConnection.IceGatheringState iceGatheringState) {
            Log.e("Ice Conn Gather Change", "" + iceGatheringState.name());

            if (iceGatheringState.name().equals("COMPLETE")) {
                Log.e("Complete", "Complete");

//                updateSdp(getChildKey,getSessionDesc);

                // Log.e("Final Sdp",""+getSessionDesc);
            }
        }

        @Override
        public void onIceCandidate(IceCandidate iceCandidate) {
            // Log.e("Ice Candidate sdp",""+iceCandidate.sdp);


            Log.e("Sdp Lines", "" + iceCandidate.sdp);
            Log.e("Sdp index", "" + iceCandidate.sdpMLineIndex);
            Log.e("Sdp mid", "" + iceCandidate.sdpMid);
            IceCandidate ice = new IceCandidate(iceCandidate.sdpMid, iceCandidate.sdpMLineIndex, iceCandidate.sdp);


            if (!onetime) {
                getSessionDesc = localSessionDesc.description;
            }
            onetime = true;
            Log.e("Old Sdp", "" + iceCandidate.sdp);
            getSessionDesc = getSessionDesc + "a=" + iceCandidate.sdp + "\n";

            Log.e("Old Sdp", "" + getSessionDesc.toString());
            oneMore = new SessionDescription(localSessionDesc.type, getSessionDesc);


            //peerConnection.addIceCandidate(iceCandidate);


            String jsonFormat = "{\"sdpMid\":" + iceCandidate.sdpMid + "," + "\"sdpMlin\":" + iceCandidate.sdpMLineIndex + "," + "\"sdp\":" + "&quot;" + iceCandidate.sdp + "&quot;" + "}";

            //updateSdpLines(getChildKey,iceCandidate.sdpMid,""+iceCandidate.sdpMLineIndex,ice.sdp);

            Xmpp_Connection.sendMessage("" + getChildKey, Message.Type.headline, jsonFormat);


        }

        @Override
        public void onAddStream(MediaStream mediaStream) {
            //Log.e("Add Media String ", "" + mediaStream);
            remoteMediaStream = mediaStream;
            remoteMediaStream.audioTracks.get(0);//.addRenderer(remoteRenderer);


            // Now video should not gets played
            remoteMediaStream.videoTracks.get(0).addRenderer(remoteRenderer);
        }

        @Override
        public void onRemoveStream(MediaStream mediaStream) {


            Log.e("Remove Stream Called", "Removed Stream Called");

            // Log.e("Remove Strign ", "");
        }

        @Override
        public void onDataChannel(DataChannel dataChannel) {
            Log.e("Data Change", "");
        }

        @Override
        public void onRenegotiationNeeded() {
            Log.e("On Reno", "Renogaiodlfsdlflsdf");
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        connectToServer();
    }


    public void connectToServer() {
        if (Xmpp_Connection.mConnection != null) {
            if (Xmpp_Connection.mConnection.isConnected()) {
                //     Xmpp_Connection.loginUser(Xmpp_Connection.mConnection,""+dbOp.getSessionId());
                Xmpp_Connection.loginUser(Xmpp_Connection.mConnection, "70921");
            } else {
                stopService(new Intent(CallingActivity.this, Service_IncomingCall.class));
                startService(new Intent(CallingActivity.this, Service_IncomingCall.class));
            }
        }


    }


    public class PeerSdpObserver implements SdpObserver {

        @Override
        public void onCreateSuccess(final SessionDescription sessionDescription) {
            //SessionDescription newSessionDesc=new SessionDescription(sessionDescription.type,sessionDescription.description);

            localSessionDesc = sessionDescription;

            //updateSdp(getChildKey,getSessionDesc);

            // updateSdp(getChildKey,localSessionDesc.description);


            Xmpp_Connection.sendMessage("" + getChildKey, Message.Type.error, "" + localSessionDesc.description);


//            Log.e("Session Desction ",""+sessionDescription.type);
//            Log.e("Have LocalDesc",""+peerConnection.getLocalDescription());
//            Log.e("Have RemoteDesc",""+peerConnection.getRemoteDescription());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (peerConnection.getLocalDescription() == null) {
                        peerConnection.setLocalDescription(sdpObserverClass, sessionDescription);
                    }

                }
            });


        }

        @Override
        public void onSetSuccess() {
            Log.e("OnSetSuccess", "SetSuccess");
        }

        @Override
        public void onCreateFailure(String s) {
            Log.e("OnCreateFailure", "" + s);
        }

        @Override
        public void onSetFailure(String s) {
            Log.e("OnSetFailure", "" + s);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();


        Log.e("Finished", "FInded");
    }

    public void updateCallStatus(String childKey) {
        Map<String, Object> mapObjectMap = new HashMap<String, Object>();
        mapObjectMap.put("callStatus", "reached");
        //       FirebaseMisc.getDatabaseReference().child(childKey).updateChildren(mapObjectMap);
    }


    public void removeNode() {
        //  FirebaseMisc.getDatabaseReference().child(getChildKey).removeValue();

        Xmpp_Connection.sendMessage("" + getChildKey, Message.Type.headline, "busy");

    }


    public void updateSdp(String childKey, String sdp) {
        Map<String, Object> objectMap = new HashMap<String, Object>();
        objectMap.put("sdp", sdp);
        objectMap.put("callStatus", "ansSend");
        //  FirebaseMisc.getDatabaseReference().child(childKey).updateChildren(objectMap);
    }


    public void updateSdpLines(String childKey, String sdpMin, String sdpLine, String sdp) {

        Map<String, Object> objectMap = new HashMap<String, Object>();
        objectMap.put("iceFlowBy", "user");
        objectMap.put("sdpMin", sdpMin);
        objectMap.put("sdpLine", sdpLine);
        objectMap.put("iceSdp", sdp);
        objectMap.put("callStatus", "userFlow");
        //  FirebaseMisc.getDatabaseReference().child(childKey).updateChildren(objectMap);

    }


    public void finishActivity() {
        isCallCut = true;
        if (notificationManager_background != null) {
            notificationManager_background.cancelAll();
        }
        try {
            notificationManager_background.cancelAll();

        } catch (Exception e) {

        }

        finish();
        callingActivity = null;
        removeNode();

        //  sharedPrefrenceClass.setActivityLaunched(false);
        try {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        } catch (Exception e) {
            Log.e("---", "" + e);
        }
        peerConnection.dispose();
        if (videoSource != null)
            videoSource.dispose();


        try {
            handler.removeCallbacks(runnable);
        } catch (Exception e) {
            Log.e("---", "" + e);
        }
    }


    //
    public void recurFunc() {
        callTimer.setVisibility(View.VISIBLE);

        handler = new Handler();
        runnable = new Runnable() {
            public void run() {

                second++;
                if (second > 59) {
                    second = 0;
                    minute++;
                }

                DecimalFormat decimalFormat = new DecimalFormat("00");
                Log.e("----", "" + decimalFormat.format(minute) + " : " + decimalFormat.format(second));

                String getMinute = "" + decimalFormat.format(minute);
                String getSeconds = "" + decimalFormat.format(second);

                callTimer.setText("" + getMinute + " : " + getSeconds);
                ;

                //timerText.setText("Waiting For Sms 0:"+decimalFormat.format(count));

                //  pb.setProgress(countOpos);
                handler.postDelayed(this, 1000);
            }
        };
        runnable.run();


    }


    public void setRemoteIceCandidates(IceCandidate remoteIceCandidates) {
        peerConnection.addIceCandidate(remoteIceCandidates);
    }

//
//    public void addCandid(IceCandidate iceCandidate)
//    {
//        listCandidates.add(iceCandidate);
//    }


    public void flipCam(boolean flipBack) {


        if (videoSource != null) {

            videoSource.stop();
            videoSource = null;
        }

        localMediaStream.removeTrack(videoTrack);
        peerConnection.removeStream(localMediaStream);
        if (flipBack) {
            videoSource = peerConnectionFactory.createVideoSource(VideoCapturer.create(CameraEnumerationAndroid.getNameOfBackFacingDevice()), new MediaConstraints());
        } else {
            videoSource = peerConnectionFactory.createVideoSource(VideoCapturer.create(CameraEnumerationAndroid.getNameOfFrontFacingDevice()), new MediaConstraints());

        }
        AudioSource audioSource = peerConnectionFactory.createAudioSource(new MediaConstraints());
        audioTrack = peerConnectionFactory.createAudioTrack("ARDAMSa0", audioSource);
        videoTrack = peerConnectionFactory.createVideoTrack("ARDAMSv0", videoSource);
        localMediaStream = peerConnectionFactory.createLocalMediaStream("LocalMediaStream");
        videoTrack.setEnabled(true);
        audioTrack.setEnabled(true);


        try {
            videoRenderer = VideoRendererGui.createGui(70, 70, 30, 30, RendererCommon.ScalingType.SCALE_ASPECT_FIT, true);
            videoTrack.addRenderer(videoRenderer);


        } catch (Exception e) {
            Log.e("Video Renderer", "" + e);
        }

        localMediaStream.addTrack(videoTrack);
        localMediaStream.addTrack(audioTrack);
        peerConnection.addStream(localMediaStream);

    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.e("OnsTOP WORKING", "ONsTOOPE");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("onPuase", "calaing");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (!isCallCut) {
                    Log.e("isnotCut", "isnotcu");
                    //  makeNotification();
                }
            }
        });
    }


    public void callOnHold() {
        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
        Intent intent = new Intent();

        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Notification summaryNotification = new NotificationCompat.Builder(this)
                .setContentTitle("Call on hold")
                .setContentText("Your call is on hold").setContentIntent(pIntent)
                .setSmallIcon(R.mipmap.ic_launcher).setAutoCancel(true)
                .setSound(uri)
                .build();


        notMan = NotificationManagerCompat.from(this);
        notMan.notify(Integer.parseInt(timeStamp), summaryNotification);

    }

    public void holdBack() {
        if (notMan != null) {
            notMan.cancelAll();
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }
}
