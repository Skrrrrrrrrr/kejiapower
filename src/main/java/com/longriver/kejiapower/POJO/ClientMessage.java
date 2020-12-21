package com.longriver.kejiapower.POJO;

import com.longriver.kejiapower.utils.DataFrame;
import com.longriver.kejiapower.utils.DataFrameType;

public class ClientMessage extends Message {


//    public ClientMessage() {
//    }

    public void getClientMessage(String message) {
        String cMessage;
        cMessage = message.replaceAll(" +", "");

        if (DataFrame.invalidDataFrameCheck(cMessage))
            throw new RuntimeException("Invalid DataFrame! Cannot construct a Message Class!");
        switch (DataFrame.dataFrameTypeClassify(cMessage)) {
            case HeartBeat:
                getHeartBeatMessage(cMessage);//如果调用，会出现重复赋值的问题
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
//        String cMessage;
//        cMessage = message.replaceAll(" +", "");
//        if (DataFrame.invalidDataFrameCheck(message)) throw new RuntimeException("Not a invalid message");
        if (message.length() != 22 || !DataFrame.dataFrameTypeClassify(message).equals(DataFrameType.HeartBeat))
            throw new RuntimeException("Not a valid client heartbeat message");
        try {
            setHead(new StringBuilder("FFFF"));
            setType(new StringBuilder(message.substring(4, 6)));
            setIdentification(new StringBuilder(message.substring(6, 10)));
//            setIdentification(new StringBuilder(String.format("%04X",Integer.parseInt(cMessage.substring(6, 10),16)+1)));
            setLength(new StringBuilder(message.substring(10, 12)));
            setClientIp(new StringBuilder(message.substring(12, 20)));
            setVoltage(new StringBuilder(4));
            setCurrent(new StringBuilder(4));
            setControl(new StringBuilder(2));
            setStatus(new StringBuilder(2));
            setDuration(new StringBuilder(4));
            setModel(new StringBuilder(2));
            setServerTime(new StringBuilder(12));
            setPreserveByte(new StringBuilder(4));
            setTail(new StringBuilder("DD"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getResponseMessage(String message) {
//        String cMessage;
//        cMessage = message.replaceAll(" +", "");

//        if (DataFrame.invalidDataFrameCheck(message)) throw new RuntimeException("Not a Invalid message");
        if (message.length() != 24 || !DataFrame.dataFrameTypeClassify(message).equals(DataFrameType.Control))
            throw new RuntimeException("Not a valid client Response message");
        try {
            setHead(new StringBuilder("FFFF"));
            setType(new StringBuilder(message.substring(4, 6)));
            setIdentification(new StringBuilder(message.substring(6, 10)));
            setLength(new StringBuilder(message.substring(10, 12)));
            setClientIp(new StringBuilder(message.substring(12, 20)));
            setStatus(new StringBuilder(message.substring(20, 22)));
            setVoltage(new StringBuilder(4));
            setCurrent(new StringBuilder(4));
            setControl(new StringBuilder(2));
            setDuration(new StringBuilder(4));
            setModel(new StringBuilder(2));
            setServerTime(new StringBuilder(12));
            setPreserveByte(new StringBuilder(4));
            setTail(new StringBuilder("DD"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getReportMessage(String message) {
//        String cMessage;
//        cMessage = message.replaceAll(" +", "");

//        if (DataFrame.invalidDataFrameCheck(message)) throw new RuntimeException("Not a Invalid message");
        if (message.length() != 38 || !DataFrame.dataFrameTypeClassify(message).equals(DataFrameType.Report))
            throw new RuntimeException("Not a valid client Report message");
        try {
            setHead(new StringBuilder("FFFF"));
            setType(new StringBuilder(message.substring(4, 6)));
            setIdentification(new StringBuilder(message.substring(6, 10)));
            setLength(new StringBuilder(message.substring(10, 12)));
            setClientIp(new StringBuilder(message.substring(12, 20)));
            setVoltage(new StringBuilder(message.substring(20, 24)));
            setCurrent(new StringBuilder(message.substring(24, 28)));
            setControl(new StringBuilder(new StringBuilder(2)));
            setStatus(new StringBuilder(message.substring(28, 30)));
            setDuration(new StringBuilder(message.substring(30, 34)));
            setModel(new StringBuilder(message.substring(34, 36)));
            setServerTime(new StringBuilder(12));
            setPreserveByte(new StringBuilder(4));
            setTail(new StringBuilder("DD"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
