package com.rohan.myvoice;

import com.rohan.myvoice.pojo.Register.Register;
import com.rohan.myvoice.pojo.SignIn.Login;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {

    //for sign In
    @Headers("APIKEY:6815ab00be4c46b597b1567db6cb3def")
    @POST("user/login/")
    @FormUrlEncoded
    Call<Login> getLoginJason(@Field("email_or_phone") String email_or_phone, @Field("password") String password);


    //for sign Up
    @Headers("APIKEY:6815ab00be4c46b597b1567db6cb3def")
    @POST("user/register/")
    @FormUrlEncoded
    Call<Register> getRegisterJason(@Field("email") String email, @Field("password") String password,
                                 @Field("first_name") String first_name, @Field("last_name") String last_name);

}
