package com.lhind.tripapp.dto.entityDTO;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class TripIdDTO {
    @Id
    @NotNull
    private Long id;
}
