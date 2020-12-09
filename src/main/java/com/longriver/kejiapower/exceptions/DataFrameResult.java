package com.longriver.kejiapower.exceptions;

import com.longriver.kejiapower.utils.StringUtils;

import java.io.Serializable;

public class DataFrameResult implements Serializable {


    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * 成功标志
     */
    protected boolean success;

    /**
     * 返回码
     */
    private DataFrameResultCodeEnum resultCode;

    /**
     * 返回信息
     */
    protected String message;

    /**
     * 构造函数，默认返回码为“SUCCESS”。
     */
    public DataFrameResult() {
        resultCode = DataFrameResultCodeEnum.SUCCESS;
        this.message = resultCode.getMessage();
        success = true;
    }

    /**
     * 构造函数
     *
     * @param success 成功标志
     */
    public DataFrameResult(boolean success) {
        this.success = success;
        this.resultCode = DataFrameResultCodeEnum.SYSTEM_FAILURE;
        this.message = resultCode.getMessage();
    }

    /**
     * 构造函数
     *
     * @param resultCode 返回码
     */
    public DataFrameResult(DataFrameResultCodeEnum resultCode) {
        success = resultCode == DataFrameResultCodeEnum.SUCCESS;
        this.resultCode = resultCode;
        this.message = resultCode.getMessage();
    }

    /**
     * 构造函数
     *
     * @param resultCode 返回码
     */
    public DataFrameResult(boolean success, DataFrameResultCodeEnum resultCode) {
        this.success = success;
        this.resultCode = resultCode;
        this.message = resultCode.getMessage();
    }

    /**
     * 返回结果信息
     *
     * @return 如果resultCode存在，返回resultCode的message，否则返回result中的message字段
     */
    public String findMessage() {
        return StringUtils.isNotBlank(message) ? message : resultCode.getMessage();
    }

    /*
     * getters and setters
     */

    /**
     * Getter method for property <tt>success</tt>.
     *
     * @return property value of success
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Setter method for property <tt>success</tt>.
     *
     * @param success value to be assigned to property success
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * Getter method for property <tt>resultCode</tt>.
     *
     * @return property value of resultCode
     */
    public DataFrameResultCodeEnum getResultCode() {
        return resultCode;
    }

    /**
     * Setter method for property <tt>resultCode</tt>.
     *
     * @param resultCode value to be assigned to property resultCode
     */
    public void setResultCode(DataFrameResultCodeEnum resultCode) {
        this.resultCode = resultCode;
    }

    /**
     * Getter method for property <tt>message</tt>.
     *
     * @return property value of message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Setter method for property <tt>message</tt>.
     *
     * @param message value to be assigned to property message
     */
    public void setMessage(String message) {
        this.message = message;
    }

//    /**
//     * @see java.lang.Object#toString()
//     */
//    @Override
//    public String toString() {
//        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
//    }

}
