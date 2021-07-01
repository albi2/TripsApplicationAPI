package com.lhind.tripapp.repository;

import com.lhind.tripapp.dto.entityDTO.UserDTO;
import com.lhind.tripapp.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);

    @Query("SELECT U FROM User U" +
            " WHERE 'ROLE_ADMIN' NOT IN (SELECT R.name FROM U.roles R)")
    Page<User> getAllUserRole(Pageable pageable);
}
