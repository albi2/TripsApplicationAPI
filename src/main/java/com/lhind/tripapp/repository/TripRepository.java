package com.lhind.tripapp.repository;

import com.lhind.tripapp.model.Trip;
import com.lhind.tripapp.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
public interface TripRepository extends JpaRepository<Trip,Long> {
    Page<Trip> findAllByUser(User u, Pageable pageable);

    @Query("SELECT T FROM Trip T " +
            "WHERE T.user.id = :userid AND T.status = 'WAITING_FOR_APPROVAL'")
    List<Trip> findTripsByUserId(@Param("userid") Long userId);

//    @Query("UPDATE Trip T SET T.status = 'APPROVED' WHERE T.id = :tripId")
//    void approveTrip(@Param("tripId") Long tripId);
}
