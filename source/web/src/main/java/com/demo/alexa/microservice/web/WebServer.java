package com.demo.alexa.microservice.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(useDefaultFilters = false)
public class WebServer {
	
	public static final String PATIENT_SERVICE_URL = "http://patient-service";
	
	public static void main(String[] args) {
		if(args.length > 0 && args.length != 2){
			errorMessage();
			return;
		}else if(args.length == 2){
			System.setProperty("eureka.client.serviceUrl.defaultZone", args[0]);
			System.setProperty("server.port", args[1]);
		}
		System.setProperty("spring.config.name", "web-server");
		SpringApplication.run(WebServer.class, args);
	}
	
	@LoadBalanced
	@Bean
	RestTemplate restTemplate() {
		return new RestTemplate();
	}
	
	@Bean
	public WebPatientService patientService() {
		return new WebPatientService(PATIENT_SERVICE_URL);
	}
	
	@Bean
	public WebPatientController patientController() {
		return new WebPatientController(patientService());
	}
	
	protected static void errorMessage() {
		System.out.println("Usage: java -jar [jar file name] <registration service endpoint> <port> OR");
		System.out.println("Usage: java -jar [jar file name]");
	}
}
