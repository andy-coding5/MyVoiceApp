package com.rohan.myvoice.Retrofit;

import com.rohan.myvoice.pojo.Forget_password_request.ForgetPasswordRequest;
import com.rohan.myvoice.pojo.Invitation_delete.InviteDelete;
import com.rohan.myvoice.pojo.delete_account.DeleteAccount;
import com.rohan.myvoice.pojo.invitation_accepted_list.InvitationList_accept_list;
import com.rohan.myvoice.pojo.Notification_details.Notifications;
import com.rohan.myvoice.pojo.Register.Register;
import com.rohan.myvoice.pojo.Response.response;
import com.rohan.myvoice.pojo.SignIn.Login;
import com.rohan.myvoice.pojo.activity_details.Activities;
import com.rohan.myvoice.pojo.citi_details.Cities;
import com.rohan.myvoice.pojo.country_details.Country;
import com.rohan.myvoice.pojo.education_details.Education;
import com.rohan.myvoice.pojo.gender_details.Gender;
import com.rohan.myvoice.pojo.invitation_details.Invite;
import com.rohan.myvoice.pojo.resent_otp.Data;
import com.rohan.myvoice.pojo.reset_password.ResetPassword;
import com.rohan.myvoice.pojo.salary_details.Salary;
import com.rohan.myvoice.pojo.state_details.States;
import com.rohan.myvoice.pojo.survey_details.Survey;
import com.rohan.myvoice.pojo.survey_questions_list.QuestionList;
import com.rohan.myvoice.pojo.update_profile.UpdateProfile;
import com.rohan.myvoice.pojo.user_profile_settings_page.UserProfile;
import com.rohan.myvoice.pojo.zip_details.Zip;

