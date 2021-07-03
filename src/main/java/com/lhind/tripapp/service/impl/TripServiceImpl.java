package com.lhind.tripapp.service.impl;

import com.lhind.tripapp.converter.SearchToPageConverter;
import com.lhind.tripapp.converter.TripCreationConverter;
import com.lhind.tripapp.dto.entityDTO.UserDetailsImpl;
import com.lhind.tripapp.dto.entityDTO.TripDeletionDTO;
import com.lhind.tripapp.dto.pagination.PagedResponse;
import com.lhind.tripapp.dto.pagination.SearchRequest;
import com.lhind.tripapp.exception.EntityNotFoundException;
import com.lhind.tripapp.model.ETripStatus;
import com.lhind.tripapp.model.Trip;
import com.lhind.tripapp.model.User;
import com.lhind.tripapp.repository.TripRepository;
import com.lhind.tripapp.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    private SearchToPageConverter pageConverter;
    private static final Logger logger = LogManager.getLogger();

    @Autowired
    public TripServiceImpl(TripRepository tripRepository,
                           UserRepository userRepository,
                           SearchToPageConverter pageConverter) {
        this.tripRepository = tripRepository;
        this.userRepository = userRepository;
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
        Trip trip = this.tripRepository.findById(id).orElseThrow(
                () -> {
                    logger.error("Could not find trip with id: " + id);
                    return new EntityNotFoundException("Could not find trip with the provided id!");
                }
        );
        return trip;
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
        User tripOwner = this.userRepository.findById(userDetails.getId())
                .orElseThrow(() -> {
                    logger.error("Trip could not be added because user is not logged in!");
                    return new EntityNotFoundException("Logged in user does not exist!");
                });

            tripOwner.addTrip(toBeAddedTrip);
            toBeAddedTrip.setUser(tripOwner);
            this.userRepository.save(tripOwner);
            return this.tripRepository.save(toBeAddedTrip);
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
            logger.error("Could not delete trip with id "+ request.getTripId() + " because it does not exist!");
            throw new EntityNotFoundException("Could not delete the trip because it does not exist!");
        }
    }
}
