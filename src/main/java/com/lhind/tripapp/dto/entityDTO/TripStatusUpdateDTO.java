package com.lhind.tripapp.dto.entityDTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@Getter
@Setter
public class TripStatusUpdateDTO {
    @Id
    @NotNull
    private Long id;

    @NotBlank
    private String status;
}
