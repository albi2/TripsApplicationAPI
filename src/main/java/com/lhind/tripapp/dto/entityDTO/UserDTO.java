package com.lhind.tripapp.dto.entityDTO;

import com.lhind.tripapp.model.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
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

    public UserDTO(Long id, String username, String email, List<String> roles) {
        id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }
}
