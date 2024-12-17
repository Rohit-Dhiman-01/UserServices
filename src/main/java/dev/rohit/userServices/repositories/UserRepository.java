package dev.rohit.userServices.repositories;

import dev.rohit.userServices.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);

    @Query(value = "SELECT u.email AS userEmail, r.role AS roleName " +
            "FROM user AS u " +
            "JOIN user_roles AS ur ON u.id = ur.user_id " +
            "JOIN role AS r ON ur.roles_id = r.id " +
            "WHERE u.id = :userId",
            nativeQuery = true)
    List<Object> findUserWithRolesNative(Long userId);

}
