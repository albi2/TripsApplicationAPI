package com.lhind.tripapp.dto.entityDTO;

import com.lhind.tripapp.model.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserDTO {

    @NotBlank
    private Long id;

    @NotBlank
    private String username;

    @NotBlank
    private String email;

    private List<String> roles;

}
