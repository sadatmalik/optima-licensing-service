package com.sadatmalik.optima.license.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * While it’s possible to directly inject configuration values into properties in individual
 * classes, it is useful to centralize all the configuration information into a single
 * configuration class and then inject the configuration class into where it’s needed.
 *
 * @author sadatmalik
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "example")
public class ServiceConfig {

    private String property;

}
