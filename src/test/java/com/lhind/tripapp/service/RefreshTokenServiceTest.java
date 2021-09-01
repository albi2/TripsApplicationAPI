package com.lhind.tripapp.service;

import com.lhind.tripapp.exception.EntityNotFoundException;
import com.lhind.tripapp.exception.RefreshTokenException;
import com.lhind.tripapp.model.RefreshToken;
import com.lhind.tripapp.model.User;
import com.lhind.tripapp.repository.RefreshTokenRepository;
import com.lhind.tripapp.repository.UserRepository;
import com.lhind.tripapp.service.impl.RefreshTokenServiceImpl;
import net.bytebuddy.asm.Advice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(MockitoJUnitRunner.class)
@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository tokenRepository;

    @Mock
    private UserRepository userRepository;

    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor;
    @Captor
    private ArgumentCaptor<RefreshToken> refreshTokenArgumentCaptor;
    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;

    private RefreshTokenService refreshTokenService;

    @BeforeEach
    void setUp() {
        refreshTokenService = new RefreshTokenServiceImpl(tokenRepository, userRepository);
    }

    @Test
    @DisplayName("Find refresh token using the string successfully")
    void findByToken() {
        RefreshToken token = new RefreshToken(1l, null, "213123123", Instant.now());

        this.refreshTokenService.findByToken("213123123");

        Mockito.verify(tokenRepository, Mockito.times(1)).findByToken(stringArgumentCaptor.capture());

        assertEquals(stringArgumentCaptor.getValue(), "213123123");
    }

    @Test
    @DisplayName("Create a refresh token successfully")
    void createRefreshToken() {
        User currentUser = new User(1l,"albi","albitaulla@yahoo.com","frenkli1",null, new ArrayList<>());

        Mockito.when(userRepository.findById(1l)).thenReturn(Optional.of(currentUser));

        ReflectionTestUtils.setField(refreshTokenService, "refreshTokenDuration", 123456789l);

        this.refreshTokenService.createRefreshToken(1l);

        Mockito.verify(tokenRepository, Mockito.times(1)).save(refreshTokenArgumentCaptor.capture());

        assertEquals(currentUser, refreshTokenArgumentCaptor.getValue().getUser());
    }

    @Test
    @DisplayName("Create a refresh token method when user does not exist")
    void createRefreshTokenThrowsException(){
        Mockito.when(userRepository.findById(1l)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            this.refreshTokenService.createRefreshToken(1l);
        });
    }

    @Test
    @DisplayName("Verify expiration token not expired")
    void verifyExpiration() {
        RefreshToken token = new RefreshToken(1l, null, "213123123", Instant.now().plusMillis(112313123));
        RefreshToken result = this.refreshTokenService.verifyExpiration(token);

        assertEquals(token, result);
    }

    @Test
    @DisplayName("Verify expiration token expired")
    void verifyExpirationThrowsException() {
        // We know the token is expired
        RefreshToken token = new RefreshToken(1l, null, "213123123", Instant.now().minusMillis(2234234));

        assertThrows(RefreshTokenException.class, () -> {
            this.refreshTokenService.verifyExpiration(token);
        });
    }

    @Test
    @DisplayName("Delete token by user successfully")
    void deleteByUser() {
        User currentUser = new User(1l,"albi","albitaulla@yahoo.com","frenkli1",null, new ArrayList<>());
        Mockito.when(userRepository.findById(1l)).thenReturn(Optional.of(currentUser));

        this.refreshTokenService.deleteByUser(1l);

        Mockito.verify(this.tokenRepository, Mockito.times(1)).deleteByUser(userArgumentCaptor.capture());

        assertEquals(currentUser, userArgumentCaptor.getValue());
    }

    @Test
    @DisplayName("Delete token by user throws exception")
    void deleteByUserThrowsException() {
        Mockito.when(userRepository.findById(1l)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->{
            this.refreshTokenService.deleteByUser(1l);
        });
    }

}