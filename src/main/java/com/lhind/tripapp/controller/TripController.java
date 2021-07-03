package com.lhind.tripapp.controller;

import com.lhind.tripapp.dto.entityDTO.TripCreationDTO;
import com.lhind.tripapp.dto.entityDTO.TripIdDTO;
import com.lhind.tripapp.dto.entityDTO.TripUpdateDTO;
import com.lhind.tripapp.dto.entityDTO.TripDeletionDTO;
import com.lhind.tripapp.dto.pagination.PagedResponse;
import com.lhind.tripapp.dto.pagination.SearchRequest;
import com.lhind.tripapp.dto.payload.MessageResponse;
import com.lhind.tripapp.dto.payload.TripSuccessResponse;
import com.lhind.tripapp.model.Trip;
import com.lhind.tripapp.service.TripService;
import com.lhind.tripapp.util.DTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/trip")
@Validated
public class TripController {

    private TripService tripService;

    @Autowired
    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    @GetMapping("")
    @PreAuthorize("hasRole('USER')")
    public PagedResponse<Trip> getTrips(@Valid SearchRequest request) {
        return this.tripService.findAllByUser(request);
    }

    @PostMapping("/add-trip")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Trip> postAddTripToUser(@DTO(TripCreationDTO.class) Trip trip) {
        Trip newTrip = this.tripService.addTrip(trip);
        return ResponseEntity.ok(newTrip);
    }

    @PostMapping("/update-trip")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Trip> postUpdateTrip(@DTO(TripUpdateDTO.class) Trip trip) {
        Trip updatedTrip = this.tripService.saveTrip(trip);
        return ResponseEntity.ok(updatedTrip);
    }

    @PostMapping("/delete-trip")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TripSuccessResponse> postDeleteTrip(@Valid @RequestBody TripDeletionDTO request) {
        this.tripService.deleteTrip(request);
        return ResponseEntity.ok(new TripSuccessResponse("Trip was deleted successfully!"));
    }

    // Get all trips from user id provided that have a status of WAITING_FOR_APPROVAL
    @GetMapping("/all-trips-by-user/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Trip> getAllTripsByUserId(@PathVariable @Min(1) Long id) {
        return this.tripService.findAllTripsByUser(id);
    }

    @GetMapping("/{tripId}")
    @PreAuthorize("hasRole('USER')")
    public Trip getTripById(@PathVariable Long tripId) {
        return this.tripService.findById(tripId);
    }

    @PostMapping("/requestApproval")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MessageResponse> requestApproval(@DTO(TripIdDTO.class) Trip requestingApprovalTrip) {
        this.tripService.requestApproval(requestingApprovalTrip);
        return ResponseEntity.ok().body(new MessageResponse("Trip status has been changed to WAITING_FOR_APPROVAL!"));
    }
}
