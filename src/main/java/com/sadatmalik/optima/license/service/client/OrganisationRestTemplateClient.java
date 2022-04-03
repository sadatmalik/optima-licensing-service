package com.sadatmalik.optima.license.service.client;

import com.sadatmalik.optima.license.model.Organisation;
import com.sadatmalik.optima.license.repository.OrganisationRedisRepository;
import com.sadatmalik.optima.license.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
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
 * Every time the licensing service needs the organisation data, it checks the Redis cache
 * before calling the organisation service.
 *
 * To increase resiliency, we never let the entire call fail if we cannot communicate with
 * the Redis server. Instead, we log the exception and let the call through to the
 * organisation service.
 *
 * @author sadatmalik
 */
@Slf4j
@Component
public class OrganisationRestTemplateClient {

    @Autowired
    private KeycloakRestTemplate restTemplate;

    @Autowired
    OrganisationRedisRepository redisRepository;

    /**
     * When using a Load Balancer–backed RestTemplate, we build the target URL with the
     * Eureka service ID.
     *
     * The Load Balancer–enabled RestTemplate class parses the URL passed into it and uses
     * whatever is passed in as the server name as the key to query the Load Balancer for
     * an instance of a service.
     *
     * The actual service location and port are entirely abstracted from the developer.
     * Also, by using the RestTemplate class, the Spring Cloud Load Balancer will round-
     * robin load balance all requests among all the service instances.
     *
     * If can’t retrieve data from Redis, calls the organisation service to retrieve the
     * data from the source database and saves it in Redis.
     *
     * @param organisationId
     * @return
     */
    public Organisation getOrganisation(String organisationId){
        log.debug("In Licensing Service.getOrganization: {}", UserContext.getCorrelationId());

        Organisation organisation = checkRedisCache(organisationId);

        if (organisation != null){
            log.debug("I have successfully retrieved an organization {} " +
                    "from the redis cache: {}", organisationId, organisation);
            return organisation;
        }

        log.debug("Unable to locate organisation from the redis cache: {}.",
                organisationId);

        ResponseEntity<Organisation> restExchange =
                restTemplate.exchange(
                        "http://localhost:8072/optima-organisation-service/v1/organisation/{organisationId}",
                        HttpMethod.GET,
                        null, Organisation.class, organisationId);

        /*Save the record to cache*/
        organisation = restExchange.getBody();
        if (organisation != null) {
            cacheOrganisationObject(organisation);
        }

        return restExchange.getBody();
    }

    /**
     * Tries to retrieve an Organization class with its organization ID from Redis.
     *
     * @param organisationId
     * @return
     */
    private Organisation checkRedisCache(String organisationId) {
        try {
            return redisRepository.findById(
                    organisationId).orElse(null);
        } catch (Exception ex) {
            log.error("Error encountered while trying to retrieve organization {} " +
                    "check Redis Cache.  Exception {}", organisationId, ex);
            return null;
        }
    }

    /**
     * Saves the organisation in Redis.
     *
     * @param organisation
     */
    private void cacheOrganisationObject(Organisation organisation) {
        try {
            redisRepository.save(organisation);
        }catch (Exception ex){
            log.error("Unable to cache organization {} in Redis. " +
                    "Exception {}", organisation.getId(), ex);
        }
    }
}