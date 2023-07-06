package com.ecom.api.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.core.StandardReflectionParameterNameDiscoverer;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginBody {

    @NotNull
    @NotBlank
    private String userName;
    @NotNull
    @NotBlank
    private String password;
}
