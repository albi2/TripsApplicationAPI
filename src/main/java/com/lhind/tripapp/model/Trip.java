package com.lhind.tripapp.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="trips")
public class Trip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length=20)
    private ETripReason reason;

    @Column
    @NotBlank
    private String description;

    @Column(name="from_country")
    @NotBlank
    private String fromCountry;

    @Column(name="to_country")
    @NotBlank
    private String toCountry;

    @Column(name="departure_date", nullable = false)
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime departureDate;

    @Column(name="arrival_date", nullable = false)
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime arrivalDate;

    @Enumerated(EnumType.STRING)
    @Column(length=20)
    private ETripStatus status = ETripStatus.CREATED;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @OneToMany(mappedBy = "trip")
    private List<Flight> flights;

    @Override
    public String toString() {
        return "Trip{" +
                "id=" + id +
                ", reason=" + reason +
                ", description='" + description + '\'' +
                ", fromCountry='" + fromCountry + '\'' +
                ", toCountry='" + toCountry + '\'' +
                ", departureDate=" + departureDate +
                ", arrivalDate=" + arrivalDate +
                '}';
    }
}