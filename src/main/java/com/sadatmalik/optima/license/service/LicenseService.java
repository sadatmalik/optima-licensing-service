package com.sadatmalik.optima.license.service;

import com.sadatmalik.optima.license.model.License;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Random;

/**
 * The service class that we’ll use to develop the logic of the different services we are going
 * to create on the controller class.
 *
 * @author sadatmalik
 */
@Service
public class LicenseService {

    @Autowired
    MessageSource messages;

    public License getLicense(String licenseId, String organizationId) {
        License license = new License();
        license.setId(new Random().nextInt(1000));
        license.setLicenseId(licenseId);
        license.setOrganizationId(organizationId);
        license.setDescription("Software product");
        license.setProductName("Ostock");
        license.setLicenseType("full");
        return license;
    }

    /**
     * Receives the locale as a method parameter and uses it to retrieve the
     * specific message.
     *
     * We receive the Locale from the Controller. We call the messages.getMessage(
     * "license.create.message",null,locale) using the locale we received.
     *
     * @param license
     * @param organizationId
     * @param locale
     * @return
     */
    public String createLicense(License license, String organizationId,
                                Locale locale) {
        String responseMessage = null;
        if(license != null) {
            license.setOrganizationId(organizationId);
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
     * @param organizationId
     * @return
     */
    public String updateLicense(License license, String organizationId) {
        String responseMessage = null;
        if (license != null) {
            license.setOrganizationId(organizationId);
            responseMessage = String.format(messages.getMessage(
                            "license.update.message", null, null),
                    license.toString());
        }
        return responseMessage;
    }

    public String deleteLicense(String licenseId, String organizationId) {
        String responseMessage = null;
        responseMessage = String.format("Deleting license with id %s for " +
                "the organization %s", licenseId, organizationId);
        return responseMessage;
    }
}
