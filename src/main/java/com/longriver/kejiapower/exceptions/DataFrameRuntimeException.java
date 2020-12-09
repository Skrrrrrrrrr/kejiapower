package com.longriver.kejiapower.exceptions;
/**
 * 自定义系统运行时异常
 *
 * @author
 * @version
 */

public class DataFrameRuntimeException extends MyRuntimeException {


        /** serialVersionUID */
        private static final long      serialVersionUID = 0L;

        /** 返回码 */
        private DataFrameResultCodeEnum resultCode;

        /**
         * 构造函数
         *
         * @param resultCode    返回码
         */
        public DataFrameRuntimeException(DataFrameResultCodeEnum resultCode) {
            super();
            this.resultCode = resultCode;
        }

        /**
         * 构造函数
         *
         * @param resultCode    返回码
         * @param e             需要传递的异常
         */
        public DataFrameRuntimeException(DataFrameResultCodeEnum resultCode, Throwable e) {
            super(e);
            this.resultCode = resultCode;
        }

        /**
         * 构造函数
         *
         * @param resultCode    返回码
         * @param message       错误信息
         */
        public DataFrameRuntimeException(DataFrameResultCodeEnum resultCode, String message) {
            super(message);
            this.resultCode = resultCode;
        }

        /*
         * getter and setter
         */
        public DataFrameResultCodeEnum getResultCode() {
            return resultCode;
        }

        public void setResultCode(DataFrameResultCodeEnum resultCode) {
            this.resultCode = resultCode;
        }



}
