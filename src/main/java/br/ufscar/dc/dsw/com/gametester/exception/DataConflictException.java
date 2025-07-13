package br.ufscar.dc.dsw.com.gametester.exception;

public class DataConflictException extends RuntimeException {
    public DataConflictException(String message) {
        super(message);
    }
}