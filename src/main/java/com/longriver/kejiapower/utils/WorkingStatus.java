package com.longriver.kejiapower.utils;

public enum WorkingStatus {

    HARDWARE_FAULT("HARDWAREFAULT", (short)0x01),
    OT("OT", (short)0x02),
    INPUT_UV("INPUTUV", (short)0x04),
    SHUTOFF("SHUTOFF", (short)0x08),
    STARTUP("STARTUP", (short)0x10),
    COMMUNICATION_TIMEOUT("COMMUNICATIONTIMEOUT",(short)0x20),
    ;

    private String status;
    private short code;

    private WorkingStatus(String status, short code) {
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

    public short getCode() {
        return code;
    }
}
