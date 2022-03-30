package com.sadatmalik.optima.license.controller;

import com.sadatmalik.optima.license.model.License;
import com.sadatmalik.optima.license.service.LicenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

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
@RestController
@RequiredArgsConstructor
@RequestMapping(value="v1/organization/{organizationId}/license")
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
            @PathVariable("organizationId") String organizationId,
            @PathVariable("licenseId") String licenseId) {

        License license = licenseService
                .getLicense(licenseId,organizationId);

        license.add(
                linkTo(methodOn(LicenseController.class)
                        .getLicense(organizationId, license.getLicenseId()))
                        .withSelfRel(),
                linkTo(methodOn(LicenseController.class)
                        .createLicense(organizationId, license, null))
                        .withRel("createLicense"),
                linkTo(methodOn(LicenseController.class)
                        .updateLicense(organizationId, license))
                        .withRel("updateLicense"),
                linkTo(methodOn(LicenseController.class)
                        .deleteLicense(organizationId, license.getLicenseId()))
                        .withRel("deleteLicense"));

        return ResponseEntity.ok(license);
    }

    /**
     * we use the @PathVariable and the @RequestBody annotations in the parameter body
     * of the updateLicense() method. @RequestBody maps the HTTPRequest body to a transfer
     * object (in this case, the License object).
     *
     * @param organizationId path variable
     * @param request request body
     * @return returns a status message
     */
    @PutMapping
    public ResponseEntity<String> updateLicense(
            @PathVariable("organizationId") String organizationId,
            @RequestBody License request) {

        return ResponseEntity.ok(licenseService
                .updateLicense(request, organizationId));
    }

    /**
     * Note the @RequestHeader annotation maps method parameters with request header
     * values. This service parameter is not required, so if it’s not specified, we
     * will use the default locale.
     *
     * @param organizationId
     * @param request
     * @param locale receive the language from the request Accept-Language header.
     * @return
     */
    @PostMapping
    public ResponseEntity<String> createLicense(
            @PathVariable("organizationId") String organizationId,
            @RequestBody License request,
            @RequestHeader(value = "Accept-Language",required = false)
                    Locale locale) {

        return ResponseEntity.ok(licenseService
                .createLicense(request, organizationId, locale));
    }

    @DeleteMapping(value="/{licenseId}")
    public ResponseEntity<String> deleteLicense(
            @PathVariable("organizationId") String organizationId,
            @PathVariable("licenseId") String licenseId) {

        return ResponseEntity.ok(licenseService
                .deleteLicense(licenseId, organizationId));
    }

}
