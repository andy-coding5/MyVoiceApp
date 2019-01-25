package com.rohan.myvoice.Retrofit;

import com.rohan.myvoice.pojo.Register.Register;
import com.rohan.myvoice.pojo.SignIn.Login;
import com.rohan.myvoice.pojo.citi_details.Cities;
import com.rohan.myvoice.pojo.country_details.Country;
import com.rohan.myvoice.pojo.education_details.Education;
import com.rohan.myvoice.pojo.gender_details.Gender;
import com.rohan.myvoice.pojo.state_details.States;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {

    // public static String token = null;

    //for sign In
    @Headers("APIKEY:6815ab00be4c46b597b1567db6cb3def")
    @POST("accounts/api/v1/user/login/")
    @FormUrlEncoded
    Call<Login> getLoginJason(@Field("email_or_phone") String email_or_phone, @Field("password") String password);


    //for sign Up
    @Headers("APIKEY:6815ab00be4c46b597b1567db6cb3def")
    @POST("accounts/api/v1/user/register/")
    @FormUrlEncoded
    Call<Register> getRegisterJason(@Field("email") String email, @Field("password") String password,
                                    @Field("first_name") String first_name, @Field("last_name") String last_name);


    //for country
    // @Headers({"APIKEY:6815ab00be4c46b597b1567db6cb3def","Authorization: Token 0604c253f8bd6ba9ccbf6ed470387c1f3ee77223"})
    /*@Headers({
            "APIKEY:6815ab00be4c46b597b1567db6cb3def",
            "Authorization:Token 0604c253f8bd6ba9ccbf6ed470387c1f3ee77223"
    })*/
    @GET("mobileapp/api/v1/data/countries/get/")
    Call<Country> getCountryJson(@Header("APIKEY") String APIKEY, @Header("Authorization") String Authorization);

    @GET("mobileapp/api/v1/data/{path}/states/get/")
    Call<States> getStateJson(@Header("APIKEY") String APIKEY, @Header("Authorization") String Authorization, @Path("path") String path);

    @GET("mobileapp/api/v1/data/{path1}/{path2}/cities/get/")
    Call<Cities> getCityJson(@Header("APIKEY") String APIKEY, @Header("Authorization") String Authorization, @Path("path1") String path1, @Path("path2") String path2);


    @GET("mobileapp/api/v1/data/gender/get/")
    Call<Gender> getGenderJson(@Header("APIKEY") String APIKEY, @Header("Authorization") String Authorization);


    @GET("mobileapp/api/v1/data/{path}/edu/get/")
    Call<Education> getEducationJson(@Header("APIKEY") String APIKEY, @Header("Authorization") String Authorization, @Path("path") String path);

}
