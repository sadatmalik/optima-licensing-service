package com.sadatmalik.optima.license.service;

import com.sadatmalik.optima.license.config.ServiceConfig;
import com.sadatmalik.optima.license.model.License;
import com.sadatmalik.optima.license.repository.LicenseRepository;
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

    public License getLicense(String licenseId, String organizationId){
        License license = licenseRepository
            .findByOrganizationIdAndLicenseId(organizationId, licenseId);
        if (null == license) {
            throw new IllegalArgumentException(
                String.format(messages.getMessage(
                                "license.search.error.message", null, null),
                        licenseId, organizationId));
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
    public String createLicense(License license, String organizationId,
                                 Locale locale) {
        String responseMessage = null;

        if (license != null) {
            license.setOrganizationId(organizationId);
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
}
