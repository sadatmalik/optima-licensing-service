package com.sadatmalik.optima.license.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * POJO class that encapsulates the license data.
 *
 * Extending RepresentationModel<License> gives us the ability to add links to the License
 * model class. Convenient for expressing HATEOAS documentation in ResponseEntity JSON
 * bodies.
 *
 * The @Entity annotation lets Spring know that this Java POJO is going to be mapping objects
 * that will hold data.
 *
 * The @Table annotation tells Spring/JPA what database table to map.
 *
 * The @Id annotation identifies the primary key for the database.
 *
 * Finally, each one of the columns from the database that will be mapped to individual
 * properties is marked with a @Column attribute. Where the attribute has the same name as the
 * database column, there is no need to add the @Column annotation.
 *
 * @author sadatmalik
 */
@Getter
@Setter
@Entity
@Table(name="licenses")
@ToString
public class License extends RepresentationModel<License> {

    @Id
    @Column(name = "license_id", nullable = false)
    private String licenseId;

    private String description;

    @Column(name = "organization_id", nullable = false)
    private String organizationId;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "license_type", nullable = false)
    private String licenseType;

    @Column(name="comment")
    private String comment;

    public License withComment(String comment){
        this.setComment(comment);
        return this;
    }
}
