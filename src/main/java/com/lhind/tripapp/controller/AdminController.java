package com.lhind.tripapp.controller;

import com.lhind.tripapp.dto.entityDTO.UserDTO;
import com.lhind.tripapp.dto.pagination.PagedResponse;
import com.lhind.tripapp.dto.pagination.SearchRequest;
import com.lhind.tripapp.dto.payload.TripStatusSuccessUpdate;
import com.lhind.tripapp.dto.entityDTO.TripStatusUpdateDTO;
import com.lhind.tripapp.model.Trip;
import com.lhind.tripapp.service.TripService;
import com.lhind.tripapp.service.UserService;
import com.lhind.tripapp.util.DTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private UserService userService;
    private TripService tripService;

    @Autowired
    public AdminController(UserService userService,
                           TripService tripService) {
        this.userService = userService;
        this.tripService = tripService;
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public PagedResponse<UserDTO> getUsers(@Valid SearchRequest request) {
        return this.userService.getUsers(request);
    }

    @PostMapping("/update-trip-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Trip> updateTripStatus(@DTO(TripStatusUpdateDTO.class) Trip trip) {
        Trip updatedTrip = this.tripService.saveTrip(trip);
        return ResponseEntity.ok().body(updatedTrip);
    }
}
