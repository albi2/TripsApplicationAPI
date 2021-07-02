package com.lhind.tripapp.repository;

import com.lhind.tripapp.model.Flight;
import com.lhind.tripapp.model.Trip;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {

    Page<Flight> findAllByTrip(Trip trip, Pageable pageable);
}
