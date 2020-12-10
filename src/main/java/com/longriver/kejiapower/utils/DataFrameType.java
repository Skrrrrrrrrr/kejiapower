package com.longriver.kejiapower.utils;

public enum DataFrameType {

    HeartBeat("HeartBeatFrame", 0),
    Control("ControlFrame", 1),
//    Massage("MassageFrame", 2),
    Report("ReportFrame", 2),
    Invalid("InvalidFrame", -1),;

    private String frameType;
    /**
     * 枚举信息
     */
    private int code;

    private DataFrameType(String frameType, int code) {
        this.frameType = frameType;
        this.code = code;
    }

    /**
     * 根据枚举值获取枚举对象，如果找不到对应的枚举返回<code>null</code>
     *
     * @param frameType 枚举值
     * @return 枚举对象
     */
    public static DataFrameType getDataFrameTypeByType(String frameType) {
        for (DataFrameType dataFrameType : DataFrameType.values()) {
            if (dataFrameType.getFrameType().equals(frameType)) {
                return dataFrameType;
            }
        }
        return null;
    }

    /*
     * getter
     */

    public String getFrameType() {
        return frameType;
    }

    public int getCode() {
        return code;
    }
}
