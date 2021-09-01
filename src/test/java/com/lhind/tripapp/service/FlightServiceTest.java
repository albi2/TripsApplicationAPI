package com.lhind.tripapp.service;

import com.lhind.tripapp.converter.SearchToPageConverter;
import com.lhind.tripapp.dto.entityDTO.FlightDTO;
import com.lhind.tripapp.dto.entityDTO.TripDTO;
import com.lhind.tripapp.dto.pagination.PagedResponse;
import com.lhind.tripapp.dto.pagination.SearchRequest;
import com.lhind.tripapp.exception.EntityNotFoundException;
import com.lhind.tripapp.model.*;
import com.lhind.tripapp.repository.FlightRepository;
import com.lhind.tripapp.repository.TripRepository;
import com.lhind.tripapp.service.impl.FlightServiceImpl;
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
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(MockitoJUnitRunner.class)
@ExtendWith(MockitoExtension.class)
class FlightServiceTest {

    @Mock
    private TripRepository tripRepository;

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private SearchToPageConverter pageConverter;

    @Mock
    private ModelMapper mapper;

    @Captor
    private ArgumentCaptor<Trip> tripArgumentCaptor;

    private FlightService flightService;

    @BeforeEach
    void setUp() {
        flightService = new FlightServiceImpl(tripRepository, flightRepository, pageConverter, mapper);
    }

    @Test
    @DisplayName("Get all trips successfully")
    void getAllByTrip() {
        final Long TRIPID = 1l;
        SearchRequest request = new SearchRequest();
        request.setPage(1);
        request.setSize(1);

//        Trip trip = new Trip(1l, ETripReason.EVENT,"Fun","Albania","Miami", LocalDateTime.of(2020,7,28,6,30,50),
//                LocalDateTime.of(2020,7,28,7,30,50), ETripStatus.CREATED,null,null);
        Trip trip = Mockito.mock(Trip.class);
        Flight flight = new Flight(1l, "Tirana", "New York", LocalDateTime.of(2020,7,28,6,30,50),
                LocalDateTime.of(2020,7,29,7,30,50),LocalDateTime.of(2020, 7, 28,5,30,50),
                2340.34,EFlightClass.FIRST, trip);
        FlightDTO flightDTO = new FlightDTO(1l, "Tirana", "New York", LocalDateTime.of(2020,7,28,6,30,50),
                LocalDateTime.of(2020,7,29,7,30,50),LocalDateTime.of(2020, 7, 28,5,30,50),
                2340.34,"FIRST");

        ArrayList<Flight> flights = new ArrayList<Flight>();
        flights.add(flight);

        ArrayList<FlightDTO> flightDTOS = new ArrayList<FlightDTO>();
        flightDTOS.add(flightDTO);

        Page<Flight> flightPage = new PageImpl<>(flights);

        SearchToPageConverter converter = new SearchToPageConverter();
        PageRequest pageRequest = converter.toPageRequest(request);

        Mockito.when(tripRepository.findById(TRIPID)).thenReturn(Optional.of(trip));
        Mockito.when(pageConverter.toPageRequest(request)).thenReturn(pageRequest);
        Mockito.when(flightRepository.findAllByTrip(trip, pageRequest)).thenReturn(flightPage);
        Mockito.when(mapper.map(Mockito.any(), Mockito.eq(FlightDTO.class))).thenReturn(flightDTO);

        PagedResponse<FlightDTO> expected = new PagedResponse<FlightDTO>(flightDTOS, 1 ,1);
        PagedResponse<FlightDTO> result = this.flightService.getAllByTrip(TRIPID, request);

        assertEquals(expected.getContent(), result.getContent());
    }

    @DisplayName("Get all flights by trip throws exception")
    @Test
    void getAllByTripThrowsException() {
        final Long TRIPID = 1l;
        SearchRequest request = new SearchRequest();
        request.setPage(1);
        request.setSize(1);

        Mockito.when(tripRepository.findById(TRIPID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            this.flightService.getAllByTrip(TRIPID, request);
        });
    }


    @Test
    @DisplayName("Save flight successfully")
    void saveFlight() {
        Trip trip = new Trip(1l, ETripReason.EVENT,"Fun","Albania","Miami", LocalDateTime.of(2020,7,28,6,30,50),
                LocalDateTime.of(2020,7,28,7,30,50), ETripStatus.CREATED,null,new ArrayList<Flight>());
        Flight flight = new Flight(1l, "Tirana", "New York", LocalDateTime.of(2020,7,28,6,30,50),
                LocalDateTime.of(2020,7,29,7,30,50),LocalDateTime.of(2020, 7, 28,5,30,50),
                2340.34,EFlightClass.FIRST, null);

        Mockito.when(tripRepository.findById(1l)).thenReturn(Optional.of(trip));

        this.flightService.saveFlight(flight, trip.getId());

        Mockito.verify(tripRepository, Mockito.times(1)).save(tripArgumentCaptor.capture());

        assertEquals(trip, tripArgumentCaptor.getValue());
    }

    @DisplayName("Save flight throw exception")
    @Test
    void saveFlightThrowException() {
        Mockito.when(tripRepository.findById(1l)).thenReturn(Optional.empty());
        Flight flight = Mockito.mock(Flight.class);

        assertThrows(EntityNotFoundException.class, () -> {
            this.flightService.saveFlight(flight, 1l);
        });
    }
}