package com.demo.alexa.microservice.registration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class RegistrationServer {
	
	public static void main(String[] args) {
		if(args.length > 0 && args.length != 2){
			errorMessage();
			return;
		}else if(args.length == 2){
			System.setProperty("eureka.instance.hostname", args[0]);
			System.setProperty("server.port", args[1]);
		}

		System.setProperty("spring.config.name", "registration-server");
		SpringApplication.run(RegistrationServer.class, args);

	}
	
	protected static void errorMessage() {
		System.out.println("Usage: java -jar [jar file name] <host name> <port> OR");
		System.out.println("Usage: java -jar [jar file name]");
	}
}
