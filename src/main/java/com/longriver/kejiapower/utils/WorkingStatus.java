package com.longriver.kejiapower.utils;

public enum WorkingStatus {

    ONLINE("OnLine", 0),
    OFFLINE("OffLine", 1),
    Invalid("Invalid", -1),
    ;

    private String status;
    /**
     * 枚举信息
     */
    private int code;

    WorkingStatus() {
        status = "CV";
        code = -1;
    }

    private WorkingStatus(String status, int code) {
        this.status = status;
        this.code = code;
    }

    /**
     * 根据枚举值获取枚举对象，如果找不到对应的枚举返回<code>null</code>
     *
     * @param status 枚举值
     * @return 枚举对象
     */
    public static WorkingStatus getWorkingStatusByStatus(String status) {
        for (WorkingStatus workingStatus : WorkingStatus.values()) {
            if (workingStatus.getFrameType().equals(status)) {
                return workingStatus;
            }
        }
        return null;
    }

    /*
     * getter
     */

    public String getFrameType() {
        return status;
    }

    public int getCode() {
        return code;
    }
}
