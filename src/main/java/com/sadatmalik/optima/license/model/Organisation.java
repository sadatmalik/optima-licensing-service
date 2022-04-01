package com.sadatmalik.optima.license.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.hateoas.RepresentationModel;

/**
 * @author sm@creativefusion.net
 */
@Getter
@Setter
@ToString
public class Organisation extends RepresentationModel<Organisation> {
    String id;
    String name;
    String contactName;
    String contactEmail;
    String contactPhone;
}
