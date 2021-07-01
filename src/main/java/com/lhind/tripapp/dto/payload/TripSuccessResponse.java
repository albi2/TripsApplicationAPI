package com.lhind.tripapp.dto.payload;

public class TripSuccessResponse {
    public String message;

    public TripSuccessResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
