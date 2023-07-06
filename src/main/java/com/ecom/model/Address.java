package com.ecom.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "address")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;


    private String addressLine1;

    private String addressLine2;

    private String city;

    private String country;

    @ManyToOne(optional = false)
    @JsonIgnore
    @JoinColumn(name = "user_id", nullable = false)
    private LocalUser user;

}
