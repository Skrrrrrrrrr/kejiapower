package com.longriver.kejiapower.model;

import com.longriver.kejiapower.utils.OperateModel;
import com.longriver.kejiapower.utils.WorkingStatus;

public class Client {

//    control = new StringBuilder(2);
//    status = new StringBuilder(2);
//    model = new StringBuilder(2);
//    duration = new StringBuilder(4);
//    serverTime = new StringBuilder(12);

    private String name;
    private String ip;
    private OperateModel operateModel;
    private WorkingStatus workingStatus;

    private float voltage = 0.0f;
    private float current = 0.0f;
    private float power = 0.0f;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public OperateModel getOperateModel() {
        return operateModel;
    }

    public void setOperateModel(OperateModel operateModel) {
        this.operateModel = operateModel;
    }

    public WorkingStatus getWorkingStatus() {
        return workingStatus;
    }

    public void setWorkingStatus(WorkingStatus workingStatus) {
        this.workingStatus = workingStatus;
    }

    public float getVoltage() {
        return voltage;
    }

    public void setVoltage(float voltage) {
        this.voltage = voltage;
    }

    public float getCurrent() {
        return current;
    }

    public void setCurrent(float current) {
        this.current = current;
    }

    public float getPower() {
        return power;
    }

    public void setPower(float power) {
        this.power = power;
    }
}
