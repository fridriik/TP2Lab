package com.turnosrotativos.exception;

import com.turnosrotativos.service.EmpleadoService;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        StringBuilder errorMessages = new StringBuilder();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errorMessages.append(error.getDefaultMessage()).append(" ");
        }
        return buildResponse(errorMessages.toString().trim(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        String errorMessage;
        if (e.getRequiredType() == LocalDate.class) {
            errorMessage = "Los campos ‘fechaDesde’ y ‘fechaHasta’ deben respetar el formato yyyy-mm-dd.";
        } else if (e.getRequiredType() == Integer.class) {
            errorMessage = "El campo ‘nroDocumento’ solo puede contener números enteros.";
        } else {
            errorMessage = e.getMessage();
        }
        return buildResponse(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequestException(BadRequestException e) {
        return buildResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Map<String, Object>> handleConflictException(ConflictException e) {
        return buildResponse(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFoundException(NotFoundException e) {
        return buildResponse(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidDateFormatException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidDateFormatException(InvalidDateFormatException e) {
        return buildResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    //Al tener las columnas nro_documento y email con unique violaban integridad de datos
    //se catchean y se controlan desde aca
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        Throwable cause = ex.getCause();
        if (cause instanceof ConstraintViolationException constraintViolation) {
            String constraintName = constraintViolation.getConstraintName();
            if (constraintName != null) {
                // CONSTRAINT_INDEX_42 --> EMAIL
                if (constraintName.contains("CONSTRAINT_INDEX_42")) {
                    logger.error("Violación de restricción de unicidad para email");
                    return buildResponse("Ya existe un empleado con el email ingresado.", HttpStatus.CONFLICT);
                }
                // CONSTRAINT_INDEX_4 --> NRO_DOCUMENTO
                else if (constraintName.contains("CONSTRAINT_INDEX_4")) {
                    logger.error("Violación de restricción de unicidad para documento");
                    return buildResponse("Ya existe un empleado con el documento ingresado.", HttpStatus.CONFLICT);
                }
            }
        }
        // Si no hay coincidencias específicas, manejar como un conflicto genérico
        return buildResponse("Conflicto de datos de integridad.", HttpStatus.CONFLICT);
    }

    private ResponseEntity<Map<String, Object>> buildResponse(String message, HttpStatus status) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("Status Code", status.value());
        response.put("Mensaje", message);
        return new ResponseEntity<>(response, status);
    }
}
