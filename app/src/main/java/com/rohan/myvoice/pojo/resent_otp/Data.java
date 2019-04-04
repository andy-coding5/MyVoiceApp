package com.rohan.myvoice.pojo.resent_otp;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private com.rohan.myvoice.pojo.SignIn.Data data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public com.rohan.myvoice.pojo.SignIn.Data getData() {
        return data;
    }

    public void setData(com.rohan.myvoice.pojo.SignIn.Data data) {
        this.data = data;
    }
}
