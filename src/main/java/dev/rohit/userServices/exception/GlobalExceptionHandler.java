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
    public ResponseEntity<UserDTO> handleException(UserAlreadyExists userAlreadyExists){
        return new ResponseEntity<>(new UserDTO(),HttpStatus.IM_USED);
    }

    @ExceptionHandler(TokenAlreadyExpire.class)
    public ResponseEntity<?> handle(TokenAlreadyExpire tokenAlreadyExpire){
        return new ResponseEntity<>(new UserDTO(),HttpStatus.UNAUTHORIZED);
    }
}
