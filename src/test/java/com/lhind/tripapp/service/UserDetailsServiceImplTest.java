package com.lhind.tripapp.service;

import com.lhind.tripapp.converter.UserDetailsConverter;
import com.lhind.tripapp.dto.entityDTO.UserDetailsImpl;
import com.lhind.tripapp.model.User;
import com.lhind.tripapp.repository.UserRepository;
import com.lhind.tripapp.service.impl.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(MockitoJUnitRunner.class)
@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserDetailsConverter userDetailsConverter;

    private UserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        userDetailsService = new UserDetailsServiceImpl(userRepository, userDetailsConverter);
    }

    @Test
    @DisplayName("Load user by username successfully")
    void loadUserByUsername() {
        String username  = "albi";
        User user = new User(1l,username,"albitaulla@yahoo.com","frenkli1",null, null);

        Mockito.when(userDetailsConverter.toDto(user)).thenReturn(new UserDetailsImpl(1l,"albi","albitaulla@yahoo.com","frenkli1", null));
        Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

        assertEquals(userDetails.getUsername(), username);
        assertEquals(userDetails.getPassword(), "frenkli1");
        assertNull(userDetails.getAuthorities());
    }

    @Test
    @DisplayName("Load user by username throw exception case")
    void loadUserByUsernameThrowException() {

    }
}