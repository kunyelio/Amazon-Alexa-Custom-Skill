package com.demo.alexa.microservice.patient;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PatientController {
	protected Logger logger = Logger.getLogger(PatientController.class
			.getName());
	protected PatientRepository patientRepository;
	
	
	@Autowired
	public PatientController(PatientRepository patientRepository) {
		this.patientRepository = patientRepository;

		logger.info("PatientRepository says system has "
				+ patientRepository.countPatients() + " patients");
	}
	
	/**
	 * Return a patient from database identified by its medical record number.
	 * 
	 * @param patientNumber: medical record number
	 * @return Patient
	 */
	@RequestMapping(value="/patient/bynumber/{patientNumber}", produces={"application/json"})
	public Patient byPatientNumber(@PathVariable("patientNumber") Integer patientNumber) {

		logger.info("patient-service byPatientID() invoked: " + patientNumber);
		Patient patient = patientRepository.findByNumber(patientNumber);
		logger.info("patient-service byPatientID() found: " + patient);

		if (patient == null){
			return Patient.noPatient();
		}
		else {
			return patient;
		}
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
     * @return Patient
	 * @param patientNumber: medical record number
	 * @param diasp: diastolic pressure to record
	 * @param sysp: systolic pressure to record
	 * @param pulse: pulse to record
	 * @param temperature: temperature to record
	 * @return Patient
	 */
	@RequestMapping(value="/patient/setVitals/{patientNumber}/{diasp}/{sysp}/{pulse}/{temperature}", produces={"application/json"})
	public Patient setVitals(@PathVariable("patientNumber") Integer patientNumber,
		@PathVariable("diasp") String diasp,
		@PathVariable("sysp") String sysp,
		@PathVariable("pulse") String pulse,
		@PathVariable("temperature") String temperature){
		logger.info("patient-service setVitals() invoked: "
				+ patientRepository.getClass().getName() + " for patientNumber = "
				+ patientNumber + " diasp = " + diasp + " sysp = " + sysp + " pulse = " + pulse 
				+ " temperature = " + temperature);
		
		if(patientRepository.setPatientVitals(patientNumber, Integer.decode(diasp), Integer.decode(sysp), 
				Integer.decode(pulse), Integer.decode(temperature)) == 1){
			return patientRepository.findByNumber(patientNumber);
		}
		else{
			return Patient.noPatient();
		}
	}
	
	/**
	 * Return a list of patients based on partial name matching.
	 * 
	 * @param partialName
	 * @return List<Patient>
	 */
	@RequestMapping(value="/patient/byname/{name}", produces={"application/json"})
	public List<Patient> byName(@PathVariable("name") String partialName) {
		logger.info("patient-service byName() invoked: "
				+ patientRepository.getClass().getName() + " for "
				+ partialName);

		List<Patient> patients = patientRepository.findByNameContainingIgnoreCase(partialName);
				
		logger.info("patient-service byName() found: " + patients);

		if (patients == null || patients.size() == 0)
			return Patient.noPatients();
		else {
			return patients;
		}
	}
	
	
	/**
	 * Return the list of patients who have at least one of their vitals is in
	 * abnormal range.
	 * 
	 * @return List<Patient>
	 */
	@RequestMapping(value="/patient/abnormal", produces={"application/json"})
	public List<Patient> getAbnormal() {
		logger.info("patient-service getAbnormal() invoked:"
				+ patientRepository.getClass().getName());

		List<Patient> patients = patientRepository.getAbnormalVitals();
				
		logger.info("patient-service getAbnormal() found: " + patients);

		if (patients == null || patients.size() == 0)
			return Patient.noPatients();
		else {
			return patients;
		}
	}
}
