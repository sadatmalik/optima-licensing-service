package com.sadatmalik.optima.license;

import com.sadatmalik.optima.license.config.ServiceConfig;
import com.sadatmalik.optima.license.events.model.OrganisationChangeModel;
import com.sadatmalik.optima.license.utils.UserContextInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Collections;
import java.util.List;
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
 * The @EnableBinding(Sink.class) tells the application service to use Spring Cloud Stream
 * to bind to a message broker. Sink.class tells the service to the use the channels defined
 * in the Sink interface to listen for incoming messages.
 *
 * Spring Cloud Stream exposes a default channel on the Sink interface. This Sink interface
 * channel is called input and is used to listen for incoming messages.
 *
 * To communicate with a specific Redis instance, we’ll expose a JedisConnectionFactory class
 * as a Spring bean. Once we have a connection to Redis, we’ll use that connection to create
 * a Spring RedisTemplate object.
 *
 * @author sadatmalik
 */
@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
@EnableConfigurationProperties(value = ServiceConfig.class)
@RefreshScope
@EnableEurekaClient
@EnableDiscoveryClient
@EnableFeignClients
@EnableBinding(Sink.class)
public class OptimaLicensingServiceApplication {

	private final ServiceConfig serviceConfig;

	public static void main(String[] args) {
		SpringApplication.run(OptimaLicensingServiceApplication.class, args);
	}

	/**
	 * Creates the Load Balancer–backed Spring RestTemplate bean. Used by the
	 * service.client.OrganisationRestTemplateClient.
	 *
	 * We add a UserContextInterceptor to the RestTemplate.
	 *
	 * @return load balancer-backed rest template.
	 */
	@LoadBalanced
	@Bean
	public RestTemplate getRestTemplate() {
		RestTemplate template = new RestTemplate();
		List interceptors = template.getInterceptors();
		if (interceptors == null) {
			template.setInterceptors(
					Collections.singletonList(
							new UserContextInterceptor()));
		} else {
			interceptors.add(new UserContextInterceptor());
			template.setInterceptors(interceptors);
		}

		return template;
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

	/**
	 * Executes this method each time a message is received from the input channel.
	 *
	 * Spring Cloud Stream automatically deserializes the incoming message to a Java POJO
	 * called OrganisationChangeModel.
	 *
	 * @param orgChange
	 */
	@StreamListener(Sink.INPUT)
	public void loggerSink(OrganisationChangeModel orgChange) {
		log.debug("Received {} event for the organisation id {}",
				orgChange.getAction(), orgChange.getOrganisationId());
	}

	/**
	 * Sets up the database connection to the Redis server.
	 *
	 * @return
	 */
	@Bean
	JedisConnectionFactory jedisConnectionFactory() {
		String hostname = serviceConfig.getRedisServer();
		int port = Integer.parseInt(serviceConfig.getRedisPort()); RedisStandaloneConfiguration redisStandaloneConfiguration
				= new RedisStandaloneConfiguration(hostname, port);
		return new JedisConnectionFactory(redisStandaloneConfiguration);
	}

	/**
	 * Creates a RedisTemplate to carry out actions for our Redis server.
	 *
	 * @return
	 */
	@Bean
	public RedisTemplate<String, Object> redisTemplate() {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(jedisConnectionFactory());
		return template;
	}

}
