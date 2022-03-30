package com.sadatmalik.optima.license.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.hateoas.RepresentationModel;

/**
 * POJO class that encapsulates the license data.
 *
 * Extending RepresentationModel<License> gives us the ability to add links to the License
 * model class. Convenient for expressing HATEOAS documentation in ResponseEntity JSON
 * bodies.
 *
 * @author sadatmalik
 */
@Getter
@Setter
@ToString
public class License extends RepresentationModel<License> {

    private int id;
    private String licenseId;
    private String description;
    private String organizationId;
    private String productName;
    private String licenseType;

}
