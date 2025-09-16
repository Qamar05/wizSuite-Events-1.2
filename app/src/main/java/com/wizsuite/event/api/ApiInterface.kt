package com.wizsuite.event.api

import com.wizsuite.event.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap
import java.util.HashMap

interface ApiInterface {

    @Multipart
    @POST("mobileguest.php")
    fun registerUserRequest(@Part filePart: MultipartBody.Part, @PartMap map:HashMap<String,RequestBody>): Call<RegistrationResponse>

    @POST("loginAPI.php")
    fun loginRequest(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @POST("updatedevicetoken.php")
    fun upDateDeviceTokenRequest(@Body updateTokenRequest: UpdateTokenRequest): Call<UpdateTokenResponse>

    @POST("forgotpasswordAPI.php")
    fun sendOTPRequest(@Body sendOTPRequest: SendOTPRequest): Call<SendOTPResponse>

    @POST("otpverifyAPi.php")
    fun verifyOTPRequest(@Body verifyOTPRequest: VerifyOTPRequest): Call<VerifyOTPResponse>

    @POST("sendsms.php")
    fun sendLoginOTPRequest(@Body sendOTPRequest: SendLoginOTPRequest): Call<SendLoginOTPResponse>

    @POST("smslogin.php")
    fun verifyLoginOTPRequest(@Body verifyOTPRequest: VerifyLoginOTPRequest): Call<LoginResponse>

    @POST("updatepasswordAPI.php")
    fun updatePasswordRequest(@Body updatePasswordRequest: UpdatePasswordRequest): Call<SendOTPResponse>

    @POST("upcominguserAPI.php")
    fun upComingEventRequest(@Body eventRequest: EventRequest): Call<EventResponse>

    @POST("oldeventsuserAPI.php")
    fun pastEventRequest(@Body eventRequest: EventRequest): Call<EventResponse>

    @POST("getallDataFromAssignId.php")
    fun getFlightDetailsRequest(@Body eventAmenityRequest: EventAmenityRequest): Call<FlightDetailResponse>

    @POST("getallDataFromAssignId.php")
    fun getCabDetailsRequest(@Body eventAmenityRequest: EventAmenityRequest): Call<CabDetailsResponse>

    @POST("getallDataFromAssignId.php")
    fun getAssistanceTeamRequest(@Body eventAmenityRequest: EventAmenityRequest): Call<AssistantDetailResponse>

    @POST("supportAPI.php")
    fun supportRequest(@Body supportRequest: SupportRequest): Call<SupportResponse>

    @POST("postnotification.php")
    fun getNotificationRequest(@Body eventRequest: EventRequest): Call<NotificationResponse>

    @POST("deleteuser.php")
    fun deleteUserRequest(@Body eventRequest: EventRequest): Call<SupportResponse>


}