import org.json.JSONArray;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {

    // public static String token = null;

    //for sign In
    @Headers("APIKEY:6815ab00be4c46b597b1567db6cb3def")
    @POST("accounts/api/v1/user/login/")
    @FormUrlEncoded
    Call<Login> getLoginJason(@Field("email_or_phone") String email_or_phone, @Field("password") String password, @Field("DeviceToken") String DeviceToken,
                              @Field("Source") String Source, @Field("device_id") String device_id);


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

    @GET("mobileapp/api/v1/data/income/get/")
    Call<Salary> getSalaryJson(@Header("APIKEY") String APIKEY, @Header("Authorization") String Authorization);


    //for Zip Details

    @POST("mobileapp/api/v1/data/zipcode/")
    @FormUrlEncoded
    Call<Zip> getZipJason(@Header("APIKEY") String APIKEY, @Header("Authorization") String Authorization,
                          @Field("country") String country, @Field("state") String state,
                          @Field("city") String city, @Field("zipcode") String zipcode);

    @POST("mobileapp/api/v1/user/profile/update/")
    @FormUrlEncoded
    Call<UpdateProfile> getUpdateProfileJson(@Header("APIKEY") String APIKEY, @Header("Authorization") String Authorization,
                                             @Field("first_name") String first_name, @Field("last_name") String last_name,
                                             @Field("zipcode") String zipcode, @Field("country") String country,
                                             @Field("state") String state, @Field("city") String city,
                                             @Field("education") String education, @Field("gender") String gender,
                                             @Field("dob") String dob, @Field("nosurveysperday") String nosurveysperday,
                                             @Field("income") String income,
                                             @Field("DeviceToken") String DeviceToken, @Field("Source") String Source,
                                             @Field("device_id") String device_id, @Field("is_pushnotification") int is_pushnotification,
                                             @Field("is_complete") String is_complete);


    //only in preference screen
    @POST("mobileapp/api/v1/user/profile/update/")
    @FormUrlEncoded
    Call<UpdateProfile> setMyPrefJSON(@Header("APIKEY") String APIKEY, @Header("Authorization") String Authorization,
                                      @Field("first_name") String first_name, @Field("last_name") String last_name,
                                      @Field("zipcode") String zipcode, @Field("country") String country,
                                      @Field("state") String state, @Field("city") String city,
                                      @Field("education") String education, @Field("gender") String gender,
                                      @Field("dob") String dob, @Field("nosurveysperday") String nosurveysperday,
                                      @Field("income") String income,
                                      @Field("DeviceToken") String DeviceToken, @Field("Source") String Source,
                                      @Field("device_id") String device_id, @Field("is_pushnotification") int is_pushnotification,
                                      @Field("is_complete") String is_complete, @Field("firstTimeRegister") String firstTimeRegister);

    //for push icon_notification update in settings fragment
    @POST("mobileapp/api/v1/user/profile/update/")
    @FormUrlEncoded
    Call<UpdateProfile> getPushUpdateJson(@Header("APIKEY") String APIKEY, @Header("Authorization") String Authorization,
                                          @Field("DeviceToken") String DeviceToken, @Field("device_id") String device_id,
                                          @Field("Source") String Source, @Field("is_pushnotification") int is_pushnotification);


    //survey
    @GET("mobileapp/api/v1/data/project/get/list/")
    Call<Survey> getSurveyJson(@Header("APIKEY") String APIKEY, @Header("Authorization") String Authorization);

    //survey's questions list
    @GET("mobileapp/api/v1/data/questions/get/list/{path}")
    Call<QuestionList> getSurveyQuestionsListJson(@Header("APIKEY") String APIKEY, @Header("Authorization") String Authorization, @Path("path") String path);

    //clicked questions detail  --  SCQ and MCQ and RNK
    @GET("mobileapp/api/v1/data/questions/get/{path}/details")
    Call<com.rohan.myvoice.pojo.survey_question_detail_SCQ_MCQ_RNK.QuestionDetail> getSCQ_MCQ_RNKJson(@Header("APIKEY") String APIKEY, @Header("Authorization") String Authorization, @Path("path") String path);


    //clicked questions detail  --  OTT
    @GET("mobileapp/api/v1/data/questions/get/{path}/details")
    Call<com.rohan.myvoice.pojo.survey_question_detail_OTT.QuestionDetail> getOTTJson(@Header("APIKEY") String APIKEY, @Header("Authorization") String Authorization, @Path("path") String path);


    //clicked questions detail  --  OTN
    @GET("mobileapp/api/v1/data/questions/get/{path}/details")
    Call<com.rohan.myvoice.pojo.survey_question_detail_OTN.QuestionDetail> getOTNJson(@Header("APIKEY") String APIKEY, @Header("Authorization") String Authorization, @Path("path") String path);


    //clicked questions detail  --  SCL
    @GET("mobileapp/api/v1/data/questions/get/{path}/details")
    Call<com.rohan.myvoice.pojo.survey_question_detail_SCL.QuestionDetail> getSCLJson(@Header("APIKEY") String APIKEY, @Header("Authorization") String Authorization, @Path("path") String path);

    //for mcq submit
    @POST("mobileapp/api/v1/data/questions/response/submit/")
    @FormUrlEncoded
    Call<response> getMCQResponseJson(@Header("APIKEY") String APIKEY, @Header("Authorization") String Authorization,
                                      @Field("AttributeID") String AttributeID, @Field("QuestionID") String QuestionID,
                                      @Field("ParentID") String ParentID, @Field("Response") JSONArray Response,
                                      @Field("Source") String Source, @Field("MainParentID") String MainParentID);

    //Activity tab
    @GET("mobileapp/api/v1/data/questions/get/activities/")
    Call<Activities> getActivityJson(@Header("APIKEY") String APIKEY, @Header("Authorization") String Authorization);

    //icon_notification tab
    @GET("mobileapp/api/v1/notification/list/")
    Call<Notifications> getnotificationssJson(@Header("APIKEY") String APIKEY, @Header("Authorization") String Authorization);

    //accept the invitation
    @GET("mobileapp/api/v1/invitation/Accept/")
    Call<Notifications> getAcceptInvitationJson(@Header("APIKEY") String APIKEY, @Header("Authorization") String Authorization);

    //ignore the invitation
    @GET("mobileapp/api/v1/invitation/Ignore/")
    Call<Notifications> getIgnoreInvitationJson(@Header("APIKEY") String APIKEY, @Header("Authorization") String Authorization);

    //for Invite
    @POST("mobileapp/api/v1/invite/")
    @FormUrlEncoded
    Call<Invite> getInviteJson(@Header("APIKEY") String APIKEY, @Header("Authorization") String Authorization,
                               @Field("id") String id, @Field("action") String action, @Field("Source") String Source);

    //acceped invitaion list
    @GET("mobileapp/api/v1/invitation/Accept")
    Call<InvitationList_accept_list> getInvitaionList_accept_Json(@Header("APIKEY") String APIKEY, @Header("Authorization") String Authorization);

    //Pending invitaion list
    @GET("mobileapp/api/v1/invitation/Pending")
    Call<InvitationList_accept_list> getInvitaionList_pending_Json(@Header("APIKEY") String APIKEY, @Header("Authorization") String Authorization);

    //delete the accepted invitaion
    @GET("mobileapp/api/v1/invite/delete/{path}")
    Call<InviteDelete> getDelete_invitaitonJson(@Header("APIKEY") String APIKEY, @Header("Authorization") String Authorization, @Path("path") String path);

    //user profile get request used in settings fragment
    @GET("mobileapp/api/v1/user/profile/get/")
    Call<UserProfile> getUserProfile_json(@Header("APIKEY") String APIKEY, @Header("Authorization") String Authorization);

    //submit otp
    @POST("accounts/api/v1/user/account/confirm/")
    @FormUrlEncoded
    Call<Data> getSubmit_otp_request(@Header("APIKEY") String APIKEY, @Header("Authorization") String Authorization,
                                     @Field("otp") String otp, @Field("Source") String Source);  // otp , sopurce

    //resend otp
    @POST("accounts/api/v1/user/account/confirm/request/")
    @FormUrlEncoded
    Call<Data> getresend_otp_request(@Header("APIKEY") String APIKEY, @Header("Authorization") String Authorization,
                                     @Field("DeviceToken") String DeviceToken, @Field("device_id") String device_id,
                                     @Field("Source") String Source);

    //delete account
    @POST("accounts/api/v1/user/delete/request/")
    @FormUrlEncoded
    Call<DeleteAccount> getDelete_accountJson(@Header("APIKEY") String APIKEY, @Header("Authorization") String Authorization,
                                              @Field("DeviceToken") String DeviceToken, @Field("Source") String Source);

    //reset password
    @POST("accounts/api/v1/user/password/change/")
    @FormUrlEncoded
    Call<ResetPassword> getReset_passwordJson(@Header("APIKEY") String APIKEY, @Header("Authorization") String Authorization,
                                              @Field("Source") String Source, @Field("old_pass") String old_pass,
                                              @Field("new_pass") String new_pass, @Field("confirm_pass") String confirm_pass);

    //Forget password request
    @POST("accounts/api/v1/user/password/reset/request/")
    @FormUrlEncoded
    Call<ForgetPasswordRequest> getForget_passwordJson(@Header("APIKEY") String APIKEY,
                                                       @Field("Source") String Source, @Field("email") String email);

    //Forget password verify
    @POST("accounts/api/v1/user/password/reset/")
    @FormUrlEncoded
    Call<ForgetPasswordRequest> getForget_password_verifyJson(@Header("APIKEY") String APIKEY,
                                                              @Field("email") String email,
                                                              @Field("otp") String otp, @Field("new_pass") String new_pass,
                                                              @Field("Source") String Source);

}
