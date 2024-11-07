package dev.rohit.userServices.models;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.Entity;
//import lombok.Getter;
//import lombok.Setter;

@Entity
//@Getter
//@Setter
@JsonDeserialize(as = Role.class)
public class Role extends BaseModel{
    private String role;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
