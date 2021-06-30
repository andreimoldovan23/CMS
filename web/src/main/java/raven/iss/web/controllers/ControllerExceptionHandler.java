package raven.iss.web.controllers;

import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import raven.iss.data.exceptions.BadRequestException;
import raven.iss.data.exceptions.InternalErrorException;
import raven.iss.data.exceptions.NotFoundException;
import raven.iss.web.security.jwt.UserNotActivatedException;

import javax.validation.ConstraintViolationException;
import java.time.DateTimeException;
import java.time.format.DateTimeParseException;

@ControllerAdvice
public class ControllerExceptionHandler {

    private static final String BAD_REQUEST = "We couldn't process the data you sent us";
    private static final String INTERNAL_ERROR = "The server encountered a problem";

    @ExceptionHandler({NotFoundException.class, UsernameNotFoundException.class})
    public ResponseEntity<Object> handleNotFound(Exception exception) {
        return new ResponseEntity<>(exception.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleUserBadRequest(Exception exception) {
        return new ResponseEntity<>(exception.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentNotValidException.class,
            DateTimeParseException.class, DateTimeException.class})
    public ResponseEntity<Object> handleAppBadRequest() {
        return new ResponseEntity<>(BAD_REQUEST, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ConstraintViolationException.class, JdbcSQLIntegrityConstraintViolationException.class})
    public ResponseEntity<Object> handleAppInternalError() {
        return new ResponseEntity<>(INTERNAL_ERROR, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({InternalErrorException.class, UserNotActivatedException.class})
    public ResponseEntity<Object> handleUserInternalError(Exception exception) {
        return new ResponseEntity<>(exception.getMessage(), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
