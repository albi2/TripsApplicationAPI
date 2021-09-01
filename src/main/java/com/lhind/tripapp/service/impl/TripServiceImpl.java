package com.lhind.tripapp.service.impl;

import com.lhind.tripapp.converter.SearchToPageConverter;
import com.lhind.tripapp.dto.entityDTO.TripDTO;
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
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TripServiceImpl implements com.lhind.tripapp.service.TripService {
    private TripRepository tripRepository;
    private UserRepository userRepository;
    private SearchToPageConverter pageConverter;
    private ModelMapper mapper;
    private static final Logger logger = LogManager.getLogger();

    @Autowired
    public TripServiceImpl(TripRepository tripRepository,
                           UserRepository userRepository,
                           SearchToPageConverter pageConverter,
                           ModelMapper modelMapper) {
        this.tripRepository = tripRepository;
        this.userRepository = userRepository;
        this.pageConverter = pageConverter;
        this.mapper = modelMapper;
    }

    @Override
    public TripDTO saveTrip(Trip trip) {
        return this.mapper.map(this.tripRepository.save(trip), TripDTO.class);
    }

    @Override
    public TripDTO findById(Long id) {
        Trip t = this.tripRepository.findById(id).orElseThrow(
                () -> {
                    logger.error("Could not find trip with id: " + id);
                    return new EntityNotFoundException("Could not find trip with the provided id!");
                });
        TripDTO trip = this.mapper.map(t, TripDTO.class);
        return trip;
    }

    @Override
    public PagedResponse<TripDTO> findAllByUser(SearchRequest request) {
        UserDetailsImpl userDetails = (UserDetailsImpl)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User loggedInUser = this.userRepository.findById(userDetails.getId()).get();

        Page<Trip> tripsPage =
                this.tripRepository.findAllByUser(loggedInUser,
                        this.pageConverter.toPageRequest(request));
        List<TripDTO> trips = tripsPage.getContent().
                stream().
                map(trip -> {
                    return mapper.map(trip, TripDTO.class);
                })
                .collect(Collectors.toList());

        return new PagedResponse<TripDTO>(trips, tripsPage.getSize(),tripsPage.getTotalElements());
    }

    @Override
    @Transactional
    public TripDTO addTrip(Trip toBeAddedTrip) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User tripOwner = this.userRepository.findById(userDetails.getId())
                .orElseThrow(() -> {
                    logger.error("Trip could not be added because user is not logged in!");
                    return new EntityNotFoundException("Logged in user does not exist!");
                });

        tripOwner.addTrip(toBeAddedTrip);
        toBeAddedTrip.setUser(tripOwner);
        this.userRepository.save(tripOwner);
        return this.mapper.map(this.tripRepository.save(toBeAddedTrip), TripDTO.class);
    }

    @Override
    public List<TripDTO> findAllTripsByUser(Long id) {
        List<TripDTO> trips = this.tripRepository.findTripsByUserId(id)
                .stream()
                .map(trip -> {
                    return this.mapper.map(trip, TripDTO.class);
                }).collect(Collectors.toList());
        return trips;
    }

    @Override
    public void requestApproval(Trip requestingApprovalTrip) {
        requestingApprovalTrip.setStatus(ETripStatus.WAITING_FOR_APPROVAL);
        this.tripRepository.save(requestingApprovalTrip);
    }


    @Override
    public void deleteTrip(TripDeletionDTO request) {
        if(this.tripRepository.existsById(request.getTripId())) {
            Trip trip = tripRepository.findById(request.getTripId()).get();
            trip.getFlights().forEach(
                    flight -> {
                        flight.setTrip(null);
                    }
            );
            this.tripRepository.deleteById(request.getTripId());
        }
        else {
            // Throw trip not found exception
            logger.error("Could not delete trip with id "+ request.getTripId() + " because it does not exist!");
            throw new EntityNotFoundException("Could not delete the trip because it does not exist!");
        }
    }
}
