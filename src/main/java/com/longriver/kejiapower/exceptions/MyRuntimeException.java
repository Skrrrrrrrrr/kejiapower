package com.longriver.kejiapower.exceptions;

public class MyRuntimeException extends  RuntimeException{
    /**
     * 运行时异常根类。【这个类可以打进jar包，供使用】
     *
     * 统一异常层次，便于处理与监控。
     *
     */
        /** serialVersionUID */
        private static final long serialVersionUID = 0L;

        /** 严重级别 */
        protected int severity = MyExceptionSeverity.NORMAL;

        /**
         * 空构造器。
         */
        public MyRuntimeException() {
            super();
        }

        /**
         * 构造器。
         *
         * @param severity 严重级别
         */
        public MyRuntimeException(int severity) {
            super();
            this.severity = severity;
        }

        /**
         * 构造器。
         *
         * @param message 消息
         */
        public MyRuntimeException(String message) {
            super(message);
        }

        /**
         * 构造器。
         *
         * @param message 消息
         * @param severity 严重级别
         */
        public MyRuntimeException(String message, int severity) {
            super(message);

            this.severity = severity;
        }

        /**
         * 构造器。
         *
         * @param cause 原因
         */
        public MyRuntimeException(Throwable cause) {
            super(cause);
        }

        /**
         * 构造器。
         *
         * @param cause 原因
         * @param severity 严重级别
         */
        public MyRuntimeException(Throwable cause, int severity) {
            super(cause);

            this.severity = severity;
        }

        /**
         * 构造器。
         *
         * @param message 消息
         * @param cause 原因
         */
        public MyRuntimeException(String message, Throwable cause) {
            super(message, cause);
        }

        /**
         * 构造器。
         *
         * @param message 消息
         * @param cause 原因
         * @param severity 严重级别
         */
        public MyRuntimeException(String message, Throwable cause, int severity) {
            super(message, cause);

            this.severity = severity;
        }

        /**
         * @return Returns the severity.
         */
        public int getSeverity() {
            return severity;
        }

        /**
         * @see java.lang.Throwable#toString()
         */
        public String toString() {
            StringBuffer buffer = new StringBuffer();

            buffer.append(super.toString()).append(" - severity: ");

            switch (severity) {
                case MyExceptionSeverity.MINOR:
                    buffer.append("MINOR");
                    break;

                case MyExceptionSeverity.NORMAL:
                    buffer.append("NORMAL");
                    break;

                case MyExceptionSeverity.MAJOR:
                    buffer.append("MAJOR");
                    break;

                case MyExceptionSeverity.CRITICAL:
                    buffer.append("CRITICAL");
                    break;

                default:
                    buffer.append("UNKNOWN");
            }

            buffer.append("(").append(severity).append(")");

            return buffer.toString();
        }
}
