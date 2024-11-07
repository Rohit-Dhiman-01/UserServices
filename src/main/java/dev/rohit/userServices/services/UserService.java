package dev.rohit.userServices.services;

import dev.rohit.userServices.dtos.UserDTO;
import dev.rohit.userServices.models.Role;
import dev.rohit.userServices.models.User;
import dev.rohit.userServices.repositories.RoleRepository;
import dev.rohit.userServices.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

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
    public UserDTO setUserRoles(Long userId, List<Long> roleIds) {
        Optional<User> userOptional = userRepository.findById(userId);
        List<Role> roles = roleRepository.findAllByIdIn(roleIds);

        if (userOptional.isEmpty()) {
            return null;
        }

        User user = userOptional.get();
        user.setRoles(Set.copyOf(roles));

        User savedUser = userRepository.save(user);

        return UserDTO.from(savedUser);
    }
}
