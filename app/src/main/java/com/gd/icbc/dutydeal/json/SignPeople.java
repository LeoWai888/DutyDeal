package com.gd.icbc.dutydeal.json;


/**
 * userName : 张三
 * userNo : 001122
 * userPhoto : 112233
 */
public class SignPeople {

    /**
     * retCode : -1
     * result : 1
     * errorCode : $ErrorCode
     * userName :
     * sysTime : 2018-12-20 14:34:08
     * userPhoto :
     */

    private String retCode;
    private String result;
    private String errorCode;
    private String userName;
    private String sysTime;
    private String userPhoto;
    private String areaNo;

    public String getRetCode() {
        return retCode;
    }

    public void setRetCode(String retCode) {
        this.retCode = retCode;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSysTime() {
        return sysTime;
    }

    public void setSysTime(String sysTime) {
        this.sysTime = sysTime;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public String getAreaNo() {
        return areaNo;
    }

    public void setAreaNo(String areaNo) {
        this.areaNo = areaNo;
    }
}
