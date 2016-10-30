package com.demo.alexa.microservice.patient;

import java.util.List;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractPatientControllerTests {
	@Autowired
	PatientController patientController;
	
	protected static final Integer PAIENTNUMBER = 1004;
	protected static final String PATIENTNAME = "Jane Doe";
	
	@Test
	public void validPatientID() {
		Logger.getGlobal().info("Start validPatientID test");
		Patient patient = patientController.byPatientNumber(PAIENTNUMBER);

		Assert.assertNotNull(patient);
		Assert.assertEquals(PAIENTNUMBER, patient.getNumber());
		Assert.assertEquals(PATIENTNAME, patient.getName());
		Logger.getGlobal().info("End validPatientID test");
	}
	
	@Test
	public void validPatientName(){
		Logger.getGlobal().info("Start validPatientName test");		
		List<Patient> patients = patientController.byName(PATIENTNAME);
		Assert.assertNotNull(patients);
		Assert.assertEquals(1, patients.size());
		
		
		Patient patient = patients.get(0);
		Assert.assertNotNull(patient);
		Assert.assertEquals(PAIENTNUMBER, patient.getNumber());
		Assert.assertEquals(PATIENTNAME, patient.getName());
		
		Logger.getGlobal().info("End validPatientName test");
	}

	/*
	@Test
	public void validAbnormalVitals(){
		Logger.getGlobal().info("Start validAbnormalVitals test");	
		List<Patient> patients = patientController.getAbnormal();
		Assert.assertEquals(new Integer(1),new Integer(patients.size()));
		for(Patient patient: patients){
			Assert.assertEquals(PATIENTID,patient.getNumber());
		}
		Logger.getGlobal().info("End validAbnormalVitals test");	
	}*/
	
	/*
	@Test
	public void setVitals(){
		String diasp = "72";
		String sysp = "112";
		String pulse = "80";
		String temp = "102";
		Logger.getGlobal().info("Start setVitals test");	
		patientController.setVitals(PATIENTID, diasp, sysp, pulse, temp);
		
		Patient patient = patientController.byPatientID(PATIENTID);

		Assert.assertNotNull(patient);
		Assert.assertEquals(patient.getDiasp().toString(),diasp);
		Assert.assertEquals(patient.getSysp().toString(),sysp);
		Assert.assertEquals(patient.getPulse().toString(),pulse);
		Assert.assertEquals(patient.getTemperature().toString(),temp);
		
		Logger.getGlobal().info("End setVitals test");
	}
	*/

}
