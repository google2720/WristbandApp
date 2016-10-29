package com.canice.wristbandapp.model;

/**
 * 注册返回信息
 * @author canice_yuan
 *
 */
public class RegisterResponseInfo extends ResponseInfo{

	private String userid;
	
	public String getUserid() {
		return userid;
	}
	
	public void setUserid(String userid) {
		this.userid = userid;
	}
}
