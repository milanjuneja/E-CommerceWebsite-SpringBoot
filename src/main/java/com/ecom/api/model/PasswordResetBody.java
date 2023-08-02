package com.ecom.api.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetBody {
    @NotNull
    @NotBlank
    private String token;
    @NotNull
    @NotBlank
    private String password;
}
