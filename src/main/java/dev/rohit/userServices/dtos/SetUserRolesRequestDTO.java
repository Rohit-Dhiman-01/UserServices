package dev.rohit.userServices.dtos;

//import lombok.Getter;
//import lombok.Setter;

import java.util.List;

//@Getter
//@Setter
public class SetUserRolesRequestDTO {
    List<Long> roleIds;

    public List<Long> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<Long> roleIds) {
        this.roleIds = roleIds;
    }
}
