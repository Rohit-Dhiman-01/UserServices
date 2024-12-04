package dev.rohit.userServices.exception;

public class UserAlreadyExists extends RuntimeException{
    public UserAlreadyExists(String Message){
        super(Message);
    }
}
