package com.sadatmalik.optima.license.config;

import org.keycloak.adapters.springsecurity.KeycloakSecurityComponents;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.client.KeycloakClientRequestFactory;
import org.keycloak.adapters.springsecurity.client.KeycloakRestTemplate;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

/**
 * Security config extend a KeycloakWebSecurityConfigurerAdapter and overrides the following
 * methods:
 *
 * - configure()
 * - configureGlobal()
 * - sessionAuthenticationStrategy()
 * - KeycloakConfigResolver() - @see KeycloakConfig
 *
 * The class must be marked with @Configuration. @EnableWebSecurity applies the configuration
 * to the global WebSecurity.
 *
 * @EnableGlobalMethodSecurity(jsr250Enabled=true) enables @RoleAllowed
 *
 * Access rules can range from coarse-grained (any authenticated user can access the entire
 * service) to fine-grained (only the application with this role, but accessing this URL
 * through a DELETE is allowed).
 *
 * Several of the more common access control rules include protecting a resource so that
 * - Only authenticated users can access a service URL.
 * - Only users with a specific role can access a service URL.
 *
 * All access rules are defined inside the configure() method. We’ll use the HttpSecurity
 * class passed in by Spring to define our rules. We restrict all access to any URL in the
 * organisation service to authenticated users only.
 *
 * If we are to access the organisation service without an access token present in the HTTP
 * header, we’ll get a 401 HTTP response code and a message indicating that full
 * authentication to the service is required.
 *
 * When we call the organisation service, we need to set the HTTP authorisation type to
 * Bearer Token with the access_token value.
 *
 * @author sm@creativefusion.net
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(jsr250Enabled = true)
@ComponentScan(basePackageClasses = KeycloakSecurityComponents.class)
public class SecurityConfig extends KeycloakWebSecurityConfigurerAdapter {

    @Autowired
    public KeycloakClientRequestFactory keycloakClientRequestFactory;

    /**
     * We need to ensure that the HTTP Authorization header is injected into the application
     * call to the organization service.
     *
     * Without Spring Security, we’d have to write a servlet filter to grab the HTTP header of
     * the incoming licensing service call and then manually add it to every outbound service
     * call in the licensing service. Keycloak provides a new REST template class that supports
     * these calls. The class is called KeycloakRestTemplate. To use this class, we first need
     * to expose it as a bean that can be autowired into a service calling another protected
     * service.
     *
     * @see com.sadatmalik.optima.license.service.client.OrganisationRestTemplateClient
     *
     * @return
     */
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public KeycloakRestTemplate keycloakRestTemplate() {
        return new KeycloakRestTemplate(keycloakClientRequestFactory);
    }

    /**
     * Registers the Keycloak authentication provider.
     *
     * The HttpSecurity object passed into the method configures all access rules. We will
     * protect the organization service so that it can only be accessed by an authenticated
     * user.
     *
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        http.authorizeRequests()
                .anyRequest().authenticated();
        http.csrf().disable();
    }

    /**
     * Defines the session authentication strategy.
     *
     * @param auth
     * @throws Exception
     */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        KeycloakAuthenticationProvider keycloakAuthenticationProvider =
                keycloakAuthenticationProvider();
        keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(
                new SimpleAuthorityMapper());
        auth.authenticationProvider(keycloakAuthenticationProvider);
    }

    /**
     * Defines the session authentication strategy.
     *
     * @return
     */
    @Bean
    @Override
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new RegisterSessionAuthenticationStrategy(
                new SessionRegistryImpl());
    }

}
