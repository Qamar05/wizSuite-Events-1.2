package com.wizsuite.event.retrofit

import com.wizsuite.event.model.EventRequest
import com.wizsuite.event.model.EventResponse
import com.wizsuite.event.model.LoginRequest
import com.wizsuite.event.model.LoginResponse
import com.wizsuite.event.model.SendLoginOTPRequest
import com.wizsuite.event.model.SendLoginOTPResponse
import com.wizsuite.event.model.SendOTPRequest
import com.wizsuite.event.model.SendOTPResponse
import com.wizsuite.event.model.SupportRequest
import com.wizsuite.event.model.SupportResponse
import com.wizsuite.event.model.UpdatePasswordRequest
import com.wizsuite.event.model.VerifyLoginOTPRequest
import com.wizsuite.event.model.VerifyOTPRequest
import com.wizsuite.event.model.VerifyOTPResponse
import com.wizsuite.event.utils.NetworkConst
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface RetrofitApi {
    @POST(NetworkConst.LOGIN)
    suspend fun requestLogin(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @POST(NetworkConst.REQUEST_OTP)
    suspend fun requestOTP(@Body request: SendOTPRequest): Response<SendOTPResponse>

    @POST(NetworkConst.VERIFY_OTP)
    suspend fun requestVerifyOTP(@Body request: VerifyOTPRequest): Response<VerifyOTPResponse>

    @POST(NetworkConst.UPDATE_PASSWORD)
    suspend fun requestUpdatePassword(@Body request: UpdatePasswordRequest): Response<SendOTPResponse>

    @POST(NetworkConst.UPCOMING_EVENT)
    suspend fun requestUpcomingEvents(@Body request: EventRequest): Response<EventResponse>

    @POST(NetworkConst.PAST_EVENT)
    suspend fun requestPastEvents(@Body request: EventRequest): Response<EventResponse>

    @POST(NetworkConst.SEND_SMS)
    suspend fun requestSendSms(@Body request: SendLoginOTPRequest): Response<SendLoginOTPResponse>

    @POST(NetworkConst.VERIFY_SMS_OTP)
    suspend fun requestVerifySmsOtp(@Body request: VerifyLoginOTPRequest): Response<LoginResponse>

    @POST(NetworkConst.DELETE_ACCOUNT)
    suspend fun requestDeleteAccount(@Body request: EventRequest): Response<SupportResponse>

    @POST(NetworkConst.SUPPORT)
    suspend fun requestSupport(@Body request: SupportRequest): Response<SupportResponse>


}