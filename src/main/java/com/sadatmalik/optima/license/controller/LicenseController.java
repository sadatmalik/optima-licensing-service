package com.sadatmalik.optima.license.controller;

import com.sadatmalik.optima.license.model.License;
import com.sadatmalik.optima.license.service.LicenseService;
import com.sadatmalik.optima.license.utils.UserContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeoutException;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * The @RestController is a class-level Java annotation that tells the Spring container that
 * this Java class will be used for a REST-based service. This annotation automatically handles
 * the serialization of data passed into the services as either JSON or XML (by default, this
 * class serializes returned data into JSON).
 *
 * Unlike the traditional Spring @Controller annotation, @RestController does not require you to
 * return a ResponseBody class from your method in the controller class. This is all handled by
 * the presence of the @RestController annotation, which includes the @ResponseBody annotation.
 *
 * WebMvcLinkBuilder is a utility class for creating links on the controller classes. We are
 * using it to generate HATEOAS links for our controller.
 *
 * @author sadatmalik
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value="v1/organisation/{organisationId}/license")
public class LicenseController {

    private final LicenseService licenseService;

    /**
     * This method implements the GET verb used in a REST call and returns a single
     * License class instance.
     *
     * We also create the HATEOAS configuration to retrieve the links for the
     * LicenseController class.
     *
     * @return returns a license in a ResponseEntity representing the entire HTTP
     * response, including the status code, the headers, and the body. If successful, it
     * allows us to return the License object as the body and the 200(OK) status code as
     * the HTTP response of the service.
     */
    @GetMapping(value="/{licenseId}")
    public ResponseEntity<License> getLicense(
            @PathVariable("organisationId") String organisationId,
            @PathVariable("licenseId") String licenseId) {

        License license = licenseService
                .getLicense(licenseId,organisationId);

        license.add(
                linkTo(methodOn(LicenseController.class)
                        .getLicense(organisationId, license.getLicenseId()))
                        .withSelfRel(),
                linkTo(methodOn(LicenseController.class)
                        .createLicense(organisationId, license, null))
                        .withRel("createLicense"),
                linkTo(methodOn(LicenseController.class)
                        .updateLicense(organisationId, license))
                        .withRel("updateLicense"),
                linkTo(methodOn(LicenseController.class)
                        .deleteLicense(organisationId, license.getLicenseId()))
                        .withRel("deleteLicense"));

        return ResponseEntity.ok(license);
    }

    /**
     * The clientType parameter passed on the route drives the type of client we’re going to
     * use. The specific types we can pass in on this route include the following:
     *
     *   - Discovery: Uses the Discovery Client and a standard Spring RestTemplate class to
     *   invoke the organization service
     *
     *   - Rest: Uses an enhanced Spring RestTemplate to invoke the Load Balancer service
     *
     *   - Feign: Uses Netflix’s Feign client library to invoke a service via the Load
     *   Balancer
     *
     * To call the getLicense() services with the different clients, you must call the following
     * GET endpoint:
     *
     *   - http://<licensing service Hostname/IP>:<licensing service Port>/v1/organisation/
     *   <organisationID>/license/<licenseID>/<client type( feign, discovery, rest)>
     *
     * @param organisationId received as a path variable
     * @param licenseId a license id path variable
     * @param clientType specifies the client libraries in which a service consumer can
     *                   interact with the Spring Cloud Load Balancer
     * @return a license
     */
    @RequestMapping(value="/{licenseId}/{clientType}", method = RequestMethod.GET)
    public License getLicensesWithClient(
            @PathVariable("organisationId") String organisationId,
            @PathVariable("licenseId") String licenseId,
            @PathVariable("clientType") String clientType) {

        return licenseService.getLicense(organisationId,
                licenseId, clientType);
    }

    /**
     * we use the @PathVariable and the @RequestBody annotations in the parameter body
     * of the updateLicense() method. @RequestBody maps the HTTPRequest body to a transfer
     * object (in this case, the License object).
     *
     * @param organisationId path variable
     * @param request request body
     * @return returns a status message
     */
    @PutMapping
    public ResponseEntity<License> updateLicense(
            @PathVariable("organisationId") String organisationId,
            @RequestBody License request) {

        return ResponseEntity.ok(licenseService
                .updateLicense(request));
    }

    /**
     * Note the @RequestHeader annotation maps method parameters with request header
     * values. This service parameter is not required, so if it’s not specified, we
     * will use the default locale.
     *
     * @param organisationId
     * @param request
     * @param locale receive the language from the request Accept-Language header.
     * @return
     */
    @PostMapping
    public ResponseEntity<String> createLicense(
            @PathVariable("organisationId") String organisationId,
            @RequestBody License request,
            @RequestHeader(value = "Accept-Language",required = false)
                    Locale locale) {

        return ResponseEntity.ok(licenseService
                .createLicense(request, organisationId, locale));
    }

    @DeleteMapping(value="/{licenseId}")
    public ResponseEntity<String> deleteLicense(
            @PathVariable("organisationId") String organisationId,
            @PathVariable("licenseId") String licenseId) {

        return ResponseEntity.ok(licenseService
                .deleteLicense(licenseId));
    }

    @RequestMapping(value="/", method = RequestMethod.GET)
    public List<License> getLicenses(
            @PathVariable("organisationId") String organisationId)
            throws TimeoutException {
        log.debug("LicenseServiceController Correlation id: {}",
                UserContextHolder.getContext().getCorrelationId());
        return licenseService.getLicensesByOrganisation(organisationId);
    }

}
