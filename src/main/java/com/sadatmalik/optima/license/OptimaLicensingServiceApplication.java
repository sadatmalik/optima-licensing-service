package com.sadatmalik.optima.license;

import com.sadatmalik.optima.license.config.ServiceConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

/**
 * The bootstrap class for the entire microservice. Core initialization logic for the
 * service should be placed in this class.
 *
 * Spring Boot Actuator offers a @RefreshScope annotation that allows a development team to
 * access a /refresh endpoint that will force the Spring Boot application to re-read its
 * application configuration.
 *
 * This annotation only reloads the custom Spring properties you have in your application
 * configuration - items like database configuration used by Spring Data won’t be reloaded by
 * this annotation.
 *
 * One technique to handle application configuration refresh events is to refresh the
 * application properties in Spring Cloud Config. Then use a simple script to query the
 * service discovery engine to find all instances of a service and call the /refresh endpoint
 * directly.
 *
 * The @EnableDiscoveryClient is the trigger for Spring Cloud to enable the application to
 * use the Discovery Client and the Spring Cloud Load Balancer libraries.
 *
 * The @EnableFeignClients annotation is needed to use the Feign client in your code. One
 * wouldn't typically have both Feign and Discovery clients enabled.
 *
 * @author sadatmalik
 */
@SpringBootApplication
@EnableConfigurationProperties(value = ServiceConfig.class)
@RefreshScope
@EnableEurekaClient
@EnableDiscoveryClient
@EnableFeignClients
public class OptimaLicensingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OptimaLicensingServiceApplication.class, args);
	}

	/**
	 * Creates the Load Balancer–backed Spring RestTemplate bean. Used by the
	 * service.client.OrganisationRestTemplateClient.
	 *
	 * @return load balancer-backed rest template.
	 */
	@LoadBalanced
	@Bean
	public RestTemplate getRestTemplate(){
		return new RestTemplate();
	}

	/**
	 * LocaleResolver with UK as the default locale.
	 *
	 * @return LocaleResolver for internationalisation
	 */
	@Bean
	public LocaleResolver localeResolver() {
		SessionLocaleResolver localeResolver = new SessionLocaleResolver();
		localeResolver.setDefaultLocale(Locale.UK);
		return localeResolver;
	}

	/**
	 * Configures a ResourceBundleSource for internationalisation.
	 *
	 * Configured to not throw an error if a message isn’t found, instead it returns
	 * the message code.
	 *
	 * And sets the base name of the languages properties files. For example, if we
	 * were in Italy, we would use the Locale.IT, and we would have a file called
	 * messages_it.properties.
	 *
	 * In case we don’t find a message in a specific language, the message source
	 * will search on the default message file called messages.properties.
	 *
	 * @return ResourceBundleSource
	 */
	@Bean
	public ResourceBundleMessageSource messageSource() {
		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
		messageSource.setUseCodeAsDefaultMessage(true);
		messageSource.setBasenames("messages");
		return messageSource;
	}
}
