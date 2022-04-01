package com.sadatmalik.optima.license.service;

import com.sadatmalik.optima.license.config.ServiceConfig;
import com.sadatmalik.optima.license.model.License;
import com.sadatmalik.optima.license.model.Organisation;
import com.sadatmalik.optima.license.repository.LicenseRepository;
import com.sadatmalik.optima.license.service.client.OrganisationDiscoveryClient;
import com.sadatmalik.optima.license.service.client.OrganisationFeignClient;
import com.sadatmalik.optima.license.service.client.OrganisationRestTemplateClient;
import com.sadatmalik.optima.license.utils.UserContextHolder;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeoutException;

/**
 * The service class that we’ll use to develop the logic of the different services we are going
 * to create on the controller class.
 *
 * Repository calls for the retrieval of licensing service data from the licensing database are
 * wrapped using a synchronous circuit breaker with Resilience4j.
 *
 * With a synchronous call, the licensing service retrieves its data but waits for the SQL
 * statement to complete or for a circuit breaker timeout before it continues processing.
 *
 * @author sadatmalik
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LicenseService {

    private final MessageSource messages;
    private final LicenseRepository licenseRepository;
    private final ServiceConfig config;

    private final OrganisationFeignClient organisationFeignClient;
    private final OrganisationRestTemplateClient organisationRestClient;
    private final OrganisationDiscoveryClient organisationDiscoveryClient;

    /**
     * Resilience4j and Spring Cloud use @CircuitBreaker to mark Java class methods managed
     * by a Resilience4j circuit breaker. When the Spring framework sees this annotation, it
     * dynamically generates a proxy that wraps the method and manages all calls to that method
     * through a thread pool specifically set aside to handle remote calls.
     *
     * With the use of the @CircuitBreaker annotation, any time the getLicensesByOrganisation()
     * method is called, the call is wrapped with a Resilience4j circuit breaker. The circuit
     * breaker interrupts any failed attempt to call the getLicensesByOrganisation() method.
     *
     * The randomlyRunningLong() call simulates the method running into a slow or timed out
     * database query.
     *
     * The annotation includes a simple fallback strategy for our licensing service that
     * returns a licensing object that says no licensing information is currently available.
     * Teh fallback attribute will contain the name of the method that will be called when
     * Resilience4j interrupts a call because of a failure.
     *
     * The @Bulkhead annotation indicates that we are setting up a bulkhead pattern. If we
     * set no further values in the application properties, Resilience4j uses the default
     * values for the semaphore bulkhead type. Omit the type attribute to default to the
     * semaphore type.
     *
     * The @Retry sets up the retry pattern around all calls surrounding the lookup. This
     * retry pattern stops overloading the service with more calls than it can consume in a
     * given timeframe. This is an imperative technique to prepare our API for high
     * availability and reliability.
     *
     * The @RateLimiter annotation sets up the rate limits. The main difference between the
     * bulkhead and the rate limiter pattern is that the bulkhead pattern is in charge of
     * limiting the number of concurrent calls (for example, it only allows X concurrent calls
     * at a time). With the rate limiter, we can limit the number of total calls in a given
     * timeframe (for example, allow X number of calls every Y seconds).
     *
     * @param organisationId
     * @return
     */
    @CircuitBreaker(name = "licenseService",
            fallbackMethod = "buildFallbackLicenseList")
    @RateLimiter(name = "licenseService",
            fallbackMethod = "buildFallbackLicenseList")
    @Retry(name = "retryLicenseService",
            fallbackMethod= "buildFallbackLicenseList")
    @Bulkhead(name= "bulkheadLicenseService",
            //type = Bulkhead.Type.THREADPOOL,
            fallbackMethod= "buildFallbackLicenseList")
    public List<License> getLicensesByOrganisation(String organisationId)
            throws TimeoutException {
        log.debug("getLicensesByOrganization Correlation id: {}",
                UserContextHolder.getContext().getCorrelationId());
        randomlyRunLong();
        return licenseRepository.findByOrganisationId(organisationId);
    }

    /**
     * The fallback method must reside in the same class as the original method that was
     * protected by @CircuitBreaker. To create the fallback method in Resilience4j, we need
     * to create a method that contains the same signature as the originating function plus
     * one extra parameter, which is the target exception parameter.
     *
     * @param organisationId
     * @param t
     * @return
     */
    private List<License> buildFallbackLicenseList(String organisationId, Throwable t){
        List<License> fallbackList = new ArrayList<>();
        License license = new License();
        license.setLicenseId("0000000-00-00000");
        license.setOrganisationId(organisationId);
        license.setProductName("Sorry no licensing information currently available");
        fallbackList.add(license);
        return fallbackList;
    }

    private void randomlyRunLong() throws TimeoutException {
        Random rand = new Random();
        int randomNum = rand.nextInt(3) + 1;
        if (randomNum==3)
            sleep();
    }

    private void sleep() throws TimeoutException {
        try {
            Thread.sleep(5000);
            throw new java.util.concurrent.TimeoutException();
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }
    }

    public License getLicense(String licenseId, String organisationId){
        License license = licenseRepository
            .findByOrganisationIdAndLicenseId(organisationId, licenseId);
        if (null == license) {
            throw new IllegalArgumentException(
                String.format(messages.getMessage(
                                "license.search.error.message", null, null),
                        licenseId, organisationId));
        }
        return license.withComment(config.getProperty());
    }

    public License getLicense(String organisationId, String licenseId,
                              String clientType) {
        License license = licenseRepository
                .findByOrganisationIdAndLicenseId(organisationId, licenseId);

        if (null == license) {
            throw new IllegalArgumentException(String.format(
                    messages.getMessage("license.search.error.message",
                            null, null),licenseId, organisationId));
        }

        Organisation organisation = retrieveOrganisationInfo(organisationId, clientType);

        if (organisation != null) {
            license.setOrganisationName(organisation.getName());
            license.setContactName(organisation.getContactName());
            license.setContactEmail(organisation.getContactEmail());
            license.setContactPhone(organisation.getContactPhone());
        }

        return license.withComment(config.getProperty());
    }

    /**
     * Receives the locale as a method parameter and uses it to retrieve the
     * specific message.
     *
     * We receive the Locale from the Controller. We call the messages.getMessage(
     * "license.create.message",null,locale) using the locale we received.
     *
     * @param license
     * @return
     */
    public String createLicense(License license, String organisationId,
                                 Locale locale) {
        String responseMessage = null;

        if (license != null) {
            license.setOrganisationId(organisationId);
            license.setLicenseId(UUID.randomUUID().toString());
            licenseRepository.save(license);
            license.withComment(config.getProperty());

            responseMessage = String.format(messages.getMessage(
                            "license.create.message", null, locale),
                    license);
        }
        return responseMessage;

    }

    /**
     * We can call the messages.getMessage("license.update.message", null, null) without
     * sending any locale. In this particular scenario, the application will use the
     * default locale we previously defined in the bootstrap class.
     *
     * @param license
     * @return
     */
    public License updateLicense(License license){
        licenseRepository.save(license);
        return license.withComment(config.getProperty());
    }

    public String deleteLicense(String licenseId){
        String responseMessage = null;
        License license = new License();
        license.setLicenseId(licenseId);
        licenseRepository.delete(license);
        responseMessage = String.format(messages.getMessage(
                "license.delete.message", null, null),licenseId);
        return responseMessage;
    }

    /**
     * The method will resolve based on the clientType passed to the route. This client type is
     * used to look up an organization service instance. Called from getLicense() method to
     * retrieve the organization data from the database.
     *
     * @param organisationId
     * @param clientType
     * @return
     */
    private Organisation retrieveOrganisationInfo(String organisationId,
                                                  String clientType) {
        Organisation organisation = null;

        switch (clientType) {
            case "feign":
                System.out.println("I am using the feign client");
                organisation = organisationFeignClient.getOrganisation(organisationId);
                break;
            case "rest":
                System.out.println("I am using the rest client");
                organisation = organisationRestClient.getOrganisation(organisationId);
                break;
            case "discovery":
                System.out.println("I am using the discovery client");
                organisation = organisationDiscoveryClient.getOrganisation(organisationId);
                break;
            default:
                organisation = organisationRestClient.getOrganisation(organisationId);
                break;
        }

        return organisation;
    }
}
