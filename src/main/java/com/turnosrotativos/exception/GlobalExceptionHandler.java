package com.turnosrotativos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import java.time.LocalDate;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        StringBuilder errorMessages = new StringBuilder();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errorMessages.append(error.getDefaultMessage()).append(" ");
        }
        String responseMessage = errorMessages.toString().trim();
        return new ResponseEntity<>(responseMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public final ResponseEntity<String> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        if (e.getRequiredType() == LocalDate.class) {
            return new ResponseEntity<>("Los campos ‘fechaDesde’ y ‘fechaHasta’ deben respetar el formato yyyy-mm-dd.", HttpStatus.BAD_REQUEST);
        }
        if (e.getRequiredType() == Integer.class) {
            return new ResponseEntity<>("El campo ‘nroDocumento’ solo puede contener números enteros.", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<String> handleBadRequestException(BadRequestException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<String> handleConflictException(ConflictException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(NotFoundException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidDateFormatException.class)
    public final ResponseEntity<String> handleInvalidDateFormatException(InvalidDateFormatException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}