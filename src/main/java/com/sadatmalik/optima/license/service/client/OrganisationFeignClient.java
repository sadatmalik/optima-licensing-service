package com.sadatmalik.optima.license.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.sadatmalik.optima.license.model.Organisation;

/**
 * An alternative to the Spring Load Balancer–enabled RestTemplate class is Netflix’s Feign
 * client library.
 *
 * The Feign library takes a different approach to call a REST service. With this approach, the
 * developer first defines a Java interface and then adds Spring Cloud annotations to map what
 * Eureka-based service the Spring Cloud Load Balancer will invoke.
 *
 * The Spring Cloud framework will dynamically generate a proxy class to invoke the targeted
 * REST service. There’s no code written for calling the ser- vice other than an interface
 * definition.
 *
 * To enable the Feign client for use in our licensing service, we need to add a new annotation,
 * @EnableFeignClients, to the licensing service’s application class.
 *
 * @FeignClient identifies the service to feign.
 *
 * To use the OrganizationFeignClient class, all we need to do is to autowire it and use it. The
 * Feign client code takes care of all the coding for us.
 *
 * On error handling
 * When you use the standard Spring RestTemplate class, all service call HTTP status codes are
 * returned via the ResponseEntity class’s getStatusCode() method. With the Feign client, any
 * HTTP 4xx–5xx status codes returned by the service being called are mapped to a Feign-
 * Exception. The FeignException contains a JSON body that can be parsed for the specific error
 * message.
 *
 * Feign provides the ability to write an error decoder class that will map the error back to a
 * custom Exception class:
 *
 * @see <a href="https://github.com/Netflix/feign/wiki/Custom-error-handling">...</a>
 *
 * @author sadatmalik
 */
@FeignClient("optima-organisation-service")
public interface OrganisationFeignClient {

    /**
     * Defines the path and action to the endpoint and the parameters passed into the
     * endpoint. This method can be called by the client to invoke the organisation
     * service. The endpoint method defined exactly like how we would expose an endpoint
     * in a Spring controller class.
     *
     * @param organisationId
     * @return The return value from the call to the organization service is automatically
     * mapped to the Organisation class that’s defined as the return value for the method.
     */
    @RequestMapping(
            method= RequestMethod.GET,
            value="/v1/organisation/{organisationId}",
            consumes="application/json")
    Organisation getOrganisation(@PathVariable("organisationId") String organisationId);
}
