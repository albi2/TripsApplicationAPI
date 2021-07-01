package com.lhind.tripapp.converter;

import com.lhind.tripapp.dto.entityDTO.UserDetailsImpl;
import com.lhind.tripapp.model.ERole;
import com.lhind.tripapp.model.Role;
import com.lhind.tripapp.model.User;
import com.lhind.tripapp.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.ConditionalOnDefaultWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserDetailsConverter {

    private RoleRepository roleRepository;

    @Autowired
    public UserDetailsConverter(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public UserDetailsImpl toDto(User user) {
        // Needs to be collection of granted authorities because its stored
        // inside security context as the User interface
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());

        return new UserDetailsImpl(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                authorities);
    }

    public User fromDto(UserDetailsImpl userDetails) {
        Set<Role> roles = userDetails.getAuthorities().stream().map(
                role -> {
                    Optional<Role> existingRole =
                            this.roleRepository.findByName(ERole.valueOf(role.getAuthority()));
                    return existingRole.get();
                }
        ).collect(Collectors.toSet());

        User user = new User();
        user.setUsername(userDetails.getUsername());
        user.setEmail(userDetails.getEmail());
        user.setPassword(userDetails.getPassword());
        user.setRoles(roles);
        return user;
    }
}
