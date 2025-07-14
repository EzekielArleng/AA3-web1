package br.ufscar.dc.dsw.com.gametester.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Anotar com @ResponseStatus é uma alternativa, mas o Handler nos dá mais controle.
// Deixaremos sem para usar o Handler.
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}