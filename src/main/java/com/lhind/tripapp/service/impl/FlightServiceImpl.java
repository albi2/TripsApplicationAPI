package com.lhind.tripapp.service.impl;

import com.lhind.tripapp.converter.SearchToPageConverter;
import com.lhind.tripapp.dto.entityDTO.FlightDTO;
import com.lhind.tripapp.dto.pagination.PagedResponse;
import com.lhind.tripapp.dto.pagination.SearchRequest;
import com.lhind.tripapp.exception.EntityNotFoundException;
import com.lhind.tripapp.model.Flight;
import com.lhind.tripapp.model.Trip;
import com.lhind.tripapp.repository.FlightRepository;
import com.lhind.tripapp.repository.TripRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FlightServiceImpl implements com.lhind.tripapp.service.FlightService {
    private TripRepository tripRepository;
    private FlightRepository flightRepository;
    private SearchToPageConverter pageConverter;
    private ModelMapper mapper;

    private static final Logger logger = LogManager.getLogger();

    @Autowired
    public FlightServiceImpl(TripRepository tripRepository,
                             FlightRepository flightRepository,
                             SearchToPageConverter pageConverter,
                             ModelMapper mapper) {
        this.tripRepository = tripRepository;
        this.flightRepository = flightRepository;
        this.pageConverter = pageConverter;
        this.mapper = mapper;
    }
    @Override
    public PagedResponse<FlightDTO> getAllByTrip(Long tripId, SearchRequest request) {

        Page<Flight> flightsPage= this.flightRepository.findAllByTrip(
                this.tripRepository.findById(tripId).orElseThrow(
                        () -> {
                            logger.error("Could find flights for the trip with id " + tripId + " because it does not exist!");
                            return new EntityNotFoundException("Trip with id " + tripId+ " could not be found!" +
                                    "We were not able to find any flights!");
                        }
                ),
                this.pageConverter.toPageRequest(request));

        List<FlightDTO> flights = flightsPage.getContent()
                .stream()
                .map(flight -> {
                    return this.mapper.map(flight, FlightDTO.class);
                })
                .collect(Collectors.toList());
        return new PagedResponse<FlightDTO>(flights, flightsPage.getSize(), flightsPage.getTotalElements());
    }

    @Override
    public FlightDTO saveFlight(Flight flight, Long tripId) {
        Trip currentTrip = this.tripRepository.findById(tripId).
                orElseThrow(() -> {
                    logger.error("Could not add flight to trip with id "+
                            tripId + " because trip does not exist!");
                    return new EntityNotFoundException("Could not find trip with give id to add flight to!");
                });

        currentTrip.addFlight(flight);
        flight.setTrip(currentTrip);
        this.tripRepository.save(currentTrip);
        return this.mapper.map(this.flightRepository.save(flight), FlightDTO.class);
    }
}
