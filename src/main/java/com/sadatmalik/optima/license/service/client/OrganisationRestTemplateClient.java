package com.sadatmalik.optima.license.service.client;

import com.sadatmalik.optima.license.model.Organisation;
import org.keycloak.adapters.springsecurity.client.KeycloakRestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * Example of invoking services with a Load Balancer–aware Spring REST template.
 *
 * This is one of the more common mechanisms for interacting with the Load Balancer via Spring.
 * To use a Load Balancer–aware RestTemplate class, we need to define a RestTemplate bean with
 * a Spring Cloud @LoadBalanced annotation.
 *
 * The method that we’ll use to create the RestTemplate bean can be found in the
 * OptimaLicenseServiceApplication.
 *
 * Using the backed RestTemplate class pretty much behaves like a standard Spring RestTemplate
 * class, except for one small difference in how the URL for the target service is defined.
 *
 * Rather than using the physical location of the service in the RestTemplate call, you need to
 * build the target URL using the Eureka service ID of the service you want to call.
 *
 * Note - the use of KeycloakRestTemplate is a drop-in replacement for the standard
 * RestTemplate. It handles the propagation of the access token.
 *
 * @author sadatmalik
 */
@Component
public class OrganisationRestTemplateClient {

    @Autowired
    KeycloakRestTemplate restTemplate;

    /**
     * When using a Load Balancer–backed RestTemplate, we build the target URL with the
     * Eureka service ID.
     *
     * The Load Balancer–enabled RestTemplate class parses the URL passed into it and uses
     * whatever is passed in as the server name as the key to query the Load Balancer for
     * an instance of a service.
     *
     * The actual service location and port are entirely abstracted from the developer.
     * Also, by using the RestTemplate class, the Spring Cloud Load Bal- ancer will round-
     * robin load balance all requests among all the service instances.
     *
     * @param organisationId
     * @return
     */
    public Organisation getOrganisation(String organisationId){
        ResponseEntity<Organisation> restExchange =
                restTemplate.exchange(
                        "http://localhost:8072/optima-organisation-service/v1/organisation/{organisationId}",
                        HttpMethod.GET,
                        null, Organisation.class, organisationId);

        return restExchange.getBody();
    }
}