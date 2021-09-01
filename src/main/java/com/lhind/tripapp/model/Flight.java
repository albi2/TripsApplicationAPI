package com.lhind.tripapp.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lhind.tripapp.validators.DateRange;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.Objects;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="flights")
@DateRange(first="departureDate",second="arrivalDate")
public class Flight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="from_city", nullable = false)
    @NotBlank
    private String fromCity;

    @Column(name="to_city")
    @NotBlank
    private String toCity;

    @Column(name="departure_date", nullable = false)
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime departureDate;

    @Column(name="arrival_date", nullable = false)
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime arrivalDate;

    @Column(name="boarding_time", nullable = false)
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime boardingTime;

    @Column(name="price")
    private Double price;

    @Enumerated(EnumType.STRING)
    @Column(length=20, name="flight_class")
    private EFlightClass flightClass;

    @ManyToOne
    @JoinColumn(name="trip_id")
    @JsonIgnore
    private Trip trip;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Flight flight = (Flight) o;
        return id.equals(flight.id);
    }
}
