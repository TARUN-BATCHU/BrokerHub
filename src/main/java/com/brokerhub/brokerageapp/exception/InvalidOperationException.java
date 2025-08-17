package com.brokerhub.brokerageapp.exception;

public class InvalidOperationException extends RuntimeException {
    public InvalidOperationException(String message) {
        super(message);
    }
}