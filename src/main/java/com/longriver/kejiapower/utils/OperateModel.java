package com.longriver.kejiapower.utils;

public enum OperateModel {

    LOCAL("LOCAL",(short)0x00),
    DAEMON50A("50A_24/100", (short)0x02),
    DAEMON35A("35A", (short)0x03),
    DAEMON15A("15A", (short)0x04),
    DAEMON15A_24_100("15A_24/100", (short)0x05),
    INVALID("INVALID",(short)0xff);


    private String runningModel;
    private short code;

    OperateModel() {
        runningModel = "CV";
        code = -1;
    }

    private OperateModel(String runningModel, short code) {
        this.runningModel = runningModel;
        this.code = code;
    }

    /**
     * 根据枚举值获取枚举对象，如果找不到对应的枚举返回<code>null</code>
     *
     * @param runningModel 枚举值
     * @return 枚举对象
     */
    public static OperateModel getOperateModelByModel(String runningModel) {
        for (OperateModel operateModel : OperateModel.values()) {
            if (operateModel.getRunningModel().equals(runningModel)) {
                return operateModel;
            }
        }
        return null;
    }

    public static OperateModel getOperateModelByCode(Short code) {
        for (OperateModel operateModel : OperateModel.values()) {
            if (operateModel.getCode() == code) {
                return operateModel;
            }
        }
        return null;
    }


    public String getRunningModel() {
        return runningModel;
    }

    public short getCode() {
        return code;
    }
}
