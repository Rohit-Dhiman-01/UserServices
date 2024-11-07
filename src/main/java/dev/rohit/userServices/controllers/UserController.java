package dev.rohit.userServices.controllers;

import dev.rohit.userServices.dtos.SetUserRolesRequestDTO;
import dev.rohit.userServices.dtos.UserDTO;
import dev.rohit.userServices.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserDetails(@PathVariable("id") Long userId) {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("test@test.com");
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }
    @PostMapping("/{id}/roles")
    public ResponseEntity<UserDTO> setUserRoles(@PathVariable("id") Long userId, @RequestBody SetUserRolesRequestDTO request) {

        UserDTO userDto = userService.setUserRoles(userId, request.getRoleIds());

        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }
}
