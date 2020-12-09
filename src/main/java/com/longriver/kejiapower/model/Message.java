package com.longriver.kejiapower.model;

import com.longriver.kejiapower.utils.DataFrame;
import com.longriver.kejiapower.utils.DataFrameType;
import com.longriver.kejiapower.utils.StringUtils;

import javax.management.relation.RoleUnresolved;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Message {
    private StringBuilder head;
    private StringBuilder type;
    private StringBuilder identification;
    private StringBuilder length;
    private StringBuilder clientIp;
    private StringBuilder voltage;
    private StringBuilder current;
    private StringBuilder control;
    private StringBuilder model;
    private StringBuilder preserveByte;
    private StringBuilder status;
    private StringBuilder duration;
    private StringBuilder serverTime;
    private StringBuilder tail;

    public Message() {
        head = new StringBuilder("FFFF");
        type = new StringBuilder(2);
        identification = new StringBuilder(4);
        length = new StringBuilder(2);
        clientIp = new StringBuilder(8);
        voltage = new StringBuilder(4);
        current = new StringBuilder(4);
        control = new StringBuilder(2);
        model = new StringBuilder(2);
        status = new StringBuilder(2);
        duration = new StringBuilder(4);
        serverTime = new StringBuilder(12);
        preserveByte = new StringBuilder(4);
        tail = new StringBuilder("DD");

        head.ensureCapacity(4);
        type.ensureCapacity(2);
        identification.ensureCapacity(4);
        length.ensureCapacity(2);
        clientIp.ensureCapacity(8);
        voltage.ensureCapacity(4);
        current.ensureCapacity(4);
        control.ensureCapacity(2);
        model.ensureCapacity(2);
        status.ensureCapacity(2);
        duration.ensureCapacity(4);
        serverTime.ensureCapacity(12);
        preserveByte.ensureCapacity(4);
        tail.ensureCapacity(2);
    }

    public Message(String head, String type, String identification, String length, String clientIp, String voltage, String current, String control, String model, String status, String duration, String serverTime, String preserveByte, String tail) {
        if (null == head || !this.head.equals(head)) throw new RuntimeException("Frame head Error!");
        if (null == tail || !this.tail.equals(tail)) throw new RuntimeException("Frame tail Error!");
//        this.head = head;
        try {
            this.type.append(type.toUpperCase());
            this.identification.append(identification.toUpperCase());
            this.length.append(length.toUpperCase());
            this.clientIp.append(clientIp.toUpperCase());
            this.voltage.append(voltage.toUpperCase());
            this.current.append(current.toUpperCase());
            this.control.append(control.toUpperCase());
            this.model.append(model.toUpperCase());
            this.status.append(status.toUpperCase());
            this.duration.append(duration.toUpperCase());
            this.serverTime.append(serverTime.toUpperCase());
            this.preserveByte.append(preserveByte.toUpperCase());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getClientHeartBeatMessage(String message) {
        if (DataFrame.invalidDataFrameCheck(message)) throw new RuntimeException("Not a valid message");
        if (!DataFrame.dataFrameTypeClassify(message).equals(DataFrameType.HeartBeat))
            throw new RuntimeException("Not a valid client heartbeat message");

        setType(new StringBuilder(message.substring(4, 6)));
        setIdentification(new StringBuilder(message.substring(6, 10)));
        setLength(new StringBuilder(message.substring(10, 12)));
        setClientIp(new StringBuilder(message.substring(12, 20)));
    }

    public void generateServerHeartBeatMessage(Message message) {
        if (!DataFrame.dataFrameTypeClassify(message.toString().replaceAll(" +", "")).equals(DataFrameType.HeartBeat))
            throw new RuntimeException("setServerHeartBeatMessage Error, not a Control frame from Client!");
//        message = message.replaceAll(" +", "");

        setType(message.getType());
        setIdentification(new StringBuilder(String.format("%016X", Integer.parseInt(message.getIdentification().toString(), 16) + 1)));
        setLength(new StringBuilder("0A"));
        setClientIp(message.getClientIp());
        setServerTime(new StringBuilder(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())));
    }

    public void getServerControlMessage() {

        setType(new StringBuilder("0C"));
        setIdentification(new StringBuilder(Integer.parseInt(getIdentification().toString(),16)));
        setLength(new StringBuilder("0C"));
        setVoltage(new StringBuilder("00"));
        setCurrent(new StringBuilder("00"));
        setControl(new StringBuilder("00"));
        setPreserveByte(new StringBuilder("0000"));
        setClientIp(new StringBuilder("127.0.0.1"));
    }


    public StringBuilder getType() {
        return type;
    }

    public void setType(StringBuilder type) {
        this.type = type;
    }

    public StringBuilder getIdentification() {
        return identification;
    }

    public void setIdentification(StringBuilder identification) {
        this.identification = identification;
    }

    public StringBuilder getLength() {
        return length;
    }

    public void setLength(StringBuilder length) {
        this.length = length;
    }

    public StringBuilder getClientIp() {
        return clientIp;
    }

    public void setClientIp(StringBuilder clientIp) {
        this.clientIp = new StringBuilder(StringUtils.ip2HexStr(clientIp.toString()));
    }

    public void setClientIp(String clientIp) {
        this.clientIp = new StringBuilder(StringUtils.ip2HexStr(clientIp));
    }

    public StringBuilder getVoltage() {
        return voltage;
    }

    public void setVoltage(StringBuilder voltage) {
        this.voltage = voltage;
    }

    public StringBuilder getCurrent() {
        return current;
    }

    public void setCurrent(StringBuilder current) {
        this.current = current;
    }

    public StringBuilder getControl() {
        return control;
    }

    public void setControl(StringBuilder control) {
        this.control = control;
    }

    public StringBuilder getModel() {
        return model;
    }

    public void setModel(StringBuilder model) {
        this.model = model;
    }

    public StringBuilder getStatus() {
        return status;
    }

    public void setStatus(StringBuilder status) {
        this.status = status;
    }

    public StringBuilder getDuration() {
        return duration;
    }

    public void setDuration(StringBuilder duration) {
        this.duration = duration;
    }

    public StringBuilder getServerTime() {
        return serverTime;
    }

    public void setServerTime(StringBuilder serverTime) {
        this.serverTime = serverTime;
    }

    public StringBuilder getPreserveByte() {
        return preserveByte;
    }

    public void setPreserveByte(StringBuilder preserveByte) {
        this.preserveByte = preserveByte;
    }

    @Override
    public String toString() {
        return head.append(type).append(identification).append(length).append(clientIp).append(voltage).append(current).append(control).append(model).append(status).append(serverTime).append(preserveByte).append(tail).toString().toUpperCase();
    }
}
