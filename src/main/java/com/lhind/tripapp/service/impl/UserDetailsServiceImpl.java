package com.lhind.tripapp.service.impl;

import com.lhind.tripapp.converter.TripCreationConverter;
import com.lhind.tripapp.converter.UserDetailsConverter;
import com.lhind.tripapp.model.User;
import com.lhind.tripapp.dto.entityDTO.UserDetailsImpl;
import com.lhind.tripapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private UserRepository userRepository;
    private UserDetailsConverter userDetailsConverter;
    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository,
                                  UserDetailsConverter userDetailsConverter) {
        this.userRepository = userRepository;
        this.userDetailsConverter = userDetailsConverter;
    }

    // Service provided to spring security for authenticating user
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

        return this.userDetailsConverter.toDto(user);
    }
}
