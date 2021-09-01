package com.lhind.tripapp.service;

import com.lhind.tripapp.converter.SearchToPageConverter;
import com.lhind.tripapp.dto.entityDTO.TripDTO;
import com.lhind.tripapp.dto.entityDTO.TripDeletionDTO;
import com.lhind.tripapp.dto.entityDTO.UserDetailsImpl;
import com.lhind.tripapp.dto.pagination.PagedResponse;
import com.lhind.tripapp.dto.pagination.SearchRequest;
import com.lhind.tripapp.exception.EntityNotFoundException;
import com.lhind.tripapp.model.*;
import com.lhind.tripapp.repository.TripRepository;
import com.lhind.tripapp.repository.UserRepository;
import com.lhind.tripapp.service.impl.TripServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(MockitoJUnitRunner.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class TripServiceTest {
    @Mock
    private TripRepository tripRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SearchToPageConverter converter;

    @Mock
    private ModelMapper mapper;

    @Captor
    private ArgumentCaptor<Trip> tripArgumentCaptor;

    // Creates instance of trip services and injects all the mocks into the created one
    private TripService tripService;

    @BeforeAll
    void setSecurityContext() {
        // Prepare spring security context with a principal
        UserDetailsImpl applicationUser = new UserDetailsImpl(1l, "albi","albitaulla@yahoo.com","frenkli1", null);
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(applicationUser);
    }

    @BeforeEach
    void setUp() {
       tripService = new TripServiceImpl(tripRepository, userRepository, converter, mapper);
    }

    @Test
    @DisplayName("Test should pass when we are able to uccessfully saving user")
    void saveTripSuccessfulTest() {
        User currentUser = new User(1l,"albi","albitaulla@yahoo.com","frenkli1",null, new ArrayList<>());
        Trip newTrip = new Trip(1l, ETripReason.EVENT,"Fun","Albania","Miami", LocalDateTime.of(2020,7,28,6,30,50),
                LocalDateTime.of(2020,7,28,7,30,50), ETripStatus.CREATED,null,null);

        Mockito.when(tripRepository.save(newTrip)).thenReturn(newTrip);

        tripService.saveTrip(newTrip);

        Mockito.verify(tripRepository, Mockito.times(1)).save(tripArgumentCaptor.capture());

        assertEquals(tripArgumentCaptor.getValue().getId(),1L);
        assertEquals(tripArgumentCaptor.getValue().getStatus(),ETripStatus.CREATED);
    }

    @Test
    @DisplayName("Case when trip is found and deleted")
    void deleteTripSuccessfully() {
        TripDeletionDTO toDeleteTrip = new TripDeletionDTO();
        toDeleteTrip.setTripId(1L);

        Trip trip = new Trip(1l, ETripReason.EVENT,"Fun","Albania","Miami", LocalDateTime.of(2020,7,28,6,30,50),
                LocalDateTime.of(2020,7,28,7,30,50), ETripStatus.CREATED,null,null);

        List<Flight> flights = new ArrayList<Flight>();
        flights.add(new Flight(1L,"Tirana", "Mumbai", LocalDateTime.of(2020,7,28,7,30,50),
                LocalDateTime.of(2020,7,29,7,30,50),LocalDateTime.of(2020,7,28,6,30,50),
                2344.34,EFlightClass.FIRST,trip));
        trip.setFlights(flights);

        Mockito.when(this.tripRepository.existsById(1l)).thenReturn(true);
        Mockito.when(this.tripRepository.findById(1L)).thenReturn(Optional.of(trip));

        this.tripService.deleteTrip(toDeleteTrip);

        assertNull(flights.get(0).getTrip());
        assertNull(trip.getFlights().get(0).getTrip());
    }

    @Test
    @DisplayName("Delete trip with id not found")
    void deleteTripNotFoundExceptionThrown() {
        TripDeletionDTO toDeleteTrip = new TripDeletionDTO();
        toDeleteTrip.setTripId(1L);

        Mockito.when(this.tripRepository.existsById(1l)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> {
           this.tripService.deleteTrip(toDeleteTrip);
        });
    }

    @Test
    @DisplayName("Should find trip by id successfully")
    void findById() {
        Trip trip = new Trip(1l, ETripReason.EVENT,"Fun","Albania","Miami", LocalDateTime.of(2020,7,28,6,30,50),
                LocalDateTime.of(2020,7,28,7,30,50), ETripStatus.CREATED,null,null);
        TripDTO dto = mapper.map(trip, TripDTO.class);
        Mockito.when(tripRepository.findById(1l)).thenReturn(Optional.of(trip));
//        Mockito.when(mapper.map(Mockito.any(Trip.class), TripDTO.class)).thenReturn(dto);
        TripDTO actualTripDTO = tripService.findById(1l);
        assertEquals(actualTripDTO, dto);
    }

    @Test
    @DisplayName("Test should throw exception due to nonexisting id")
    void findByIdThrowsException() {
        Mockito.when(tripRepository.findById(1l)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            this.tripService.findById(1l);
        });
    }

    @Test
    @DisplayName("Find all trips for the currently logged in user")
    void findAllByUser() {
        SearchRequest request = new SearchRequest();
        request.setPage(1);
        request.setSize(1);
        SearchToPageConverter pageConverter = new SearchToPageConverter();
        PageRequest pageRequest = pageConverter.toPageRequest(request);

        List<Trip> list = new ArrayList<Trip>();
        list.add(new Trip(1l, ETripReason.EVENT,"Fun","Albania","Miami", LocalDateTime.of(2020,7,28,6,30,50),
                LocalDateTime.of(2020,7,28,7,30,50), ETripStatus.CREATED,null,null));

        List<TripDTO> dtoList = new ArrayList<>();
        dtoList.add(new TripDTO(1l, "EVENT","Fun","Albania","Miami", LocalDateTime.of(2020,7,28,6,30,50),
                LocalDateTime.of(2020,7,28,7,30,50), "CREATED"));

        Page<Trip> page = new PageImpl<>(list);
        User user = new User(1l,"albi","albitaulla@yahoo.com","frenkli1",null, null);

        Mockito.when(converter.toPageRequest(request)).thenReturn(pageRequest);
        Mockito.when(userRepository.findById(1l)).thenReturn(Optional.of(user));
        Mockito.when(tripRepository.findAllByUser(user, pageRequest)).thenReturn(page);
        Mockito.when(mapper.map(Mockito.any(), Mockito.eq(TripDTO.class))).thenReturn(new TripDTO(1l, "EVENT","Fun","Albania","Miami", LocalDateTime.of(2020,7,28,6,30,50),
                LocalDateTime.of(2020,7,28,7,30,50), "CREATED"));
        PagedResponse<TripDTO> expected = new PagedResponse<TripDTO>(dtoList, 1 ,1);
        PagedResponse<TripDTO> result = tripService.findAllByUser(request);

        assertEquals(expected.getContent(), result.getContent());
        assertEquals(expected.getCount(), result.getCount());
        assertEquals(expected.getTotalCount(), result.getTotalCount());
    }


    @Test
    void addTrip() {
        User currentUser = new User(1l,"albi","albitaulla@yahoo.com","frenkli1",null, new ArrayList<>());
        Trip newTrip = new Trip(1l, ETripReason.EVENT,"Fun","Albania","Miami", LocalDateTime.of(2020,7,28,6,30,50),
                LocalDateTime.of(2020,7,28,7,30,50), ETripStatus.CREATED,null,null);

        Mockito.when(userRepository.findById(1l)).thenReturn(Optional.of(currentUser));
        Mockito.when(userRepository.save(currentUser)).thenReturn(currentUser);
        Mockito.when(tripRepository.save(newTrip)).thenReturn(newTrip);

        tripService.addTrip(newTrip);

        Mockito.verify(tripRepository, Mockito.times(1)).save(tripArgumentCaptor.capture());

        assertEquals(tripArgumentCaptor.getValue().getId(),1L);
        assertEquals(tripArgumentCaptor.getValue().getStatus(),ETripStatus.CREATED);
    }

    @Test
    void findAllTripsByUser() {
        List<Trip> list = new ArrayList<Trip>();
        list.add(new Trip(1l, ETripReason.EVENT,"Fun","Albania","Miami", LocalDateTime.of(2020,7,28,6,30,50),
                LocalDateTime.of(2020,7,28,7,30,50), ETripStatus.CREATED,null,null));

        List<TripDTO> dtoList = list.stream()
                .map(el -> {
                    return mapper.map(el, TripDTO.class);
                }).collect(Collectors.toList());

        Mockito.when(tripRepository.findTripsByUserId(1l)).thenReturn(list);
        List<TripDTO> result = this.tripService.findAllTripsByUser(1l);
        assertEquals(dtoList, result);
    }

    @Test
    void requestApproval() {
        Trip requestingApprovalTrip = new Trip(1l, ETripReason.EVENT,"Fun","Albania","Miami", LocalDateTime.of(2020,7,28,6,30,50),
                LocalDateTime.of(2020,7,28,7,30,50), ETripStatus.CREATED,null,null);

        this.tripService.requestApproval(requestingApprovalTrip);
        // Verify that when the upcoming method is called one time - must be called after the method we check is called
        Mockito.verify(this.tripRepository,Mockito.times(1)).save(tripArgumentCaptor.capture());

        assertEquals(tripArgumentCaptor.getValue().getStatus(), ETripStatus.WAITING_FOR_APPROVAL);
    }
}