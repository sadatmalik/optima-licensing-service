package com.sadatmalik.optima.license.utils;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

/**
 * POJO class that contains all the specific data we want to store in the UserContextHolder.
 *
 * The UserContext class holds the HTTP header values for an individual service client
 * request that is processed by our microservice.
 *
 * @author sadatmalik
 */
@Getter
@Setter
@Component
public class UserContext {
    public static final String CORRELATION_ID  = "tmx-correlation-id";
    public static final String AUTH_TOKEN      = "tmx-auth-token";
    public static final String USER_ID         = "tmx-user-id";
    public static final String ORGANISATION_ID = "tmx-organisation-id";

    private String correlationId= "";
    private String authToken= "";
    private String userId = "";
    private String organisationId = "";

}