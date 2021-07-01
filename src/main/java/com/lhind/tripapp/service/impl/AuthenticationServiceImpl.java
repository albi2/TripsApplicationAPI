package com.lhind.tripapp.service.impl;

import com.lhind.tripapp.converter.UserDTOConverter;
import com.lhind.tripapp.dto.entityDTO.UserDTO;
import com.lhind.tripapp.dto.entityDTO.UserDetailsImpl;
import com.lhind.tripapp.dto.payload.*;
import com.lhind.tripapp.exception.RefreshTokenException;
import com.lhind.tripapp.exception.RoleNotFoundException;
import com.lhind.tripapp.exception.UserExistsException;
import com.lhind.tripapp.model.ERole;
import com.lhind.tripapp.model.RefreshToken;
import com.lhind.tripapp.model.Role;
import com.lhind.tripapp.model.User;
import com.lhind.tripapp.repository.RoleRepository;
import com.lhind.tripapp.repository.UserRepository;
import com.lhind.tripapp.service.RefreshTokenService;
import com.lhind.tripapp.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthenticationServiceImpl implements com.lhind.tripapp.service.AuthenticationService {

    private RefreshTokenService refreshTokenService;
    private AuthenticationManager authenticationManager;
    private JwtUtils jwtUtils;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder encoder;
    private UserDTOConverter userConverter;

    @Autowired
    public AuthenticationServiceImpl(RefreshTokenService refreshTokenService,
                                     AuthenticationManager authenticationManager,
                                    JwtUtils jwtUtils, UserRepository userRepository,
                                     RoleRepository roleRepository,PasswordEncoder encoder,
                                     UserDTOConverter userConverter) {
        this.refreshTokenService = refreshTokenService;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userConverter = userConverter;
        this.encoder = encoder;
    }
    @Override
    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        // Create authentication object for spring security from given user
        // Throws unauthorized error in case of inexistent user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        // vendos kontekst autentikimi per spring security, vazhdon me pas permes service
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // Generate token for user based on authentication context
        String jwt = jwtUtils.generateJwtToken(authentication);

        // Get authenticated user and his roles, this is done through the service by the authentication
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        // Generate refresh token
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        return new JwtResponse(jwt,
                refreshToken.getRefreshToken(),
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles);
    }

    @Override
    public TokenRefreshResponse getRefreshedToken(TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();
        // Mapping the optional refresh token
        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshToken -> refreshTokenService.verifyExpiration(refreshToken))
                .map(refreshToken -> refreshToken.getUser())
                .map(user -> {
                    String jwtToken = jwtUtils.generateTokenFromUsername(user.getUsername());
                    return new TokenRefreshResponse(jwtToken, requestRefreshToken);
                })
                .orElseThrow(() -> new RefreshTokenException(requestRefreshToken,"Refresh token not in database!"));

    }

    @Override
    public UserDTO signupUser(User request) {
        if(this.userRepository.existsByUsername(request.getUsername())) {
            throw new UserExistsException("User with this username already exists!");
        }
        else if(this.userRepository.existsByEmail(request.getEmail())) {
            throw new UserExistsException("User with this email already exists!");
        }

        request.setPassword(encoder.encode(request.getPassword()));
        if(request.getRoles() == null) {
            request.setRoles(new HashSet<Role>());
            Role userRole = this.roleRepository.findByName(ERole.ROLE_USER).get();
            request.addRole(userRole);
        }

        return this.userConverter.toDto(this.userRepository.save(request));
    }

}
