package com.longriver.kejiapower.utils;

public enum OperateModel {

    ConstantCurrent("CC", 0),
    ConstantVoltage("CV", 1),
    ConstantPower("CP", 2),
    Invalid("Invalid", -1),
    ;

    private String runningModel;
    /**
     * 枚举信息
     */
    private int code;

    OperateModel() {
        runningModel = "CV";
        code = -1;
    }

    private OperateModel(String runningModel, int code) {
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
            if (operateModel.getFrameType().equals(runningModel)) {
                return operateModel;
            }
        }
        return null;
    }

    /*
     * getter
     */

    public String getFrameType() {
        return runningModel;
    }

    public int getCode() {
        return code;
    }
}
