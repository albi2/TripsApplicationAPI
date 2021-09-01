package com.lhind.tripapp.service;

import com.lhind.tripapp.converter.SearchToPageConverter;
import com.lhind.tripapp.converter.UserDTOConverter;
import com.lhind.tripapp.dto.entityDTO.UserDTO;
import com.lhind.tripapp.dto.pagination.PagedResponse;
import com.lhind.tripapp.dto.pagination.SearchRequest;
import com.lhind.tripapp.exception.EntityNotFoundException;
import com.lhind.tripapp.model.User;
import com.lhind.tripapp.repository.TripRepository;
import com.lhind.tripapp.repository.UserRepository;
import com.lhind.tripapp.service.impl.UserServiceImpl;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(MockitoJUnitRunner.class)
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private TripRepository tripRepository;
    @Mock
    private UserDTOConverter dtoConverter;
    @Mock
    private SearchToPageConverter pageConverter;

    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;

    private UserService userService;

    @BeforeEach
    void setUp() {
        this.userService = new UserServiceImpl(userRepository, tripRepository, dtoConverter, pageConverter);
    }

    @Test
    void saveUser() {
        User user = new User(1l,"albi","albitaulla@yahoo.com","frenkli1",null, null);
        this.userService.saveUser(user);

        Mockito.verify(this.userRepository, Mockito.times(1)).save(userArgumentCaptor.capture());

        assertEquals(userArgumentCaptor.getValue(), user);
    }

    @Test
    @DisplayName("Find user by id successfully")
    void findById() {
        User user = new User(1l,"albi","albitaulla@yahoo.com","frenkli1",null, null);
        UserDTO userDTO = new UserDTO(1l, "albi","albitaulla@yahoo.com", null);
        Mockito.when(this.userRepository.findById(1l)).thenReturn(Optional.of(user));
        Mockito.when(dtoConverter.toDto(Mockito.any(User.class))).thenReturn(userDTO);

        UserDTO actualResponse = this.userService.findById(1l);

        assertEquals(actualResponse, userDTO);
    }

    @Test
    @DisplayName("Find user by id throwing exception")
    void findByIdThrowException() {
        Mockito.when(this.userRepository.findById(0l)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> {
            this.userService.findById(0l);
        });
    }

    @Test
    @DisplayName("Get all users successfully")
    void getUsers() {
        SearchRequest request = new SearchRequest();
        request.setSize(1);
        request.setPage(1);
        UserDTO userDTO = new UserDTO(1l, "albi","albitaulla@yahoo.com", null);
        User user = new User(1l,"albi","albitaulla@yahoo.com","frenkli1",null, null);

        List<User> list = new ArrayList<User>();
        list.add(user);

        Page<User> page = new PageImpl<>(list);

        Mockito.when(userRepository.getAllUserRole(pageConverter.toPageRequest(request))).thenReturn(page);
        Mockito.when(dtoConverter.toDto(Mockito.any(User.class))).thenReturn(userDTO);

        PagedResponse<UserDTO> actualResponse = this.userService.getUsers(request);

        assertEquals(actualResponse.getContent().size(), 1);
    }
}