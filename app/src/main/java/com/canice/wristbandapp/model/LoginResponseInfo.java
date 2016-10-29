package com.canice.wristbandapp.model;

/**
 * 登录返回信息
 * @author canice_yuan
 *
 */
public class LoginResponseInfo extends ResponseInfo{

	private UserInfo userInfo;
	
	public UserInfo getUserInfo() {
		return userInfo;
	}
	
	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}
	
}
