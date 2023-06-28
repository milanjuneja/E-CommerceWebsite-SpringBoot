package com.ecom.api.model;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RegistrationBody {

    private String userName;
    @Email
    private String email;

    private String password;

    private String firstName;

    private String lastName;
    

}
