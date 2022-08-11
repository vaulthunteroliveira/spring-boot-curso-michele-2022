package com.api.parkingcontrol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class ParkingControlApplication {

	public static final Logger LOGGER = LoggerFactory.getLogger(ParkingControlApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ParkingControlApplication.class, args);
		LOGGER.info(String.format("Senha:\t %s",  new BCryptPasswordEncoder().encode("senha123")));
	}

}
