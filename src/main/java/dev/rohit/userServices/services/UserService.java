package dev.rohit.userServices.services;

import dev.rohit.userServices.dtos.UserDTO;
import dev.rohit.userServices.exception.UserAlreadyExists;
import dev.rohit.userServices.models.Role;
import dev.rohit.userServices.models.User;
import dev.rohit.userServices.repositories.RoleRepository;
import dev.rohit.userServices.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }
    public UserDTO getUserDetails(Long userId) {
        return new UserDTO(); // returning an empty user for now. Update this to fetch user details from the DB
    }

    public  List<Object> getUserRole(Long userId){
        List<Object> userWithRolesNative = userRepository.findUserWithRolesNative(userId);
        if (userWithRolesNative.isEmpty()){
            return new ArrayList<>();
        }
        return userWithRolesNative;
    }

    public UserDTO setUserRoles(Long userId, List<Long> roleIds) {
        Optional<User> userOptional = userRepository.findById(userId);
        List<Role> roles = roleRepository.findAllByIdIn(roleIds);
        if (roles.size() != roleIds.size()) {
            throw new EntityNotFoundException("Some roles were not found for the provided IDs");
        }

        Set<Role> rolesSet = new HashSet<>(roles);
        if (roleIds.isEmpty()) {
            throw new IllegalArgumentException("Role IDs cannot be null or empty");
        }

        if (userOptional.isEmpty()) {
            throw new UserAlreadyExists("Can not find user with ID +" +userId );
        }

        User user = userOptional.get();
        user.setRoles(rolesSet);

        User savedUser = userRepository.save(user);

        return UserDTO.from(savedUser);
    }
}
