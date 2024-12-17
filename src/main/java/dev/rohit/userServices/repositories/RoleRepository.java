package dev.rohit.userServices.repositories;

import dev.rohit.userServices.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    List<Role> findAllByIdIn(List<Long> roleIds);
    Optional<Role> findByRole(String role);
}
