package com.longriver.kejiapower.POJO;

public class Message {
    private StringBuilder head;
    private StringBuilder type;
    private StringBuilder identification;
    private StringBuilder length;
    private StringBuilder clientIp;
    private StringBuilder voltage;
    private StringBuilder current;
    private StringBuilder control;
    private StringBuilder status;
    private StringBuilder model;
    private StringBuilder duration;
    private StringBuilder serverTime;
    private StringBuilder preserveByte;
    private StringBuilder tail;


//    private StringBuilder head = new StringBuilder(4);
//    private StringBuilder type = new StringBuilder(2);
//    private StringBuilder identification = new StringBuilder(4);
//    private StringBuilder length = new StringBuilder(2);
//    private StringBuilder clientIp = new StringBuilder(8);
//    private StringBuilder voltage = new StringBuilder(4);
//    private StringBuilder current = new StringBuilder(4);
//    private StringBuilder control = new StringBuilder(2);
//    private StringBuilder status = new StringBuilder(2);
//    private StringBuilder model = new StringBuilder(2);
//    private StringBuilder duration = new StringBuilder(4);
//    private StringBuilder serverTime = new StringBuilder(12);
//    private StringBuilder preserveByte = new StringBuilder(4);
//    private StringBuilder tail = new StringBuilder(2);


    public Message() {
        head = new StringBuilder(4);
        type = new StringBuilder(2);
        identification = new StringBuilder(4);
        length = new StringBuilder(2);
        clientIp = new StringBuilder(8);
        voltage = new StringBuilder(4);
        current = new StringBuilder(4);
        control = new StringBuilder(2);
        status = new StringBuilder(2);
        model = new StringBuilder(2);
        duration = new StringBuilder(4);
        serverTime = new StringBuilder(12);
        preserveByte = new StringBuilder(4);
        tail = new StringBuilder(2);

        head.ensureCapacity(4);
        type.ensureCapacity(2);
        identification.ensureCapacity(4);
        length.ensureCapacity(2);
        clientIp.ensureCapacity(8);
        voltage.ensureCapacity(4);
        current.ensureCapacity(4);
        control.ensureCapacity(2);
        status.ensureCapacity(2);
        model.ensureCapacity(2);
        duration.ensureCapacity(4);
        serverTime.ensureCapacity(12);
        preserveByte.ensureCapacity(4);
        tail.ensureCapacity(2);
    }

//    public Message(String head, String type, String identification, String length, String clientIp, String voltage, String current, String control,  String status, String model,String duration, String serverTime, String preserveByte, String tail) {
//        if (null == head || !this.head.equals(head)) throw new RuntimeException("Frame head Error!");
//        if (null == tail || !this.tail.equals(tail)) throw new RuntimeException("Frame tail Error!");
////        this.head = head;
//        try {
//            this.type.append(type.toUpperCase());
//            this.identification.append(identification.toUpperCase());
//            this.length.append(length.toUpperCase());
//            this.clientIp.append(clientIp.toUpperCase());
//            this.voltage.append(voltage.toUpperCase());
//            this.current.append(current.toUpperCase());
//            this.control.append(control.toUpperCase());
//            this.status.append(status.toUpperCase());
//            this.model.append(model.toUpperCase());
//            this.duration.append(duration.toUpperCase());
//            this.serverTime.append(serverTime.toUpperCase());
//            this.preserveByte.append(preserveByte.toUpperCase());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


    public StringBuilder getHead() {
        return head;
    }

    public void setHead(StringBuilder head) {
        this.head = head;
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
        this.clientIp = clientIp;
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

    public StringBuilder getStatus() {
        return status;
    }

    public void setStatus(StringBuilder status) {
        this.status = status;
    }

    public StringBuilder getModel() {
        return model;
    }

    public void setModel(StringBuilder model) {
        this.model = model;
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

    public StringBuilder getTail() {
        return tail;
    }

    public void setTail(StringBuilder tail) {
        this.tail = tail;
    }

    public String toString() {
        return new StringBuilder().append(head).append(type).append(identification).append(length).append(clientIp).append(voltage).append(current).append(control).append(status).append(model).append(serverTime).append(preserveByte).append(tail).toString().toUpperCase();
    }
}
