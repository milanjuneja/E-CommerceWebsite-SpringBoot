package com.ecom.api.model;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String jwt;

    private boolean success;

    private String failureReason;

}
