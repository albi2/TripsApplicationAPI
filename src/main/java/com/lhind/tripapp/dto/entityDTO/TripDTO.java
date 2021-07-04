package com.lhind.tripapp.dto.entityDTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lhind.tripapp.model.ETripStatus;
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
public class TripDTO {
    private Long id;

    @Size(max=20)
    private String reason;

    private String description;

    private String fromCountry;

    private String toCountry;

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime departureDate;

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime arrivalDate;

    private String status;

}
