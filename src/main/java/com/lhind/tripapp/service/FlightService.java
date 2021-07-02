package com.lhind.tripapp.service;

import com.lhind.tripapp.dto.pagination.PagedResponse;
import com.lhind.tripapp.dto.pagination.SearchRequest;
import com.lhind.tripapp.model.Flight;

public interface FlightService {
    PagedResponse<Flight> getAllByTrip(Long tripId, SearchRequest request);

    Flight saveFlight(Flight flight, Long tripId);
}
