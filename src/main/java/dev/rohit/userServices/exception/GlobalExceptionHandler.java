package dev.rohit.userServices.exception;

import dev.rohit.userServices.dtos.ExceptionDTO;
import dev.rohit.userServices.dtos.UserDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UserAlreadyExists.class)
    public ResponseEntity<ExceptionDTO> handleException(UserAlreadyExists userAlreadyExists){
        return new ResponseEntity<>(new ExceptionDTO(HttpStatus.CONFLICT, userAlreadyExists.getMessage()),HttpStatus.CONFLICT);
    }

    @ExceptionHandler(TokenAlreadyExpire.class)
    public ResponseEntity<ExceptionDTO> handle(TokenAlreadyExpire tokenAlreadyExpire){
        return new ResponseEntity<>(new ExceptionDTO(HttpStatus.UNAUTHORIZED, tokenAlreadyExpire.getMessage()),HttpStatus.UNAUTHORIZED);
    }
}
