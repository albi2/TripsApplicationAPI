package com.lhind.tripapp.dto.entityDTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lhind.tripapp.model.EFlightClass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
public class FlightDTO {
    @NotNull
    private Long id;

    private String fromCity;

    private String toCity;

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime departureDate;

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime arrivalDate;

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime boardingTime;

    private Double price;

    @Size(max=20)
    private String flightClass;
}
