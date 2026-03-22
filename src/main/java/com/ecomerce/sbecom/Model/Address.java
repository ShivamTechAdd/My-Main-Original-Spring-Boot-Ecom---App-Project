package com.ecomerce.sbecom.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "addresses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;

    @NotBlank
    @Size(min = 5,message = "Street name must be at lest 5 character.")
    private String street;

    @NotBlank
    @Size(min = 5,message = "building  name must be at lest 5 character.")
    private String buildingName;

    @NotBlank
    @Size(min = 4,message = "City  name must be at lest 4 character.")
    private String city;

    @NotBlank
    @Size(min = 3,message = "state  name must be at lest 3 character.")
    private String state;

    @NotBlank
    @Size(min = 2,message = "Country  name must be at lest 2 character.")
    private String country;

    @NotBlank
    @Size(min = 6,message = "pincode must be of 6 character.")
    private String pincode;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Address(String pincode,String street, String buildingName, String city, String state, String country) {
        this.pincode = pincode;
        this.street = street;
        this.buildingName = buildingName;
        this.city = city;
        this.state = state;
        this.country = country;
    }


}
