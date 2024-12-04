package dev.rohit.userServices.dtos;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class ExceptionDTO {
    private HttpStatus status;
    private String message;

    public ExceptionDTO(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
