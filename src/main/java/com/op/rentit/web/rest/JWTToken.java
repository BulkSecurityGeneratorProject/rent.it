package com.op.rentit.web.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Object to return as body in JWT Authentication.
 */
@Data
@AllArgsConstructor
public class JWTToken {

    private String idToken;

    @JsonProperty("id_token")
    public String getIdToken() {
        return idToken;
    }

}
