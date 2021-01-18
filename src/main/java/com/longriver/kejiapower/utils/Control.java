package com.longriver.kejiapower.utils;

public enum Control {

    INITIATE("INITIATE", (short)0x01 ),
    TERMINATE("TERMINATE", (short)0x00),
    INVALID("INVALID", (short)0xff),
    ;

    private String status;
    /**
     * 枚举信息
     */
    private short code;

    Control() {
        status = "Invalid";
        code = 0xff;
    }

    private Control(String status, short code) {
        this.status = status;
        this.code = code;
    }

    /**
     * 根据枚举值获取枚举对象，如果找不到对应的枚举返回<code>null</code>
     *
     * @param status 枚举值
     * @return 枚举对象
     */
    public static Control getWorkingStatusByStatus(String status) {
        for (Control workingStatus : Control.values()) {
            if (workingStatus.getFrameType().equals(status)) {
                return workingStatus;
            }
        }
        return null;
    }
    public static Control getWorkingStatusByCode(short code) {
        for (Control control : Control.values()) {
            if (control.getCode() == code) {
                return control;
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

    @Override
    public String toString() {
        return status ;
    }
}
