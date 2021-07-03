package com.lhind.tripapp.service.impl;

import com.lhind.tripapp.converter.SearchToPageConverter;
import com.lhind.tripapp.converter.TripCreationConverter;
import com.lhind.tripapp.dto.entityDTO.UserDetailsImpl;
import com.lhind.tripapp.dto.entityDTO.TripDeletionDTO;
import com.lhind.tripapp.dto.pagination.PagedResponse;
import com.lhind.tripapp.dto.pagination.SearchRequest;
import com.lhind.tripapp.model.ETripStatus;
import com.lhind.tripapp.model.Trip;
import com.lhind.tripapp.model.User;
import com.lhind.tripapp.repository.TripRepository;
import com.lhind.tripapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class TripServiceImpl implements com.lhind.tripapp.service.TripService {
    private TripRepository tripRepository;
    private UserRepository userRepository;
    private TripCreationConverter tripCreationConverter;
    private SearchToPageConverter pageConverter;

    @Autowired
    public TripServiceImpl(TripRepository tripRepository,
                           UserRepository userRepository,
                           TripCreationConverter tripCreationConverter,
                           SearchToPageConverter pageConverter) {
        this.tripRepository = tripRepository;
        this.userRepository = userRepository;
        this.tripCreationConverter = tripCreationConverter;
        this.pageConverter = pageConverter;
    }

    @Override
    public Trip saveTrip(Trip trip) {
        return this.tripRepository.save(trip);
    }

    @Override
    public List<Trip> findAll() {
        return this.tripRepository.findAll();
    }

    @Override
    public Trip findById(Long id) {
        Optional<Trip> trip = this.tripRepository.findById(id);
        if(!trip.isPresent()) {
            // throw trip not found exception
        }
        return trip.get();
    }

    @Override
    public PagedResponse<Trip> findAllByUser(SearchRequest request) {
        UserDetailsImpl userDetails = (UserDetailsImpl)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User loggedInUser = this.userRepository.findById(userDetails.getId()).get();


        Page<Trip> tripsPage =
                this.tripRepository.findAllByUser(loggedInUser,
                        this.pageConverter.toPageRequest(request));
        List<Trip> trips = tripsPage.getContent();

        return new PagedResponse<Trip>(trips, tripsPage.getSize(),tripsPage.getTotalElements());
    }

    @Override
    public Trip addTrip(Trip toBeAddedTrip) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> user = this.userRepository.findById(userDetails.getId());

        if(user.isPresent()) {
            User tripOwner = user.get();
            tripOwner.addTrip(toBeAddedTrip);
            toBeAddedTrip.setUser(tripOwner);
            // Update user, cascade to trip
            this.userRepository.save(tripOwner);
            return this.tripRepository.save(toBeAddedTrip);
        }
        else {
            // throw user not found exception
            return null;
        }

    }

    @Override
    public List<Trip> findAllTripsByUser(Long id) {
        return this.tripRepository.findTripsByUserId(id);
    }

    @Override
    public void requestApproval(Trip requestingApprovalTrip) {
        requestingApprovalTrip.setStatus(ETripStatus.WAITING_FOR_APPROVAL);
        this.saveTrip(requestingApprovalTrip);
    }


    @Override
    public void deleteTrip(TripDeletionDTO request) {
        if(this.tripRepository.existsById(request.getTripId())) {
            this.tripRepository.deleteById(request.getTripId());
        }
        else {
            // Throw trip not found exception
        }
    }
}
