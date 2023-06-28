package com.ecom.api.model;

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
    private String email;

    private String password;

    private String firstName;

    private String lastName;
    

}
