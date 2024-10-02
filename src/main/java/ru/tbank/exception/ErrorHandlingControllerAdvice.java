package ru.tbank.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class ErrorHandlingControllerAdvice {

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        log.error("Ошибка: {}", ex.getMessage(), ex);
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(ex.getMessage());
        return response;
    }

    @ExceptionHandler(value = EntityNotFoundException.class)
    public ResponseEntity<String> handleCategoryNotFoundException(EntityNotFoundException ex) {
        log.error(ex.getMessage(), ex);
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.NOT_FOUND.value()).body(ex.getMessage());
        return response;
    }
}
