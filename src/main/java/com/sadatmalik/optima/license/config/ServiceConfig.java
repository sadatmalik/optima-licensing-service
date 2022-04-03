package com.sadatmalik.optima.license.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * While it’s possible to directly inject configuration values into properties in individual
 * classes, it is useful to centralize all the configuration information into a single
 * configuration class and then inject the configuration class into where it’s needed.
 *
 * ServiceConfig is a simple class that contains the logic to retrieve the custom parameters
 * that we’ll define in the configuration file for the licensing service, including the
 * Redis host and port.
 *
 * @author sadatmalik
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "example")
public class ServiceConfig {

    private String property;

    @Value("${redis.server}")
    private String redisServer = "";

    @Value("${redis.port}")
    private String redisPort = "";
}
