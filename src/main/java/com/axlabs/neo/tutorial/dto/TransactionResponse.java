package com.axlabs.neo.tutorial.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionResponse {

    @JsonProperty
    private Boolean status;

    @JsonProperty
    private String transactionId;

    @JsonProperty
    private String faultCause;

    public TransactionResponse(Boolean status, String transactionId, String faultCause) {
        this.status = status;
        this.transactionId = transactionId;
        this.faultCause = faultCause;
    }

    public Boolean getStatus() {
        return this.status;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getFaultCause() {
        return faultCause;
    }
}
