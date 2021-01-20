package com.longriver.kejiapower.utils;

public enum WorkingStatus {

    HARDWARE_FAULT("HARDWAREFAULT", (short) 0x01),
    OT("OT", (short) 0x02),
    HARDWARE_FAULT__OT("HARDWARE_FAULT__OT", (short) 0x03),
    INPUT_UV("INPUTUV", (short) 0x04),
    INPUT_UV__HARDWARE_FAULT("INPUT_UV__HARDWARE_FAULT", (short) 0x05),
    INPUT_UV__OT("HARDWARE_FAULT__OT", (short) 0x06),
    INPUT_UV__HARDWARE_FAULT__OT("HARDWARE_FAULT__OT__INPUT_UV", (short) 0x07),
    SHUTOFF("SHUTOFF", (short) 0x08),
    SHUTOFF__HARDWARE_FAULT("SHUTOFF__HARDWARE_FAULT", (short) 0x09),
    SHUTOFF__OT("SHUTOFF__OT", (short) 0x0a),
    SHUTOFF__HARDWARE_FAULT__OT("SHUTOFF__INPUT_UV", (short) 0x0b),
    SHUTOFF__INPUT_UV("SHUTOFF__INPUT_UV", (short) 0x0c),
    SHUTOFF__INPUT_UV__HARDWARE_FAULT("SHUTOFF__INPUT_UV__HARDWARE_FAULT", (short) 0x0d),
    SHUTOFF__INPUT_UV__OT("SHUTOFF__INPUT_UV__OT", (short) 0x0e),
    SHUTOFF__INPUT_UV__HARDWARE_FAULT__OT("HARDWARE_FAULT_OT", (short) 0x0f),
    STARTUP("STARTUP", (short) 0x10),
    STARTUP__HARDWARE_FAULT("STARTUP__HARDWARE_FAULT", (short) 0x11),
    STARTUP__OT("STARTUP__OT", (short) 0x12),
    STARTUP__HARDWARE_FAULT__OT("STARTUP__HARDWARE_FAULT__OT", (short) 0x13),
    STARTUP__INPUT_UV("STARTUP__INPUT_UV", (short) 0x14),
    STARTUP__INPUT_UV__HARDWARE_FAULT("STARTUP__INPUT_UV__HARDWARE_FAULT", (short) 0x15),
    STARTUP__INPUT_UV__OT("STARTUP__INPUT_UV__OT", (short) 0x16),
    STARTUP__INPUT_UV__HARDWARE_FAULT__OT("STARTUP__INPUT_UV__HARDWARE_FAULT__OT", (short) 0x17),

    COMMUNICATION_TIMEOUT("COMMUNICATIONTIMEOUT", (short) 0x20),
    UNKNOWN("UNKNOWN", (short) 0xff),

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
            if (workingStatus.getStatus().equals(status)) {
                return workingStatus;
            }
        }
        return null;
    }

    /**
     * 根据枚举值获取枚举对象，如果找不到对应的枚举返回<status>null</status>
     *
     * @param code 枚举值
     * @return 枚举对象
     */
    public static WorkingStatus getWorkingStatusByCode(Short code) {
        for (WorkingStatus workingStatus : WorkingStatus.values()) {
            if (workingStatus.getCode() == code) {
                return workingStatus;
            }
        }
        return null;
    }

    /*
     * getter
     */

    public String getStatus() {
        return status;
    }

    public short getCode() {
        return code;
    }

    @Override
    public String toString() {
        return status ;
    }
}
