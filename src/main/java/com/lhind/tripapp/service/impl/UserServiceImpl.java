package com.lhind.tripapp.service.impl;

import com.lhind.tripapp.converter.SearchToPageConverter;
import com.lhind.tripapp.converter.TripCreationConverter;
import com.lhind.tripapp.converter.UserDTOConverter;
import com.lhind.tripapp.dto.entityDTO.TripCreationDTO;
import com.lhind.tripapp.dto.entityDTO.UserDTO;
import com.lhind.tripapp.dto.pagination.PagedResponse;
import com.lhind.tripapp.dto.pagination.SearchRequest;
import com.lhind.tripapp.dto.payload.JwtResponse;
import com.lhind.tripapp.dto.payload.LoginRequest;
import com.lhind.tripapp.exception.EntityNotFoundException;
import com.lhind.tripapp.model.Trip;
import com.lhind.tripapp.model.User;
import com.lhind.tripapp.dto.entityDTO.UserDetailsImpl;
import com.lhind.tripapp.repository.TripRepository;
import com.lhind.tripapp.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements com.lhind.tripapp.service.UserService {
    private UserRepository userRepository;
    private TripRepository tripRepository;
    private UserDTOConverter dtoConverter;
    private SearchToPageConverter pageConverter;
    private static final Logger logger = LogManager.getLogger();

    @Autowired
    public UserServiceImpl(UserRepository userRepository, TripRepository tripRepository,
                           UserDTOConverter dtoConverter, SearchToPageConverter pageConverter) {
        this.userRepository = userRepository;
        this.tripRepository = tripRepository;
        this.dtoConverter = dtoConverter;
        this.pageConverter = pageConverter;
    }

    @Override
    public User saveUser(User user) {
        return this.userRepository.save(user);
    }

    @Override
    public UserDTO findById(Long id) {

        return dtoConverter.toDto(this.userRepository.findById(id).orElseThrow(
                () -> {
                    logger.error("Could not find user with id: " + id );
                    return new EntityNotFoundException("Could not find user with the provided id!");
                }
        ));
    }

    @Override
    public PagedResponse<UserDTO> getUsers(SearchRequest request) {
        Page<User> page = this.userRepository.getAllUserRole(this.pageConverter.toPageRequest(request));
        List<UserDTO> users = page.getContent().stream().map(user -> dtoConverter.toDto(user)).collect(Collectors.toList());
        return new PagedResponse<UserDTO>(users, page.getSize(),page.getTotalElements());
    }
}
