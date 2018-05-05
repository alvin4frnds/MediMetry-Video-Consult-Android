package com.medimetry.medimetryvideoconsultation.VideoCalling;

/**
 * Created by Pardeep on 11/25/2016.
 */

public class VideoCall
{
    String calleeId;
    String callerId;
    String iceSdp;
    String iceFlowBy;

    public VideoCall() {
    }

    String calleeName;

    public String getIceSdp() {
        return iceSdp;
    }

    public void setIceSdp(String iceSdp) {
        this.iceSdp = iceSdp;
    }

    public String getIceFlowBy() {
        return iceFlowBy;
    }

    public void setIceFlowBy(String iceFlowBy) {
        this.iceFlowBy = iceFlowBy;
    }

    public VideoCall(String calleeId, String callerId, String calleeName,
                     String callerName, String callStatus, String sdp,
                     String sdpMin, String sdpLine, String iceSdp, String iceFlowBy) {
        this.calleeId = calleeId;
        this.callerId = callerId;
        this.calleeName = calleeName;
        this.callerName = callerName;
        this.callStatus = callStatus;
        this.sdp = sdp;
        this.sdpMin = sdpMin;
        this.sdpLine = sdpLine;
        this.iceSdp=iceSdp;
        this.iceFlowBy=iceFlowBy;


    }

    public String getCalleeId() {

        return calleeId;
    }

    public void setCalleeId(String calleeId) {
        this.calleeId = calleeId;
    }

    public String getCallerId() {
        return callerId;
    }

    public void setCallerId(String callerId) {
        this.callerId = callerId;
    }

    public String getCalleeName() {
        return calleeName;
    }

    public void setCalleeName(String calleeName) {
        this.calleeName = calleeName;
    }

    public String getCallerName() {
        return callerName;
    }

    public void setCallerName(String callerName) {
        this.callerName = callerName;
    }

    public String getCallStatus() {
        return callStatus;
    }

    public void setCallStatus(String callStatus) {
        this.callStatus = callStatus;
    }

    public String getSdp() {
        return sdp;
    }

    public void setSdp(String sdp) {
        this.sdp = sdp;
    }

    public String getSdpMin() {
        return sdpMin;
    }

    public void setSdpMin(String sdpMin) {
        this.sdpMin = sdpMin;
    }

    public String getSdpLine() {
        return sdpLine;
    }

    public void setSdpLine(String sdpLine) {
        this.sdpLine = sdpLine;
    }

    String callerName;
    String callStatus;
    String sdp;
    String sdpMin;
    String sdpLine;


}
