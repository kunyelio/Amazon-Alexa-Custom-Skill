package com.demo.alexa.microservice.patient;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "PATIENT")
public class Patient implements Serializable {
 
	private static final long serialVersionUID = 1L;
	
	public static Long nextId = 0L;
	
	@Id
	protected Long id;
	
	@Column(name = "name")
	protected String name;
	
	@Column(name = "number")
	protected Integer number;
	
	@Column(name = "pulse")
	protected Integer pulse;
	
	@Column(name = "temperature")
	protected Integer temperature;
	
	@Column(name = "sysp")
	protected Integer sysp;
	
	@Column(name = "diasp")
	protected Integer diasp;
	
	protected static Long getNextId() {
		synchronized (nextId) {
			return nextId++;
		}
	}
	
	protected Patient(){
	}

	public Patient(String name, Integer number, Integer pulse,
			Integer temperature, Integer sysp, Integer diasp){
		id = getNextId();
		this.name = name;
		this.number = number;
		this.pulse = pulse;
		this.temperature = temperature;
		this.sysp = sysp;
		this.diasp = diasp;
	}

	public long getId() {
		return id;
	}

	protected void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	protected void setName(String name) {
		this.name = name;
	}

	public Integer getNumber() {
		return number;
	}

	protected void setNumber(Integer number) {
		this.number = number;
	}

	public Integer getPulse() {
		return pulse;
	}

	protected void setPulse(Integer pulse) {
		this.pulse = pulse;
	}

	public Integer getTemperature() {
		return temperature;
	}

	protected void setTemperature(Integer temperature) {
		this.temperature = temperature;
	}

	public Integer getSysp() {
		return sysp;
	}

	protected void setSysp(Integer sysp) {
		this.sysp = sysp;
	}

	public Integer getDiasp() {
		return diasp;
	}

	protected void setDiasp(Integer diasp) {
		this.diasp = diasp;
	}


	protected static Patient noPatient(){
		Patient p = new Patient();
		p.id = -1l;
		return p;
	}
	
	protected static List<Patient> noPatients(){
		ArrayList<Patient> patients = new ArrayList<Patient>();
		patients.add(noPatient());
		return patients;
	}
	
}
