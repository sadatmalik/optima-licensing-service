package com.sadatmalik.optima.license.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * POJO class that encapsulates the license data.
 *
 * @author sadatmalik
 */
@Getter
@Setter
@ToString
public class License {

    private int id;
    private String licenseId;
    private String description;
    private String organizationId;
    private String productName;
    private String licenseType;

}
