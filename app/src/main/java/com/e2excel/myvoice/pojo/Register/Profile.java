package com.e2excel.myvoice.pojo.Register;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Profile {

    @SerializedName("userid")
    @Expose
    private Integer userid;
    @SerializedName("country")
    @Expose
    private Object country;
    @SerializedName("state")
    @Expose
    private Object state;
    @SerializedName("city")
    @Expose
    private Object city;
    @SerializedName("zipcode")
    @Expose
    private Object zipcode;
    @SerializedName("education")
    @Expose
    private Object education;
    @SerializedName("sex")
    @Expose
    private Object sex;
    @SerializedName("gender")
    @Expose
    private Object gender;
    @SerializedName("dob")
    @Expose
    private Object dob;
    @SerializedName("income")
    @Expose
    private Object income;
    @SerializedName("points")
    @Expose
    private Integer points;
    @SerializedName("nosurveysperday")
    @Expose
    private Integer nosurveysperday;
    @SerializedName("others")
    @Expose
    private Object others;
    @SerializedName("is_complete")
    @Expose
    private Boolean isComplete;
    @SerializedName("is_pushnotification")
    @Expose
    private Boolean isPushnotification;
    @SerializedName("DeviceToken")
    @Expose
    private Object deviceToken;
    @SerializedName("Source")
    @Expose
    private Object source;
    @SerializedName("countryname")
    @Expose
    private String countryname;
    @SerializedName("statename")
    @Expose
    private String statename;
    @SerializedName("gendername")
    @Expose
    private String gendername;
    @SerializedName("educationname")
    @Expose
    private String educationname;
    @SerializedName("isVerified")
    @Expose
    private Boolean isVerified;

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public Object getCountry() {
        return country;
    }

    public void setCountry(Object country) {
        this.country = country;
    }

    public Object getState() {
        return state;
    }

    public void setState(Object state) {
        this.state = state;
    }

    public Object getCity() {
        return city;
    }

    public void setCity(Object city) {
        this.city = city;
    }

    public Object getZipcode() {
        return zipcode;
    }

    public void setZipcode(Object zipcode) {
        this.zipcode = zipcode;
    }

    public Object getEducation() {
        return education;
    }

    public void setEducation(Object education) {
        this.education = education;
    }

    public Object getSex() {
        return sex;
    }

    public void setSex(Object sex) {
        this.sex = sex;
    }

    public Object getGender() {
        return gender;
    }

    public void setGender(Object gender) {
        this.gender = gender;
    }

    public Object getDob() {
        return dob;
    }

    public void setDob(Object dob) {
        this.dob = dob;
    }

    public Object getIncome() {
        return income;
    }

    public void setIncome(Object income) {
        this.income = income;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Integer getNosurveysperday() {
        return nosurveysperday;
    }

    public void setNosurveysperday(Integer nosurveysperday) {
        this.nosurveysperday = nosurveysperday;
    }

    public Object getOthers() {
        return others;
    }

    public void setOthers(Object others) {
        this.others = others;
    }

    public Boolean getIsComplete() {
        return isComplete;
    }

    public void setIsComplete(Boolean isComplete) {
        this.isComplete = isComplete;
    }

    public Boolean getIsPushnotification() {
        return isPushnotification;
    }

    public void setIsPushnotification(Boolean isPushnotification) {
        this.isPushnotification = isPushnotification;
    }

    public Object getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(Object deviceToken) {
        this.deviceToken = deviceToken;
    }

    public Object getSource() {
        return source;
    }

    public void setSource(Object source) {
        this.source = source;
    }

    public String getCountryname() {
        return countryname;
    }

    public void setCountryname(String countryname) {
        this.countryname = countryname;
    }

    public String getStatename() {
        return statename;
    }

    public void setStatename(String statename) {
        this.statename = statename;
    }

    public String getGendername() {
        return gendername;
    }

    public void setGendername(String gendername) {
        this.gendername = gendername;
    }

    public String getEducationname() {
        return educationname;
    }

    public void setEducationname(String educationname) {
        this.educationname = educationname;
    }

    public Boolean getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }

}