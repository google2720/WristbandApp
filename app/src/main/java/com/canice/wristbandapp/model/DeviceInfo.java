package com.canice.wristbandapp.model;


/**
 * 蓝牙设备信息
 * @author canice_yuan
 *
 */
public class DeviceInfo {

	private String deviceId;
	private String deviceName;
	private String deviceMacAdd;
	
	
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	public String getDeviceMacAdd() {
		return deviceMacAdd;
	}
	public void setDeviceMacAdd(String deviceMacAdd) {
		this.deviceMacAdd = deviceMacAdd;
	}
	
}
