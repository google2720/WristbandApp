package com.canice.wristbandapp.model;

/**
 * 运动历史记录信息
 * @author canice_yuan
 *
 */
public class SportRecordInfo {

	private int index;
	private String date;
	private String step;
	private String energy;
	
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getStep() {
		return step;
	}
	public void setStep(String step) {
		this.step = step;
	}
	public String getEnergy() {
		return energy;
	}
	public void setEnergy(String energy) {
		this.energy = energy;
	}
	
	
}
