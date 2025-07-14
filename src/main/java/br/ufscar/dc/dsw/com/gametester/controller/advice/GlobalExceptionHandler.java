package br.ufscar.dc.dsw.com.gametester.controller.advice;

import br.ufscar.dc.dsw.com.gametester.exception.AuthorizationException;
import br.ufscar.dc.dsw.com.gametester.exception.DataConflictException;
import br.ufscar.dc.dsw.com.gametester.exception.InvalidDataException;
import br.ufscar.dc.dsw.com.gametester.exception.ResourceNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

// Esta classe vai interceptar exceções de todos os @RestControllers
@RestControllerAdvice
public class GlobalExceptionHandler {

    // DTO simples para a resposta de erro
    public record ErrorResponse(String message, Map<String, String> details) {
        public ErrorResponse(String message) {
            this(message, null);
        }
    }

    // Handler para Recurso Não Encontrado (404)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    // Handler para Conflito de Dados (ex: e-mail duplicado) (409)
    @ExceptionHandler(DataConflictException.class)
    public ResponseEntity<ErrorResponse> handleDataConflict(DataConflictException ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    // Handler para Dados Inválidos (ex: senhas não batem) (400)
    @ExceptionHandler(InvalidDataException.class)
    public ResponseEntity<ErrorResponse> handleInvalidData(InvalidDataException ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // Handler para erros de violação de integridade do banco (ex: deletar usuário com dados associados) (403)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex) {
        ErrorResponse error = new ErrorResponse("Operação não permitida. O recurso pode estar associado a outros dados.");
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    // Handler para erros de validação do @Valid nos DTOs (400)
    // Este é MUITO importante para dar um feedback claro sobre qual campo falhou
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        ErrorResponse error = new ErrorResponse("Erro de validação.", errors);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // ... outros handlers ...

    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<ErrorResponse> handleAuthorizationException(AuthorizationException ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN); // Retorna 403
    }
}