package dev.rohit.userServices.dtos;

//import lombok.Getter;
//import lombok.Setter;

//@Getter
//@Setter
public class LogoutRequestDTO {
    private String token;
    private Long userId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
