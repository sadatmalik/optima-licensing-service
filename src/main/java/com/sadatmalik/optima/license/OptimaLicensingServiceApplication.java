package com.sadatmalik.optima.license;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

/**
 * The bootstrap class for the entire microservice. Core initialization logic for the
 * service should be placed in this class.
 *
 * @author sadatmalik
 */
@SpringBootApplication
public class OptimaLicensingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OptimaLicensingServiceApplication.class, args);
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
