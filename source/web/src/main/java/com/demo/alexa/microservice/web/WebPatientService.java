package com.demo.alexa.microservice.web;


import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class WebPatientService {
	@Autowired
	@LoadBalanced
	protected RestTemplate restTemplate;

	protected String serviceUrl;

	protected Logger logger = Logger.getLogger(WebPatientService.class
			.getName());
	

	/**
	 * The serviceURL parameter corresponds to the patient service.
	 * @param String serviceURL
	 */
	public WebPatientService(String serviceURL) {
		this.serviceUrl = serviceURL.startsWith("http") ? serviceURL
				: "http://" + serviceURL;
	}
	
	/**
	 * Return a Patient object corresponding to the medical record number
	 *  
	 * @param String patientNumber: Patient's medical record number
	 * @return Patient
	 */
	public Patient findByNumber(String patientNumber) {
		logger.info("findByNumber() invoked: for " + patientNumber);
		return restTemplate.getForObject(serviceUrl + "/patient/bynumber/{patientNumber}",
				Patient.class, patientNumber);
	}

	/**
     * Return a list of patients based on partial name matching.
	 *  
	 * @param String name
	 * @return List<Patient>
	 */
	public List<Patient> findByNameContains(String name) {
		logger.info("findByNameContains() invoked:  for " + name);
		Patient[] patients = null;

		try {
			patients = restTemplate.getForObject(serviceUrl
					+ "/patient/byname/{name}", Patient[].class, name);
		} catch (HttpClientErrorException e) { 
		}

		if (patients == null || patients.length == 0)
			return null;
		else
			return Arrays.asList(patients);
	}
	
	/**
     * Return the list of patients who have at least one of their vitals is in
     * abnormal range.
     * 
	 * @return List<Patient>
	 */
	public List<Patient> getAbnormal() {
		logger.info("getAbnormal() invoked");
		Patient[] patients = null;
		try {
			patients = restTemplate.getForObject(serviceUrl
					+ "/patient/abnormal", Patient[].class);
		} catch (HttpClientErrorException e) { 
		}

		if (patients == null || patients.length == 0)
			return null;
		else
			return Arrays.asList(patients);
	}
	/**
     * Record vital measurements for a patient identified by its medical record number.
     * Return updated patient.
     *
     * @param patientNumber: medical record number
     * @param diasp: diastolic pressure to record
     * @param sysp: systolic pressure to record
     * @param pulse: pulse to record
     * @param temperature: temperature to record
	 *
	 * @return Patient
	 */
	public Patient setVitals(Integer patientNumber, String diasp,
			String sysp, String pulse, String temperature){
		logger.info("setVitals() invoked");
		
		return restTemplate.getForObject(serviceUrl
					+ "/patient/setVitals/" + patientNumber + "/"
					+ diasp + "/"
					+ sysp + "/"
					+ pulse + "/"
					+ temperature, Patient.class);
	}
}
