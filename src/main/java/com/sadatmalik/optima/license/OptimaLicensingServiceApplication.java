package com.sadatmalik.optima.license;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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

}
