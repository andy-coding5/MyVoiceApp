package com.rohan.myvoice.pojo.SignIn;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Profile {

    @SerializedName("userid")
    @Expose
    private Integer userid;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("zipcode")
    @Expose
    private String zipcode;
    @SerializedName("education")
    @Expose
    private String education;
    @SerializedName("sex")
    @Expose
    private Object sex;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("dob")
    @Expose
    private String dob;
    @SerializedName("income")
    @Expose
    private String income;
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
    private String deviceToken;
    @SerializedName("Source")
    @Expose
    private String source;
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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public Object getSex() {
        return sex;
    }

    public void setSex(Object sex) {
        this.sex = sex;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getIncome() {
        return income;
    }

    public void setIncome(String income) {
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

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
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