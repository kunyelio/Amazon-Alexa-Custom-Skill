package com.demo.alexa.microservice.patient;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;

@EnableAutoConfiguration
@EnableDiscoveryClient
@Import(PatientConfiguration.class)
public class PatientServer {
	
	@Autowired
	protected PatientRepository patientRepository;

	protected Logger logger = Logger.getLogger(PatientServer.class.getName());
	
	public static void main(String[] args) {
		if(args.length > 0 && args.length != 2){
			errorMessage();
			return;
		}else if(args.length == 2){
			System.setProperty("eureka.client.serviceUrl.defaultZone", args[0]);
			System.setProperty("server.port", args[1]);
		}
		System.setProperty("spring.config.name", "patient-server");
		SpringApplication.run(PatientServer.class, args);
	}
	
	protected static void errorMessage() {
		System.out.println("Usage: java -jar [jar file name] <registration service endpoint> <port> OR");
		System.out.println("Usage: java -jar [jar file name]");
	}
}
