package com.khirye.rpc.serialize;

public class SerializeException extends RuntimeException {
    public SerializeException(String msg) {
        super(msg);
    }

    public SerializeException(Throwable cause) {
        super(cause);
    }
}
