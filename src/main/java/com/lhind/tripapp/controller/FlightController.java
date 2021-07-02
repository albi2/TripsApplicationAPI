package com.lhind.tripapp.controller;

import com.lhind.tripapp.dto.entityDTO.FlightCreationDTO;
import com.lhind.tripapp.dto.pagination.PagedResponse;
import com.lhind.tripapp.dto.pagination.SearchRequest;
import com.lhind.tripapp.model.Flight;
import com.lhind.tripapp.service.FlightService;
import com.lhind.tripapp.util.DTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/flight")
public class FlightController {

    private FlightService flightService;

    @Autowired
    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @GetMapping("/{tripId}")
    @PreAuthorize("hasRole('USER')")
    public PagedResponse<Flight> getFlightsByTripId(@PathVariable Long tripId,
                                                          @Valid SearchRequest request) {
        return this.flightService.getAllByTrip(tripId,request);
    }

    @PostMapping("/add-flight/{tripId}")
    @PreAuthorize("hasRole('USER')")
    public Flight addFlight(@PathVariable Long tripId, @DTO(FlightCreationDTO.class) Flight flight) {
        return this.flightService.saveFlight(flight, tripId);
    }
}
