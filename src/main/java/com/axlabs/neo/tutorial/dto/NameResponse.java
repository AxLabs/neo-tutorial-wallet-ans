package com.axlabs.neo.tutorial.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NameResponse {

    @JsonProperty
    private Boolean status;

    @JsonProperty
    private String name;

    @JsonProperty
    private String address;

    public NameResponse(Boolean status, String name, String address) {
        this.status = status;
        this.name = name;
        this.address = address;
    }

    public Boolean getStatus() {
        return this.status;
    }

    public String getName() {
        return this.name;
    }

    public String getAddress() {
        return this.address;
    }

}
