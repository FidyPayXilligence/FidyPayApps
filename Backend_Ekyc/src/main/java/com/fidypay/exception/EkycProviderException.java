package com.fidypay.exception;

public class EkycProviderException extends Exception {

    private static final long serialVersionUID = 1L;


    public EkycProviderException(String message) {
        super(message);
    }

    public EkycProviderException(String message, Throwable cause) {
        super(message, cause);
    }

    public EkycProviderException(Throwable cause) {
        super(cause);
    }

}
