package com.bidanet.hprose.starter.exception;

/**
 * Created by xuejike on 2017/6/28.
 */
public class HproseConfigException extends Exception {
    public HproseConfigException() {
    }

    public HproseConfigException(String message) {
        super(message);
    }

    public HproseConfigException(String message, Throwable cause) {
        super(message, cause);
    }

    public HproseConfigException(Throwable cause) {
        super(cause);
    }

    public HproseConfigException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
