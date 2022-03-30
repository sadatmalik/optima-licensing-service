package com.sadatmalik.optima.license.service;

import com.sadatmalik.optima.license.model.License;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * The service class that we’ll use to develop the logic of the different services we are going
 * to create on the controller class.
 *
 * @author sadatmalik
 */
@Service
public class LicenseService {

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

    public String createLicense(License license, String organizationId) {
        String responseMessage = null;
        if(license != null) {
            license.setOrganizationId(organizationId);
            responseMessage = String.format("This is the post and the " +
                    "object is: %s", license);
        }
        return responseMessage;
    }

    public String updateLicense(License license, String organizationId) {
        String responseMessage = null;
        if (license != null) {
            license.setOrganizationId(organizationId);
            responseMessage = String.format("This is the put and " +
                    "the object is: %s", license);
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
