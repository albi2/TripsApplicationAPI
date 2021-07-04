package com.lhind.tripapp.dto.entityDTO;

import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty(name="id", dataType = "long", example = "1")
    private Long id;
}
