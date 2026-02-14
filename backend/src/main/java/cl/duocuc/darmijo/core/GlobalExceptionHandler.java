package cl.duocuc.darmijo.core;

import cl.duocuc.darmijo.core.exceptions.AuthorityException;
import cl.duocuc.darmijo.core.exceptions.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body("Recurso no encontrado: " + ex.getMessage());
        
    }
    
    @ExceptionHandler(AuthorityException.class)
    public ResponseEntity<?> handleAuthorityException(AuthorityException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body("Acceso denegado: " + ex.getMessage());
        
    }
    
}

