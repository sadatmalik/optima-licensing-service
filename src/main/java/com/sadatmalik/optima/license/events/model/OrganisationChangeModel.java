package com.sadatmalik.optima.license.events.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author sadatmalik
 */
@Getter
@Setter
@ToString
public class OrganisationChangeModel {
    private String type;
    private String action;
    private String organisationId;
    private String correlationId;

    public OrganisationChangeModel(){
        super();
    }

    public  OrganisationChangeModel(String type,
                                    String action,
                                    String organisationId,
                                    String correlationId) {
        super();
        this.type   = type;
        this.action = action;
        this.organisationId = organisationId;
        this.correlationId = correlationId;
    }
}