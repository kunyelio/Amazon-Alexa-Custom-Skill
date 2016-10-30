package com.demo.alexa.microservice.patient;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;

public interface PatientRepository extends Repository<Patient, Long>{
	
	// Return a patient based on its medical record number
	public Patient findByNumber(Integer patientNumber);
	
	// Return a list of patients based on name matching
	public List<Patient> findByNameContainingIgnoreCase(String partialName);
	
	// Update a particular patient's vital measurements; patient is identified by
	// its medical record number
	@Transactional
	@Modifying
	@Query("update Patient p set p.diasp = ?2, p.sysp = ?3, p.pulse = ?4, p.temperature = ?5 where p.number = ?1")
	public int setPatientVitals(Integer patientNumber, Integer diasp, Integer sysp, Integer pulse, Integer temperature);
	
	// Return #patients in database
	@Query("SELECT count(*) from Patient")
	public int countPatients();
	
	// Return the list of patients who have at least one of their vitals is in
	// abnormal range
	@Query("SELECT p from Patient p where DIASP > 80 or SYSP > 120 or PULSE < 60 or PULSE > 100 or TEMPERATURE < 96 or TEMPERATURE > 100")
	public List<Patient> getAbnormalVitals();
}
