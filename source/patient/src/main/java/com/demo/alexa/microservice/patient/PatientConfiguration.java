package com.demo.alexa.microservice.patient;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

@Configuration
@ComponentScan
@EntityScan("com.demo.alexa.microservice.patient")
@EnableJpaRepositories("com.demo.alexa.microservice.patient")
@PropertySource("classpath:db-config.properties")
public class PatientConfiguration {
	protected Logger logger;

	public PatientConfiguration() {
		logger = Logger.getLogger(getClass().getName());
	}
	
	@Bean
	public DataSource dataSource() {
		logger.info("dataSource() invoked");

		DataSource dataSource = (new EmbeddedDatabaseBuilder()).addScript("classpath:db/schema.sql")
				.addScript("classpath:db/data.sql").build();

		logger.info("dataSource = " + dataSource);

		// Log data
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		List<Map<String, Object>> patients = jdbcTemplate.queryForList("SELECT name FROM PATIENT");
		logger.info("System has " + patients.size() + " patients");

		return dataSource;
	}
}
