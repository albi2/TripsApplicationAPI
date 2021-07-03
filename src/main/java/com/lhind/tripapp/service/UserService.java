package com.lhind.tripapp.service;

import com.lhind.tripapp.dto.entityDTO.UserDTO;
import com.lhind.tripapp.dto.pagination.PagedResponse;
import com.lhind.tripapp.dto.pagination.SearchRequest;
import com.lhind.tripapp.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User saveUser(User user);

    UserDTO findById(Long id);

    PagedResponse<UserDTO> getUsers(SearchRequest request);
}
