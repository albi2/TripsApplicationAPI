package com.lhind.tripapp.controller;
import com.lhind.tripapp.dto.entityDTO.UserCreationDTO;
import com.lhind.tripapp.dto.entityDTO.UserDTO;
import com.lhind.tripapp.dto.payload.*;
import com.lhind.tripapp.model.User;
import com.lhind.tripapp.service.AuthenticationService;
import com.lhind.tripapp.util.DTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private AuthenticationService authenticationService;

    @Autowired
    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    // User signs in, gets provided with jwt token for access and refresh token for jwt
    // renewal in case of expiration
    @PostMapping("/signin")
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        JwtResponse jwtResponse = this.authenticationService.authenticateUser(loginRequest);
        return ResponseEntity.ok(jwtResponse);
    }

    // User tries to refresh the JWT token by providing refresh token
    @PostMapping("/refreshJwtToken")
    public ResponseEntity<TokenRefreshResponse> postRefreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        TokenRefreshResponse tokenRefreshResponse = this.authenticationService.getRefreshedToken(request);
        return ResponseEntity.ok(tokenRefreshResponse);
    }

    @PostMapping("/signup")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> registerUser(@DTO(UserCreationDTO.class) User request) {
        UserDTO newUser = this.authenticationService.signupUser(request);
        return ResponseEntity.ok().body(newUser);
    }
}
