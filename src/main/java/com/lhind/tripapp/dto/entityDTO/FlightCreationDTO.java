package com.lhind.tripapp.dto.entityDTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lhind.tripapp.model.EFlightClass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class FlightCreationDTO {
    @NotBlank
    private String fromCity;

    @NotBlank
    private String toCity;

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime departureDate;

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime arrivalDate;

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime boardingTime;

    @NotNull
    private Double price;

    @Enumerated(EnumType.STRING)
    private EFlightClass flightClass;
}
