package br.ufscar.dc.dsw.com.gametester.exception;

public class AuthorizationException extends RuntimeException {
    public AuthorizationException(String message) {
        super(message);
    }
}