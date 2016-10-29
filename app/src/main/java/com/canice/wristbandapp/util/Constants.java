package com.canice.wristbandapp.util;

/**
 * 定义常量
 * @author canice_yuan
 *
 */
public class Constants {
	
	public static final String QUIT_ALL_ACTIVTY = "com.canice.wristbandapp.action.QUIT_ALL_ACTIVTY";
	public static final String QUIT_ONE_ACTIVTY = "com.canice.wristbandapp.action.QUIT_ONE_ACTIVTY";
	
	// save user login paramss
	public static final String LOGIN_STATE = "login_state";
	public static final String SHARED_NAME = "sahred_name";
	public static final String USER_NAME = "user_name";
	public static final String USER_PASSWORLD = "user_passworld";

	public static final String PORFILE_SEX = "porfile_sex";
	public static final int PORFILE_SEX_MAN = 0;
	public static final int PORFILE_SEX_WOMAN = 1;
	public static final String USER_SEX_MAN = "男";
	public static final String USER_SEX_WOMAN = "女";
	
	public static final String DEVICE_ACTION = "device_action";
	public static final int DEVICE_ACTION_REMOVE = 0;
	public static final int DEVICE_ACTION_ADD = 1;
	
	public static final int RECORD_DATA_SPORT = 1;
	public static final int RECORD_DATA_SLEEP = 2;
	
	public static final String RETCODE_SUCCESS = "000";
	public static final String RETCODE_FAILURE = "001";
	public static final String RETCODE_KICKED="002";
	
	public static final String RANK_STARTDATE="startDate";
	public static final String RANK_ENDDATE="endDate";
	
	//Server Address
	public static final String SERVER_IP = "http://www.bcdest.com:8080";
	public static final String SERVER_REGISTER = SERVER_IP + "/rest/ring/regist?";
	public static final String SERVER_LOGIN = SERVER_IP + "/rest/ring/login?";
	public static final String SERVER_FIND_PASSWORD = SERVER_IP + "/rest/ring/forget-password?";
	public static final String SERVER_LOGIN_SUSTAIN = SERVER_IP + "";
	public static final String SERVER_LOGOUT = SERVER_IP + "";
	public static final String SERVER_SEND_CODE = SERVER_IP + "/rest/ring/send-msg?";
	public static final String SERVER_EDIT_USER = SERVER_IP + "/rest/ring/eidt-user-info?";
	public static final String SERVER_BIND_DIVICE = SERVER_IP + "rest/ring/bind-ex-device?";
	public static final String SERVER_UNBIND_DIVICE = SERVER_IP + "/rest/ring/bind-ex-device-remove?";
	public static final String SERVER_SAVE_STEPNUM = SERVER_IP + "/rest/ring/save-step-data?";
	public static final String SERVER_SAVE_SLEEPDATA = SERVER_IP + "/rest/ring/save-sleep-data?";
	public static final String SERVER_SAVE_USERHEAD = SERVER_IP + "/rest/ring/set-head-image?";
	public static final String SERVER_GET_USERHEAD = SERVER_IP + "/rest/ring/get-head-image?";
	public static final String SERVER_RELATE_USERINFO = SERVER_IP + "/rest/ring/relate-user-info?";
	public static final String SERVER_CREATE_EXDEVICE_ID = SERVER_IP + "/rest/ring/create-exdevice-id";
	public static final String SERVER_RANK = SERVER_IP + "/rest/ring/get-rank-list-info";
	public static final String SERVER_RELATE_USER = SERVER_IP + "/rest/ring/relate-user-info";
	public static final String SERVER_GET_STEP_DATA = SERVER_IP + "/rest/ring/get-step-data";
	public static final String SERVER_GET_SLEEP_DATA = SERVER_IP + "/rest/ring/get-sleep-data";
	public static final String SERVER_UPLOAD_STEP_DATA = SERVER_IP + "/rest/ring/save-many-step-data";
	public static final String SERVER_UPLOAD_SLEEP_DATA = SERVER_IP + "/rest/ring/save-many-sleep-data";
	/*
	 * Server Parames
	 */
	public static final String EMAIL = "email";
	public static final String MOBILE = "mobile";
	public static final String PASSWORD = "password";
	public static final String DEVICEID = "deviceId";
	public static final String EXDEVICEID = "exDeviceId";
	public static final String INVITE_CODE = "inviteCode";
	public static final String TOKEN = "token";
	public static final String MSGCODE = "msgCode";
	public static final String NICKNAME = "nickName";
	public static final String CPASSWORD = "confirmPassword";
	public static final String RETCODE = "retCode";
	public static final String RETMSG = "retMsg";
	public static final String USERID = "userid";
	public static final String USERINFO = "userInfo";
	public static final String HIGH = "high";
	public static final String WEIGHT = "weight";
	public static final String AGE = "age";
	public static final String SEX = "sex";
	public static final String ID = "id";
	public static final String STEPLONG = "stepLong";
	public static final String RANKINDOS = "rankInfos";
	public static final String GOAL = "goal";
	public static final String CALL_REMIND = "callRemind";
	public static final String SMS_REMIND = "smsRemind";
	public static final String WE_CHAT_REMIND = "weChatRemind";
	public static final String QQ_REMIND = "qqRemind";

	public static final String SOS = "sos";
    public static final String SOS_PHONE = "sos_phone";
    public static final String ANTI_LOST = "anti_lost";
    public static final String ANTI_LOST_VALUE = "anti_lost_value";

	public static final String RECORDDATE = "recordDate";
	public static final String STEPNUM = "stepNum";
	
	public static final String QSMTIME = "qsmTime";
	public static final String SSMTIME = "ssmTime";
	public static final String SLEEPDATE = "sleepDate";
	public static final String HEADIMAGE = "headImage";
	public static final String INVITECODE = "inviteCode";
	
	public static final String STEP_INFOS = "stepInfos";
	public static final String SLEEP_INFOS = "sleepInfos";
	
	public static final int IMAGE_REQUEST_CODE = 20001;
	public static final int MSG_SAVE_USERHEAD = 0x20002;
	
    public static final int requestCode_social=100;
    public static final int resultCode_social=101;

	public static final String SOS_SMS = "sos_sms";
    // APP_ID 替换为你的应用从官方网站申请到的合法appId
   // public static final String APP_ID = "wx5ac8c621e7ca98a9";//未签名App_Id

	public static final String APP_ID = "wxc3d7ba3b7854507a";
	public static final String QQ_APP_ID = "1105459075";

	public static final String LANGUAGE = "language";

	public static final String CHANGE_LANGUAGE = "change_language";


}
