package com.longriver.kejiapower.POJO;

import com.longriver.kejiapower.utils.DataFrame;
import com.longriver.kejiapower.utils.DataFrameType;

public class ClientMessage extends Message {

    public ClientMessage() {
    }

    public ClientMessage(String message) {
        if (DataFrame.invalidDataFrameCheck(message))
            throw new RuntimeException("Invalid DataFrame! Cannot construct a Message Class!");
        switch (DataFrame.dataFrameTypeClassify(message)) {
            case HeartBeat:
                getHeartBeatMessage(message);
                break;
            case Control:
                getResponseMessage(message);
                break;
            case Report:
                getReportMessage(message);
                break;
//            default:
        }
    }


    public void getHeartBeatMessage(String message) {
//        if (DataFrame.invalidDataFrameCheck(message)) throw new RuntimeException("Not a invalid message");
        if (!DataFrame.dataFrameTypeClassify(message).equals(DataFrameType.HeartBeat))
            throw new RuntimeException("Not a valid client heartbeat message");
        try {
            setType(new StringBuilder(message.substring(4, 6)));
            setIdentification(new StringBuilder(message.substring(6, 10)));
            setLength(new StringBuilder(message.substring(10, 12)));
            setClientIp(new StringBuilder(message.substring(12, 20)));
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
        try {
            setType(new StringBuilder(message.substring(4, 6)));
            setIdentification(new StringBuilder(message.substring(6, 10)));
            setLength(new StringBuilder(message.substring(10, 12)));
            setClientIp(new StringBuilder(message.substring(12, 20)));
            setStatus(new StringBuilder(message.substring(20, 22)));
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
        try {
            setType(new StringBuilder(message.substring(4, 6)));
            setIdentification(new StringBuilder(message.substring(6, 10)));
            setLength(new StringBuilder(message.substring(10, 12)));
            setClientIp(new StringBuilder(message.substring(12, 20)));
            setVoltage(new StringBuilder(message.substring(20, 24)));
            setCurrent(new StringBuilder(message.substring(24, 28)));
            setControl(new StringBuilder(message.substring(28, 30)));
            setStatus(new StringBuilder(message.substring(30, 34)));
            setModel(new StringBuilder(message.substring(34, 36)));
            setDuration(new StringBuilder(4));
            setServerTime(new StringBuilder(12));
            setPreserveByte(new StringBuilder(4));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
