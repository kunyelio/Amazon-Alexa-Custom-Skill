package com.demo.alexa.microservice.web;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class WebPatientController {

	@Autowired
	protected WebPatientService patientService;
	
	protected Logger logger = Logger.getLogger(WebPatientController.class
			.getName());

	public WebPatientController(WebPatientService patientService) {
		this.patientService = patientService;
	}
	
	/**
	 * Return a Patient object corresponding to the medical record number
	 *  
	 * @param String patientNumber: Patient's medical record number
	 * @return Patient
	 */
	@RequestMapping(value="/patient/bynumber/{patientNumber}", produces={"application/json"})
	public Patient byPNumber(@PathVariable("patientNumber") String patientNumber) {
		logger.info("web-service byPNumber() called by: " + patientNumber);
		Patient patient = patientService.findByNumber(patientNumber);
		logger.info("web-service byPNumber() found: " + patient);
		return patient;
	}

	/**
     * Return a list of patients based on partial name matching.
	 *  
	 * @param String partialName
	 * @return List<Patient>
	 */
	@RequestMapping(value="/patient/byname/{name}", produces={"application/json"})
	public List<Patient> byName(@PathVariable("name") String partialName) {
		logger.info("web-service byName() called by: " + partialName);
		List<Patient> patients = patientService.findByNameContains(partialName);
		logger.info("web-service byName() found: " + patients);
		return patients;
		
	}
	
	/**
     * Return the list of patients who have at least one of their vitals is in
     * abnormal range.
     * 
	 * @return List<Patient>
	 */
	@RequestMapping(value="/patient/abnormal", produces={"application/json"})
	public List<Patient> abnormalVitals(){
		logger.info("web-service abnormalVitals() called");
		List<Patient> patients = patientService.getAbnormal();
		logger.info("web-service abnormalVitals() found: " + patients.size() + " patients");
		return patients;
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
	@RequestMapping(value="/patient/setVitals/{patientNumber}/{diasp}/{sysp}/{pulse}/{temperature}", produces={"application/json"})
	public Patient setVitals(@PathVariable("patientNumber") Integer patientNumber,
			@PathVariable("diasp") String diasp,
			@PathVariable("sysp") String sysp,
			@PathVariable("pulse") String pulse,
			@PathVariable("temperature") String temperature){
		logger.info("web-service setVitals() called");
		return patientService.setVitals(patientNumber, diasp, sysp, pulse, temperature);
	}
}
