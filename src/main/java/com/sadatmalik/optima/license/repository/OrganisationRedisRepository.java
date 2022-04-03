package com.sadatmalik.optima.license.repository;

import com.sadatmalik.optima.license.model.Organisation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Using Spring Data to access our Redis store, we need to define a repository class that will
 * be injected into any of service classes that need to access Redis.
 *
 * @author sadatmalik
 */
@Repository
public interface OrganisationRedisRepository extends CrudRepository<Organisation,String> {
}