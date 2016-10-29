package com.canice.wristbandapp.model;

/**
 * 排名界面个人信息
 * @author canice_yuan
 *
 */
public class RangeInfo implements Comparable<RangeInfo> {

	private String userName;
	String userId;
	private String portraitUrl;
	private int range;
	private int stepNo;
	private String sumSteps;
	
	/**
     * 获取sumSteps
     * @return sumSteps sumSteps
     */
    public String getSumSteps() {
        return sumSteps;
    }
    /**
     * 获取userName
     * @return userName userName
     */
    public String getUserName() {
        return userName;
    }
    /**
     * 设置userName
     * @param userName userName
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }
    /**
     * 获取userId
     * @return userId userId
     */
    public String getUserId() {
        return userId;
    }
    /**
     * 设置userId
     * @param userId userId
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }
    /**
     * 设置sumSteps
     * @param sumSteps sumSteps
     */
    public void setSumSteps(String sumSteps) {
        this.sumSteps = sumSteps;
    }
   
	public String getPortraitUrl() {
		return portraitUrl;
	}
	public void setPortraitUrl(String portraitUrl) {
		this.portraitUrl = portraitUrl;
	}
	public int getRange() {
		return range;
	}
	public void setRange(int range) {
		this.range = range;
	}
	public int getStepNo() {
		return stepNo;
	}
	public void setStepNo(int stepNo) {
		this.stepNo = stepNo;
	}

    @Override
    public int compareTo(RangeInfo another) {
        // TODO Auto-generated method stub
        int sort=this.sumSteps.compareTo(another.sumSteps);
        sort=-sort;
       return sort;
    }

	
	
}
