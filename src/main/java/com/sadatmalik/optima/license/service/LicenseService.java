package com.sadatmalik.optima.license.service;

import com.sadatmalik.optima.license.config.ServiceConfig;
import com.sadatmalik.optima.license.model.License;
import com.sadatmalik.optima.license.model.Organisation;
import com.sadatmalik.optima.license.repository.LicenseRepository;
import com.sadatmalik.optima.license.service.client.OrganisationDiscoveryClient;
import com.sadatmalik.optima.license.service.client.OrganisationFeignClient;
import com.sadatmalik.optima.license.service.client.OrganisationRestTemplateClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.UUID;

/**
 * The service class that we’ll use to develop the logic of the different services we are going
 * to create on the controller class.
 *
 * @author sadatmalik
 */
@Service
@RequiredArgsConstructor
public class LicenseService {

    private final MessageSource messages;
    private final LicenseRepository licenseRepository;
    private final ServiceConfig config;

    private final OrganisationFeignClient organisationFeignClient;
    private final OrganisationRestTemplateClient organisationRestClient;
    private final OrganisationDiscoveryClient organisationDiscoveryClient;

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
