package com.sadatmalik.optima.license.service.client;

import com.sadatmalik.optima.license.model.Organisation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * The Spring Discovery Client offers the lowest level of access to the Load Balancer and the
 * services registered within it. Using the injected Discovery Client, we can query for all the
 * services registered with the Spring Cloud Load Balancer client and their corresponding URLs.
 *
 * To retrieve all instances of the organisation services registered with Eureka, we use the
 * getInstances() method, passing in the service key that we’re looking for to retrieve a list
 * of ServiceInstance objects.
 *
 * The ServiceInstance class holds information about a specific instance of a service, including
 * its hostname, port, and URI.
 *
 * We take the first ServiceInstance class in the list to build a target URL that can then be
 * used to call the service, using a standard Spring RestTemplate to call the organisation
 * service and retrieve the required organisation data.
 *
 * To use the Discovery Client, we first need to annotate the OptimaLicensingServiceApplication
 * with @EnableDiscoveryClient.
 *
 * You should only use the Discovery Client when your service needs to query the Load Balancer
 * to understand what services and service instances are registered with it. It becomes your
 * responsibility to choose which returned service instance you’re going to invoke. And it is
 * your responsibility to build the URL that you’ll use to call your service.
 *
 * Directly instantiated the RestTemplate class in the code is antithetical to usual Spring
 * REST invocations because you’ll usually have the Spring framework inject the RestTemplate
 * class via the @Autowired annotation.
 *
 * Once we’ve enabled the Spring Discovery Client in the application class via
 * @EnableDiscoveryClient, all REST templates managed by the Spring framework will have a Load
 * Balancer–enabled interceptor injected into those instances. This will change how URLs are
 * created with the RestTemplate class. Directly instantiating RestTemplate allows you to avoid
 * this behaviour.
 *
 * @author sadatmalik
 */
@Component
public class OrganisationDiscoveryClient {

    @Autowired
    private DiscoveryClient discoveryClient;

    /**
     * The code that calls the organisation service via the Spring Discovery
     * Client.
     *
     * Using the injected discovery service, gets a list of all the instances of
     * the organisation services. Then retrieves the service endpoint, and uses
     * it with a standard Spring RestTemplate class to call the service.
     *
     * @param organisationId
     * @return
     */
    public Organisation getOrganisation(String organisationId) {
        RestTemplate restTemplate = new RestTemplate();
        List<ServiceInstance> instances = discoveryClient
                .getInstances("optima-organisation-service");

        if (instances.size() == 0)
            return null;

        String serviceUri = String.format("%s/v1/organisation/%s",
                instances.get(0).getUri().toString(), organisationId);

        ResponseEntity<Organisation> restExchange =
                restTemplate.exchange(
                        serviceUri,
                        HttpMethod.GET,
                        null, Organisation.class, organisationId);

        return restExchange.getBody();
    }
}