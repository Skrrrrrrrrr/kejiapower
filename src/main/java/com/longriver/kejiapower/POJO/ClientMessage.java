package com.longriver.kejiapower.POJO;

import com.longriver.kejiapower.utils.DataFrame;
import com.longriver.kejiapower.utils.DataFrameType;

public class ClientMessage extends Message {

    private String cMessage;

    public ClientMessage() {
    }

    public ClientMessage(String message) {
        if (DataFrame.invalidDataFrameCheck(message))
            throw new RuntimeException("Invalid DataFrame! Cannot construct a Message Class!");
        cMessage = message.replaceAll(" +", "");
        switch (DataFrame.dataFrameTypeClassify(cMessage)) {
            case HeartBeat:
                getHeartBeatMessage(cMessage);
                break;
            case Control:
                getResponseMessage(cMessage);
                break;
            case Report:
                getReportMessage(cMessage);
                break;
//            default:
        }
    }

    public void getHeartBeatMessage(String message) {

//        if (DataFrame.invalidDataFrameCheck(message)) throw new RuntimeException("Not a invalid message");
        if (!DataFrame.dataFrameTypeClassify(message).equals(DataFrameType.HeartBeat))
            throw new RuntimeException("Not a valid client heartbeat message");
        cMessage = message.replaceAll(" +", "");
        try {
            setType(new StringBuilder(cMessage.substring(4, 6)));
            setIdentification(new StringBuilder(cMessage.substring(6, 10)));
            setLength(new StringBuilder(cMessage.substring(10, 12)));
            setClientIp(new StringBuilder(cMessage.substring(12, 20)));
            setVoltage(new StringBuilder(4));
            setCurrent(new StringBuilder(4));
            setControl(new StringBuilder(2));
            setStatus(new StringBuilder(2));
            setModel(new StringBuilder(2));
            setDuration(new StringBuilder(4));
            setServerTime(new StringBuilder(12));
            setPreserveByte(new StringBuilder(4));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getResponseMessage(String message) {
//        if (DataFrame.invalidDataFrameCheck(message)) throw new RuntimeException("Not a Invalid message");
        if (!DataFrame.dataFrameTypeClassify(message).equals(DataFrameType.Control))
            throw new RuntimeException("Not a valid client Response message");
        cMessage = message.replaceAll(" +", "");
        try {
            setType(new StringBuilder(cMessage.substring(4, 6)));
            setIdentification(new StringBuilder(cMessage.substring(6, 10)));
            setLength(new StringBuilder(cMessage.substring(10, 12)));
            setClientIp(new StringBuilder(cMessage.substring(12, 20)));
            setStatus(new StringBuilder(cMessage.substring(20, 22)));
            setVoltage(new StringBuilder(4));
            setCurrent(new StringBuilder(4));
            setControl(new StringBuilder(2));
            setModel(new StringBuilder(2));
            setDuration(new StringBuilder(4));
            setServerTime(new StringBuilder(12));
            setPreserveByte(new StringBuilder(4));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getReportMessage(String message) {
//        if (DataFrame.invalidDataFrameCheck(message)) throw new RuntimeException("Not a Invalid message");
        if (!DataFrame.dataFrameTypeClassify(message).equals(DataFrameType.Report))
            throw new RuntimeException("Not a valid client Report message");
        cMessage = message.replaceAll(" +", "");
        try {
            setType(new StringBuilder(cMessage.substring(4, 6)));
            setIdentification(new StringBuilder(cMessage.substring(6, 10)));
            setLength(new StringBuilder(cMessage.substring(10, 12)));
            setClientIp(new StringBuilder(cMessage.substring(12, 20)));
            setVoltage(new StringBuilder(cMessage.substring(20, 24)));
            setCurrent(new StringBuilder(cMessage.substring(24, 28)));
            setControl(new StringBuilder(cMessage.substring(28, 30)));
            setStatus(new StringBuilder(cMessage.substring(30, 34)));
            setModel(new StringBuilder(cMessage.substring(34, 36)));
            setDuration(new StringBuilder(4));
            setServerTime(new StringBuilder(12));
            setPreserveByte(new StringBuilder(4));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
