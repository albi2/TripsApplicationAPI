package com.lhind.tripapp.service;

import com.lhind.tripapp.dto.entityDTO.TripDeletionDTO;
import com.lhind.tripapp.dto.entityDTO.TripStatusUpdateDTO;
import com.lhind.tripapp.dto.pagination.PagedResponse;
import com.lhind.tripapp.dto.pagination.SearchRequest;
import com.lhind.tripapp.model.Trip;

import java.util.List;
import java.util.Optional;

public interface TripService {
    Trip saveTrip(Trip trip);

    void deleteTrip(TripDeletionDTO request);

    List<Trip> findAll();

    Trip findById(Long id);

    PagedResponse<Trip> findAllByUser(SearchRequest request);

    Trip addTrip(Trip trip);


    List<Trip> findAllTripsByUser(Long request);

    void requestApproval(Trip requestingApprovalTrip);
}
