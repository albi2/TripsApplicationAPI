package com.lhind.tripapp.converter;

import com.lhind.tripapp.dto.entityDTO.TripCreationDTO;
import com.lhind.tripapp.model.ETripReason;
import com.lhind.tripapp.model.Trip;
import com.lhind.tripapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TripCreationConverter {

    private UserRepository userRepository;

    @Autowired
    public TripCreationConverter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public TripCreationDTO toDto(Trip trip) {
        TripCreationDTO dto = new TripCreationDTO();
        dto.setReason(trip.getReason().name());
        dto.setArrivalDate(trip.getArrivalDate());
        dto.setDepartureDate(trip.getDepartureDate());
        dto.setFromCountry(trip.getFromCountry());
        dto.setToCountry(trip.getToCountry());
        dto.setDescription(trip.getDescription());
        return dto;
    }

    public Trip fromDto(TripCreationDTO tripDto) {
        Trip trip = new Trip();
        trip.setReason(ETripReason.valueOf(tripDto.getReason()));
        trip.setFromCountry(tripDto.getFromCountry());
        trip.setToCountry(tripDto.getToCountry());
        trip.setArrivalDate(tripDto.getArrivalDate());
        trip.setDepartureDate(tripDto.getDepartureDate());
        trip.setDescription(tripDto.getDescription());
        return trip;
    }


}
