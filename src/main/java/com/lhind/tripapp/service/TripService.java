package com.lhind.tripapp.service;

import com.lhind.tripapp.dto.entityDTO.TripDTO;
import com.lhind.tripapp.dto.entityDTO.TripDeletionDTO;
import com.lhind.tripapp.dto.entityDTO.TripStatusUpdateDTO;
import com.lhind.tripapp.dto.pagination.PagedResponse;
import com.lhind.tripapp.dto.pagination.SearchRequest;
import com.lhind.tripapp.model.Trip;

import java.util.List;
import java.util.Optional;

public interface TripService {
    TripDTO saveTrip(Trip trip);

    void deleteTrip(TripDeletionDTO request);

    TripDTO findById(Long id);

    PagedResponse<TripDTO> findAllByUser(SearchRequest request);

    TripDTO addTrip(Trip trip);


    List<TripDTO> findAllTripsByUser(Long request);

    void requestApproval(Trip requestingApprovalTrip);
}
