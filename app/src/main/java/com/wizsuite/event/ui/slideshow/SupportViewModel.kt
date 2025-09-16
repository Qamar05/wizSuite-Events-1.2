package com.wizsuite.event.ui.slideshow

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wizsuite.event.model.SupportRequest
import com.wizsuite.event.model.SupportResponse
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

class SupportViewModel: ViewModel() {
    private val apiService = ApiService.getNetworkService()
    private var job: Job? = null

    val supportResponse = MutableLiveData<SupportResponse>()
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

    fun refreshSupport(token: String, message: String) {
        if (isMessageValid(message)) {
        requestSupport(SupportRequest(token, message))
        } else {
            sendOTPError.value = "Please enter valid Email"
        }
    }

    private fun requestSupport(request: SupportRequest) {
        loading.value = true
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            supervisorScope {
                try {
                    val response = apiService.requestSupport(request)
                    withContext(Dispatchers.Main) {
                        Log.d(
                            "MVVM",
                            "Support Success : ${response.isSuccessful}  Response Code : ${response.code()} response : ${response.body()}"
                        )
                        if (response.code() == 200) {
                            //if (response.body()!!.status) {
                            supportResponse.value = response.body()
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



    private fun onError(message: String) {
        Log.d("MVVM", "Support Response $message")
        sendOTPError.value = message
        loading.value = false
        navigationEvent.value = false
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }



    // A placeholder password validation check
    private fun isMessageValid(message: String): Boolean {
        return message.isNotBlank() && message.length > 5
    }

    fun navigationEventCompleted() {
        navigationEvent.value = false
    }

}