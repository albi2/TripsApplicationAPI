package com.lhind.tripapp.service.impl;

import com.lhind.tripapp.converter.SearchToPageConverter;
import com.lhind.tripapp.dto.pagination.PagedResponse;
import com.lhind.tripapp.dto.pagination.SearchRequest;
import com.lhind.tripapp.model.Flight;
import com.lhind.tripapp.model.Trip;
import com.lhind.tripapp.repository.FlightRepository;
import com.lhind.tripapp.repository.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FlightServiceImpl implements com.lhind.tripapp.service.FlightService {
    private TripRepository tripRepository;
    private FlightRepository flightRepository;
    private SearchToPageConverter pageConverter;

    @Autowired
    public FlightServiceImpl(TripRepository tripRepository,
                             FlightRepository flightRepository,
                             SearchToPageConverter pageConverter) {
        this.tripRepository = tripRepository;
        this.flightRepository = flightRepository;
        this.pageConverter = pageConverter;
    }
    @Override
    public PagedResponse<Flight> getAllByTrip(Long tripId, SearchRequest request) {
        if(!this.tripRepository.existsById(tripId)) {
            // throw trip not found exception
        }

        Page<Flight> flightsPage= this.flightRepository.findAllByTrip(this.tripRepository.findById(tripId).get(),
                this.pageConverter.toPageRequest(request));

        List<Flight> flights = flightsPage.getContent();
        return new PagedResponse<Flight>(flights, flightsPage.getSize(), flightsPage.getTotalElements());
    }

    @Override
    public Flight saveFlight(Flight flight, Long tripId) {
        Optional<Trip> trip = this.tripRepository.findById(tripId);
        if(!trip.isPresent()) {
            // Throw trip not found exception
        }

        Trip currentTrip = trip.get();
        currentTrip.addFlight(flight);
        flight.setTrip(currentTrip);
        this.tripRepository.save(currentTrip);
        return this.flightRepository.save(flight);
    }
}
