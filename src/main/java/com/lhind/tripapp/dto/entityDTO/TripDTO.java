package com.lhind.tripapp.dto.entityDTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lhind.tripapp.model.ETripStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TripDTO tripDTO = (TripDTO) o;
        return Objects.equals(id, tripDTO.id) && Objects.equals(reason, tripDTO.reason) && Objects.equals(description, tripDTO.description) && Objects.equals(fromCountry, tripDTO.fromCountry) && Objects.equals(toCountry, tripDTO.toCountry) && Objects.equals(departureDate, tripDTO.departureDate) && Objects.equals(arrivalDate, tripDTO.arrivalDate) && Objects.equals(status, tripDTO.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, reason, description, fromCountry, toCountry, departureDate, arrivalDate, status);
    }
}
