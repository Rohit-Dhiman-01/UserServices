package dev.rohit.userServices.exception;

public class TokenAlreadyExpire extends RuntimeException{
    public TokenAlreadyExpire(String Message){
        super(Message);
    }
}
