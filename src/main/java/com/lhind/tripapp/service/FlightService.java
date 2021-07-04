package com.lhind.tripapp.service;

import com.lhind.tripapp.dto.entityDTO.FlightDTO;
import com.lhind.tripapp.dto.pagination.PagedResponse;
import com.lhind.tripapp.dto.pagination.SearchRequest;
import com.lhind.tripapp.model.Flight;

public interface FlightService {
    PagedResponse<FlightDTO> getAllByTrip(Long tripId, SearchRequest request);

    FlightDTO saveFlight(Flight flight, Long tripId);
}
