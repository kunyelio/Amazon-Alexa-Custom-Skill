package com.demo.alexa.microservice.patient;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;


public class PatientControllerTests extends AbstractPatientControllerTests {
	protected static final Patient thePatient = new Patient(PATIENTNAME ,PAIENTNUMBER, 75, 98, 110, 70);

	protected static class TestPatientRepository implements PatientRepository {
		
		

		@Override
		public Patient findByNumber(Integer patientNumber) {
			if (patientNumber.equals(PAIENTNUMBER))
				return thePatient;
			else
				return null;
		}

		@Override
		public List<Patient> findByNameContainingIgnoreCase(String partialName) {
			List<Patient> patients = new ArrayList<Patient>();

			if (PATIENTNAME.toLowerCase().indexOf(partialName.toLowerCase()) != -1)
				patients.add(thePatient);

			return patients;
		}

		@Override
		public int countPatients() {
			return 1;
		}

		@Override
		public int setPatientVitals(Integer patientNumber, Integer diasp,
				Integer sysp, Integer pulse, Integer temperature) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public List<Patient> getAbnormalVitals() {
			// TODO Auto-generated method stub
			return null;
		}

	}
	
	protected TestPatientRepository testRepo = new TestPatientRepository();
	
	@Before
	public void setup() {
		patientController = new PatientController(testRepo);
	}

}
