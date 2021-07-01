package com.lhind.tripapp.dto.payload;

import javax.validation.constraints.NotBlank;

public class UserTripRequest {
    @NotBlank
    private Long tripId;

    public Long getTripId() {
        return tripId;
    }

    public void setTripId(Long tripId) {
        this.tripId = tripId;
    }
}
