package com.lhind.tripapp.dto.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TripStatusSuccessUpdate {
    private String message;

    public TripStatusSuccessUpdate(String message) {
        this.message = message;
    }
}
