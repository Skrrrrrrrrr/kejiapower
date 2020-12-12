package com.longriver.kejiapower.POJO;

import com.longriver.kejiapower.utils.DataFrame;
import com.longriver.kejiapower.utils.DataFrameType;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ServerMessage extends Message {

//    public ServerMessage() {
//    }

    public void getServerMessage(Message message) {
//        if (DataFrame.invalidDataFrameCheck(message))
//            throw new RuntimeException("Invalid DataFrame!Cannot construct a Message Class!");
        switch (DataFrame.dataFrameTypeClassify(message.toString())) {
            case HeartBeat:
                generateHeartBeatMessage(message);
                break;
            case Control:
//                generateControlMessage(message);
                generateControlMessage();//
                break;
            case Report:
                generateRespondMessage(message);
                break;
//            default:
        }
    }

    public void generateHeartBeatMessage(Message message) {
//        if (!DataFrame.dataFrameTypeClassify(message.toString().replaceAll(" +", "")).equals(DataFrameType.HeartBeat))
//            throw new RuntimeException("setServerHeartBeatMessage Error, not a HeartBeat frame from Client!");
//        message = message.replaceAll(" +", "");
        setHead(new StringBuilder("FFFF"));
        setType(message.getType());
        setIdentification(new StringBuilder(String.format("%04X", Integer.parseInt(message.getIdentification().toString(), 16) + 1)));
        setLength(new StringBuilder("0A"));
        setClientIp(message.getClientIp());
        setServerTime(new StringBuilder(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())));
        //其他置空
        setVoltage(new StringBuilder(4));
        setCurrent(new StringBuilder(4));
        setControl(new StringBuilder(2));
        setStatus(new StringBuilder(2));
        setModel(new StringBuilder(2));
        setDuration(new StringBuilder(4));
        setPreserveByte(new StringBuilder(4));
        setTail(new StringBuilder("DD"));

    }

    public void generateControlMessage() {
        setHead(new StringBuilder("FFFF"));
        setType(new StringBuilder("0C"));
        setIdentification(new StringBuilder(Integer.parseInt(getIdentification().toString(), 16) + 1));
        setLength(new StringBuilder("0C"));

        //单独设置，然后需要生成ClientMessage时不用再次设置，此处可以加一个校验，判断是否非法
        if (Integer.parseInt(getVoltage().toString(), 16) > 1000 ||
                Integer.parseInt(getCurrent().toString(), 16) > 100) {
            throw new RuntimeException("Voltage or Current out of range!");
        }
//        setVoltage(new StringBuilder("00"));
//        setCurrent(new StringBuilder("00"));
//        setControl(new StringBuilder("00"));
//        setClientIp(new StringBuilder("127.0.0.1"));

        setPreserveByte(new StringBuilder("FFFF"));
//其他置空
        setClientIp(new StringBuilder(8));
        setStatus(new StringBuilder(2));
        setModel(new StringBuilder(2));
        setDuration(new StringBuilder(4));
        setServerTime(new StringBuilder(12));
        setTail(new StringBuilder("DD"));
    }

    @Deprecated
    public void generateControlMessage(String message) {
        setHead(new StringBuilder("FFFF"));
        setType(new StringBuilder("0C"));
        setIdentification(new StringBuilder(Integer.parseInt(getIdentification().toString(), 16) + 1));
        setLength(new StringBuilder("0C"));
        setVoltage(new StringBuilder("00"));
        setCurrent(new StringBuilder("00"));
        setControl(new StringBuilder("00"));
        setPreserveByte(new StringBuilder("0000"));
        setClientIp(new StringBuilder("127.0.0.1"));
        //其他置空
        setClientIp(new StringBuilder(8));
        setStatus(new StringBuilder(2));
        setModel(new StringBuilder(2));
        setDuration(new StringBuilder(4));
        setServerTime(new StringBuilder(12));
        setTail(new StringBuilder("DD"));
    }

    @Deprecated
    public void generateControlMessage(Message message) {
        setHead(new StringBuilder("FFFF"));
        setType(new StringBuilder("0C"));
        setIdentification(new StringBuilder(Integer.parseInt(getIdentification().toString(), 16)));
        setLength(new StringBuilder("0C"));
        setVoltage(new StringBuilder("00"));
        setCurrent(new StringBuilder("00"));
        setControl(new StringBuilder("00"));
        setPreserveByte(new StringBuilder("0000"));
        setClientIp(new StringBuilder("127.0.0.1"));
        //其他置空
        setClientIp(new StringBuilder(8));
        setStatus(new StringBuilder(2));
        setModel(new StringBuilder(2));
        setDuration(new StringBuilder(4));
        setServerTime(new StringBuilder(12));
        setTail(new StringBuilder("DD"));
    }


    public void generateRespondMessage(Message message) {
//        if (!DataFrame.dataFrameTypeClassify(message.toString().replaceAll(" +", "")).equals(DataFrameType.Report))
//            throw new RuntimeException("generateRespondMessage Error, not a Report frame from Client!");
        setHead(new StringBuilder("FFFF"));
        setType(new StringBuilder("0B"));
        setIdentification(new StringBuilder(Integer.parseInt(message.getIdentification().toString(), 16) + 1));
        setLength(new StringBuilder("05"));
        setClientIp(message.getClientIp());
        //其他置空
        setVoltage(new StringBuilder("00"));
        setCurrent(new StringBuilder("00"));
        setControl(new StringBuilder("00"));
        setStatus(new StringBuilder(2));
        setModel(new StringBuilder(2));
        setDuration(new StringBuilder(4));
        setServerTime(new StringBuilder(12));
        setPreserveByte(new StringBuilder(4));
        setTail(new StringBuilder("DD"));
    }



}
