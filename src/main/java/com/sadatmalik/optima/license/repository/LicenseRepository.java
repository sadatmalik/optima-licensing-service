package com.sadatmalik.optima.license.repository;

import com.sadatmalik.optima.license.model.License;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * The repository interface, LicenseRepository, is marked with @Repository, which tells Spring
 * that it should treat this interface as a repository and generate a dynamic proxy for it. The
 * dynamic proxy, in this case, provides a set of fully featured, ready-to-use objects.
 *
 * @author sadatmalik
 */
public interface LicenseRepository extends CrudRepository<License,String> {

    List<License> findByOrganizationId(String organizationId);

    License findByOrganizationIdAndLicenseId(String organizationId, String licenseId);
}
