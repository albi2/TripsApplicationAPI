package com.lhind.tripapp.service;

import com.lhind.tripapp.dto.entityDTO.UserDTO;
import com.lhind.tripapp.dto.payload.*;
import com.lhind.tripapp.model.User;

public interface AuthenticationService {
    JwtResponse authenticateUser(LoginRequest loginRequest);

    TokenRefreshResponse getRefreshedToken(TokenRefreshRequest request);

    UserDTO signupUser(User request);
}
