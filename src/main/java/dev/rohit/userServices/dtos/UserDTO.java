package dev.rohit.userServices.dtos;

import dev.rohit.userServices.models.Role;
import dev.rohit.userServices.models.User;
//import lombok.Getter;
//import lombok.Setter;
import java.util.HashSet;
import java.util.Set;

//@Getter
//@Setter
public class UserDTO {
    private String email;
    private Set<Role> roles = new HashSet<>();

    public static UserDTO from(User user) {
        UserDTO userDto = new UserDTO();
        userDto.setEmail(user.getEmail());
        userDto.setRoles(user.getRoles());
        return userDto;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
