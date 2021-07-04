package com.lhind.tripapp.controller;

import com.lhind.tripapp.dto.entityDTO.*;
import com.lhind.tripapp.dto.pagination.PagedResponse;
import com.lhind.tripapp.dto.pagination.SearchRequest;
import com.lhind.tripapp.dto.payload.MessageResponse;
import com.lhind.tripapp.dto.payload.TripSuccessResponse;
import com.lhind.tripapp.model.Trip;
import com.lhind.tripapp.service.TripService;
import com.lhind.tripapp.util.DTO;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

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
    public PagedResponse<TripDTO> getTrips(@Valid SearchRequest request) {
        return this.tripService.findAllByUser(request);
    }

    @PostMapping("/add-trip")
    @PreAuthorize("hasRole('USER')")
    @ApiImplicitParams({ @ApiImplicitParam(name = "trip",
            value = "The body of the trip that will be created", paramType = "body",
            dataType = "com.lhind.tripapp.dto.entityDTO.TripCreationDTO") })
    public ResponseEntity<TripDTO> postAddTripToUser(@ApiIgnore @DTO(TripCreationDTO.class) Trip trip) {
        TripDTO newTrip = this.tripService.addTrip(trip);
        return ResponseEntity.ok(newTrip);
    }

    /**
     * Update a trip by providing the trip you want to update with the ID
     * @param trip
     * @return
     */
    @PostMapping("/update-trip")
    @PreAuthorize("hasRole('USER')")
    @ApiImplicitParams({ @ApiImplicitParam(name = "trip",
            value = "Object containing id of trip to be updated and the values of the fields that will be updated",
            paramType = "body", dataType = "com.lhind.tripapp.dto.entityDTO.TripUpdateDTO") })
    public ResponseEntity<TripDTO> postUpdateTrip(@ApiIgnore @DTO(TripUpdateDTO.class) Trip trip) {
        TripDTO updatedTrip = this.tripService.saveTrip(trip);
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
    public List<TripDTO> getAllTripsByUserId(@PathVariable @Min(1) Long id) {
        return this.tripService.findAllTripsByUser(id);
    }

    @GetMapping("/{tripId}")
    @PreAuthorize("hasRole('USER')")
    public TripDTO getTripById(@PathVariable Long tripId) {
        return this.tripService.findById(tripId);
    }

    @PostMapping("/requestApproval")
    @PreAuthorize("hasRole('USER')")
    @ApiImplicitParams({ @ApiImplicitParam(name = "requestingApprovalTrip",
            value = "Object containing id of the trip whose status will be updated", paramType = "body", dataType = "com.lhind.tripapp.dto.entityDTO.TripIdDTO") })
    public ResponseEntity<MessageResponse> requestApproval(@ApiIgnore @DTO(TripIdDTO.class)  Trip requestingApprovalTrip) {
        this.tripService.requestApproval(requestingApprovalTrip);
        return ResponseEntity.ok().body(new MessageResponse("Trip status has been changed to WAITING_FOR_APPROVAL!"));
    }
}
