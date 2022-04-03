package com.sadatmalik.optima.license.config;

import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * KeycloackConfigResolver is maintained in this separate configuration class.
 *
 * @see <a href="https://stackoverflow.com/questions/57787768/example-keycloak-spring-boot-app-fails-to-find-bean-keycloakspringbootconfigreso">...</a>
 *
 * @author sm@creativefusion.net
 */
@Configuration
public class KeycloakConfig {

    /**
     * By default, the Spring Security Adapter looks for a keycloak.json file.
     *
     * @return
     */
    @Bean
    public KeycloakSpringBootConfigResolver keycloakConfigResolver() {
        return new KeycloakSpringBootConfigResolver();
    }
}