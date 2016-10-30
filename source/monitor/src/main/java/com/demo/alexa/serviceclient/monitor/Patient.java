package com.demo.alexa.serviceclient.monitor;

import java.io.Serializable;

public class Patient implements Serializable{
	private static final long serialVersionUID = 1L;
	protected Long id;
	protected String name;
	protected Integer number;
	protected Integer pulse;
	protected Integer temperature;
	protected Integer sysp;
	protected Integer diasp;
	
	protected Patient(){}
	
	public Long getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public Integer getNumber() {
		return number;
	}
	public Integer getPulse() {
		return pulse;
	}
	public Integer getTemperature() {
		return temperature;
	}
	public Integer getSysp() {
		return sysp;
	}
	public Integer getDiasp() {
		return diasp;
	}
	protected void setId(Long id) {
		this.id = id;
	}
	protected void setName(String name) {
		this.name = name;
	}
	protected void setNumber(Integer number) {
		this.number = number;
	}
	protected void setPulse(Integer pulse) {
		this.pulse = pulse;
	}
	protected void setTemperature(Integer temperature) {
		this.temperature = temperature;
	}
	protected void setSysp(Integer sysp) {
		this.sysp = sysp;
	}
	protected void setDiasp(Integer diasp) {
		this.diasp = diasp;
	}
	
	public String toString(){
		return name + " " + id;
	}
}
