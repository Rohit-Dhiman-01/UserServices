package dev.rohit.userServices.controllers;

import dev.rohit.userServices.dtos.SetUserRolesRequestDTO;
import dev.rohit.userServices.dtos.UserDTO;
import dev.rohit.userServices.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/admin")
public class UserController {
    private UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserDetails(@PathVariable("id") Long userId) {
          return new ResponseEntity<>(userService.getUserRole(userId) , HttpStatus.OK);

    }
    @PutMapping("/{id}/roles")
    public ResponseEntity<UserDTO> setUserRoles(@PathVariable("id") Long userId, @RequestBody SetUserRolesRequestDTO request) {

        UserDTO userDto = userService.setUserRoles(userId, request.getRoleIds());

        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }
}
