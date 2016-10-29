package com.canice.wristbandapp.model;

/**
 * 睡眠历史记录信息
 * @author canice_yuan
 *
 */
public class SleepRecordInfo {

	private int index;
	private String date;
	private String duration;
	
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
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	
}
