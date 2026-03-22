package com.ecomerce.sbecom.Exceptions;

public class ApiException extends RuntimeException{
    private static final Long serialVersionUid = 1L;

    public ApiException(){
    }
    public ApiException(String message) {
        super(message);
    }

}
