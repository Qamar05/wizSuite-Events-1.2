package com.wizsuite.event.ui.activities.verify_otp

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wizsuite.event.model.SendOTPRequest
import com.wizsuite.event.model.SendOTPResponse
import com.wizsuite.event.model.VerifyOTPRequest
import com.wizsuite.event.model.VerifyOTPResponse
import com.wizsuite.event.retrofit.ApiService
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

class VerifyOtpViewModel : ViewModel() {
    private val apiService = ApiService.getNetworkService()
    private var job: Job? = null

    val verifyOTPResponse = MutableLiveData<VerifyOTPResponse>()
    val sendOTPResponse = MutableLiveData<SendOTPResponse>()
    val sendOTPError = MutableLiveData<String?>()
    val loading = MutableLiveData<Boolean>()
    val navigationEvent = MutableLiveData<Boolean>()


    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        when (exception) {
            is IOException -> {
                // Handle network error
                println("Network Error: ${exception.message}")
                Log.e("ERROR", "Network Error: ${exception.message}")
            }

            is HttpException -> {
                // Handle HTTP error
                println("HTTP Error: ${exception.message}")
                Log.e("ERROR", "HTTP Error: ${exception.message}")
            }

            else -> {
                // Handle other exceptions
                println("Unknown Error: ${exception.message}")
                Log.e("ERROR", "Unknown Error: ${exception.message}")
            }
        }
    }

    fun refreshVerifyOtp(email: String,otp: String) {
        if (isUserNameValid(email)) {
            requestVerifyOtp(VerifyOTPRequest(email, otp))
        } else {
            sendOTPError.value = "Please enter valid Email"
        }
    }

    private fun requestVerifyOtp(request: VerifyOTPRequest) {
        loading.value = true
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            supervisorScope {
                try {
                    val response = apiService.requestVerifyOTP(request)
                    withContext(Dispatchers.Main) {
                        Log.d(
                            "MVVM",
                            "Login Success : ${response.isSuccessful}  Response Code : ${response.code()} response : ${response.body()}"
                        )
                        if (response.code() == 200) {
                            //if (response.body()!!.status) {
                            verifyOTPResponse.value = response.body()
                            //  loginError.value = null
                            loading.value = false
                            navigationEvent.value = true
                            // }
                        } else {
                            loading.value = false
                            navigationEvent.value = false
                            try {
                                val jObjError = JSONObject(response.errorBody()!!.string())
                                val error = jObjError.getString("message")
                                onError(error)
                            } catch (e: java.lang.Exception) {
                                onError(e.message.toString())
                            }
                        }
                    }
                } catch (e: IOException) {
                    Log.e("ERROR", "Network Error: ${e.message}")
                    onError("Network Error: ${e.message}")
                } catch (e: HttpException) {
                    Log.e("ERROR", "HTTP Error: ${e.message}")
                    onError("HTTP Error: ${e.message}")
                } catch (e: Exception) {
                    Log.e("ERROR", "Unknown Error: ${e.message}")
                    onError("Unknown Error: ${e.message}")

                }
            }
        }

    }


    fun refreshSendOtp(userEmail: String) {
        if (isUserNameValid(userEmail)) {
            requestOtp(SendOTPRequest(userEmail))
        } else {
            sendOTPError.value = "Please enter valid Email"
        }
    }

    private fun requestOtp(request: SendOTPRequest) {
        loading.value = true
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            supervisorScope {
                try {
                    val response = apiService.requestOTP(request)
                    withContext(Dispatchers.Main) {
                        Log.d(
                            "MVVM",
                            "Login Success : ${response.isSuccessful}  Response Code : ${response.code()} response : ${response.body()}"
                        )
                        if (response.code() == 200) {
                            //if (response.body()!!.status) {
                            sendOTPResponse.value = response.body()
                            //  loginError.value = null
                            loading.value = false
                            navigationEvent.value = true
                            // }
                        } else {
                            loading.value = false
                            navigationEvent.value = false
                            try {
                                val jObjError = JSONObject(response.errorBody()!!.string())
                                val error = jObjError.getString("error")
                                onError(error)
                            } catch (e: java.lang.Exception) {
                                onError(e.message.toString())
                            }
                        }
                    }
                } catch (e: IOException) {
                    Log.e("ERROR", "Network Error: ${e.message}")
                    onError("Network Error: ${e.message}")
                } catch (e: HttpException) {
                    Log.e("ERROR", "HTTP Error: ${e.message}")
                    onError("HTTP Error: ${e.message}")
                } catch (e: Exception) {
                    Log.e("ERROR", "Unknown Error: ${e.message}")
                    onError("Unknown Error: ${e.message}")

                }
            }
        }

    }


    private fun onError(message: String) {
        Log.d("MVVM", "Login Response $message")
        sendOTPError.value = message
        loading.value = false
        navigationEvent.value = false
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }

    fun navigationEventCompleted() {
        navigationEvent.value = false
    }

}