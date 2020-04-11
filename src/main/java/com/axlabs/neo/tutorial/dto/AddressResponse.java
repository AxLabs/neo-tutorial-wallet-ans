package com.axlabs.neo.tutorial.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AddressResponse {

    @JsonProperty
    private String address;

    public AddressResponse(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

}
