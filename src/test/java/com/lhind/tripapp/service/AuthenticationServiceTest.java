package com.lhind.tripapp.service;

import com.lhind.tripapp.converter.UserDTOConverter;
import com.lhind.tripapp.dto.entityDTO.UserDTO;
import com.lhind.tripapp.dto.entityDTO.UserDetailsImpl;
import com.lhind.tripapp.dto.payload.JwtResponse;
import com.lhind.tripapp.dto.payload.LoginRequest;
import com.lhind.tripapp.dto.payload.TokenRefreshRequest;
import com.lhind.tripapp.dto.payload.TokenRefreshResponse;
import com.lhind.tripapp.exception.RefreshTokenException;
import com.lhind.tripapp.exception.UserExistsException;
import com.lhind.tripapp.model.ERole;
import com.lhind.tripapp.model.RefreshToken;
import com.lhind.tripapp.model.Role;
import com.lhind.tripapp.model.User;
import com.lhind.tripapp.repository.RoleRepository;
import com.lhind.tripapp.repository.UserRepository;
import com.lhind.tripapp.service.impl.AuthenticationServiceImpl;
import com.lhind.tripapp.util.JwtUtils;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.data.cassandra.DataCassandraTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(MockitoJUnitRunner.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthenticationServiceTest {

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private UserDTOConverter userDTOConverter;

    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;

    private AuthenticationService authenticationService;

    @BeforeAll
    void setSecurityContext() {
        UserDetailsImpl applicationUser = new UserDetailsImpl(1l, "albi","albitaulla@yahoo.com","frenkli1", new ArrayList<GrantedAuthority>());
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(applicationUser);
    }

    @BeforeEach
    void setUp() {
        authenticationService = new AuthenticationServiceImpl(refreshTokenService,authenticationManager,
                jwtUtils,userRepository,roleRepository, encoder,userDTOConverter);
        // Prepare spring security context with a principal

    }

    @Test
    @DisplayName("Authenticate user with the login request by creating the security context and providing the token response")
    void authenticateUser() {
        LoginRequest request = new LoginRequest("albi", "frenkli1");
        UserDetailsImpl applicationUser = new UserDetailsImpl(1l, "albi","albitaulla@yahoo.com","frenkli1", new ArrayList<GrantedAuthority>());
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authenticationManager.authenticate(Mockito.any())).thenReturn(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn(applicationUser);

        int jwtExpirationMs = 86400;
        String jwtSecret = "mysecretkey";

        String jwtToken = Jwts.builder()
                .setSubject("albi")
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime()+ jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512,jwtSecret)
                .compact();

        Mockito.when(refreshTokenService.createRefreshToken(1l)).thenReturn(new RefreshToken(1l, null, "123123123", Instant.now().plusMillis(1213312312)));

        Mockito.when(jwtUtils.generateJwtToken(Mockito.any(Authentication.class))).thenReturn(jwtToken);

        JwtResponse jwtResponse = this.authenticationService.authenticateUser(request);
        assertEquals(jwtToken, jwtResponse.getAccessToken());
        assertEquals("albi", jwtResponse.getUsername());
        assertEquals("albitaulla@yahoo.com", jwtResponse.getEmail());
        assertEquals(1L, jwtResponse.getId());
    }

    @Test
    @DisplayName("Get refresh token successfully in case jwt token expires")
    void getRefreshedToken() {
        TokenRefreshRequest request = Mockito.mock(TokenRefreshRequest.class);
        User user = Mockito.mock(User.class);
        RefreshToken refreshToken = new RefreshToken(1l, user, "123123123", Instant.now().plusMillis(1213312312));

        int jwtExpirationMs = 86400;
        String jwtSecret = "mysecretkey";

        String jwtToken = Jwts.builder()
                .setSubject("albi")
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime()+ jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512,jwtSecret)
                .compact();

        Mockito.when(request.getRefreshToken()).thenReturn("123123123");
        Mockito.when(refreshTokenService.findByToken("123123123")).thenReturn(Optional.of(refreshToken));
        Mockito.when(refreshTokenService.verifyExpiration(refreshToken)).thenReturn(refreshToken);
        Mockito.when(jwtUtils.generateTokenFromUsername("albi")).thenReturn(jwtToken);
        Mockito.when(user.getUsername()).thenReturn("albi");

        TokenRefreshResponse result = this.authenticationService.getRefreshedToken(request);

        assertEquals(jwtToken, result.getAccessToken());
        assertEquals("123123123", result.getRefreshToken());

    }

    @Test
    @DisplayName("Get refresh token throw exception")
    void getRefreshTokenThrowException() {
        TokenRefreshRequest request = Mockito.mock(TokenRefreshRequest.class);
        Mockito.when(request.getRefreshToken()).thenReturn("123123123");

        Mockito.when(refreshTokenService.findByToken("123123123")).thenReturn(Optional.empty());

        assertThrows(RefreshTokenException.class, () -> {
           authenticationService.getRefreshedToken(request);
        });
    }

    @Test
    @DisplayName("Sign up user successfully")
    void signupUser() {
        User user = new User(1l, "albi", "albitaulla@yahoo.com", "frenkli1", null, null);
        Set<Role> rolesSet = new HashSet<Role>();
        rolesSet.add(new Role(1, ERole.ROLE_USER));
        User toBeSavedUser = new User(user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                rolesSet,
                null
                );

        List<String> roles = new ArrayList<String>();
        roles.add("ROLE_USER");

        UserDTO expected = new UserDTO(1l, "albi", "albitaulla@yahoo.com", roles);

        String encodedPassword = "A6B213C9D9E8E";
        Mockito.when(userRepository.existsByUsername(user.getUsername())).thenReturn(false);
        Mockito.when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        Mockito.when(encoder.encode(user.getPassword())).thenReturn(encodedPassword);
        Mockito.when(roleRepository.findByName(ERole.ROLE_USER)).thenReturn(Optional.of(new Role(1, ERole.ROLE_USER)));
        Mockito.when(userDTOConverter.toDto(Mockito.any(User.class))).thenReturn(expected);

        Mockito.when(userRepository.save(user)).thenReturn(toBeSavedUser);

        UserDTO dtoResult = this.authenticationService.signupUser(user);
        Mockito.verify(userRepository, Mockito.times(1)).save(userArgumentCaptor.capture());

        assertEquals(user, userArgumentCaptor.getValue());
        assertEquals(expected, dtoResult);
    }

    @Test
    @DisplayName("Sign up user throws exception user with username exists")
    void signupUserThrowsException() {
        User user = new User(1l, "albi", "albitaulla@yahoo.com", "frenkli1", null, null);
        Mockito.when(userRepository.existsByUsername(user.getUsername())).thenReturn(true);

        assertThrows(UserExistsException.class, () -> {
            this.authenticationService.signupUser(user);
        });
    }

    @Test
    @DisplayName("Sign up user throws exception user with email exists")
    void signupUserThrowsExceptionEmail() {
        User user = new User(1l, "albi", "albitaulla@yahoo.com", "frenkli1", null, null);
        Mockito.when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        assertThrows(UserExistsException.class, () -> {
            this.authenticationService.signupUser(user);
        });
    }
}