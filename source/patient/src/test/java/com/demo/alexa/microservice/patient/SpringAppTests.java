package com.demo.alexa.microservice.patient;

import org.junit.runner.RunWith;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@SpringBootApplication
@Import(PatientConfiguration.class)
class PatientMain {
	public static void main(String[] args) {
		System.setProperty("spring.config.name", "patient-server");
		SpringApplication.run(PatientMain.class, args);
	}
}

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = PatientMain.class)
public class SpringAppTests  extends AbstractPatientControllerTests {

}
