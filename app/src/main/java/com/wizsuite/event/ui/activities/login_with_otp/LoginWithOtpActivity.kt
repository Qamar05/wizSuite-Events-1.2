package com.wizsuite.event.ui.activities.login_with_otp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.wizsuite.event.R
import com.wizsuite.event.api.ApiInterface
import com.wizsuite.event.api.ApiUtility
import com.wizsuite.event.databinding.ActivityLoginWithOtpBinding
import com.wizsuite.event.model.SendLoginOTPRequest
import com.wizsuite.event.model.SendLoginOTPResponse
import com.wizsuite.event.ui.activities.verify_login_otp.VerifyLoginOtpActivity
import com.wizsuite.event.utils.AppUtils
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.log

class LoginWithOtpActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var binding: ActivityLoginWithOtpBinding
    lateinit var loginWithOtpViewModel: LoginWithOtpViewModel
    val TAG: String = "LOGIN"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginWithOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loginWithOtpViewModel = ViewModelProvider(this)[LoginWithOtpViewModel::class.java]
        setOnClickListener()
        observeViewModel()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun observeViewModel() {
        loginWithOtpViewModel.sendSmsResponse.observe(this) { response ->
            Toast.makeText(
                this@LoginWithOtpActivity,
                /*response.body()!!.status*/
                "OTP sent to your registered Phone, Please check",
                Toast.LENGTH_LONG
            ).show()
            val intent =
                Intent(this@LoginWithOtpActivity, VerifyLoginOtpActivity::class.java)
            intent.putExtra(AppUtils.Companion.PHONE, binding.userPhone.text.toString())
            startActivity(intent)
            finish()
        }
        loginWithOtpViewModel.sendOTPError.observe(this) { errorMessage ->
            Toast.makeText(
                this@LoginWithOtpActivity,
                /*response.body()!!.status*/
                errorMessage,
                Toast.LENGTH_LONG
            ).show()
        }
        loginWithOtpViewModel.loading.observe(this) { isLoading ->
            if (isLoading) {
                binding.loading.visibility = View.VISIBLE
            } else {
                binding.loading.visibility = View.GONE
            }
        }
    }

    private fun setOnClickListener() {
        binding.sendOTP.setOnClickListener(this)
        Log.d(TAG, "Set OnClickListener")
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.sendOTP -> {
                Log.d(TAG, "In R.id.login")
                if (validateForm()) {
                    loginWithOtpViewModel.refreshSendSms(binding.userPhone.text.toString())
                    //callSendOTPAPI(binding.userPhone.text.toString())
                }

                /*val intent = Intent(this@LoginWithOtpActivity, VerifyOTPActivity::class.java)
                startActivity(intent)*/
            }
            else -> Log.d(TAG, "In Else Part")
        }
    }

    private fun callSendOTPAPI(phone: String) {
        binding.loading.visibility = View.VISIBLE
        Log.d(TAG, "Request : $phone")
        val apiService = ApiUtility.getInstance().create(ApiInterface::class.java)
        apiService.sendLoginOTPRequest(SendLoginOTPRequest(phone))
            .enqueue(object : Callback<SendLoginOTPResponse> {
                override fun onResponse(
                    call: Call<SendLoginOTPResponse>,
                    response: Response<SendLoginOTPResponse>
                ) {
                    // Log.d(TAG, "Response : " + response.body()!!)
                    if (response.code() == 200) {
                        binding.loading.visibility = View.INVISIBLE
                        Toast.makeText(
                            this@LoginWithOtpActivity,
                            /*response.body()!!.status*/
                            "OTP sent to your registered Phone, Please check",
                            Toast.LENGTH_LONG
                        ).show()
                        val intent =
                            Intent(this@LoginWithOtpActivity, VerifyLoginOtpActivity::class.java)
                        intent.putExtra(AppUtils.Companion.PHONE, phone)
                        startActivity(intent)
                        finish()
                    } else {
                        binding.loading.visibility = View.INVISIBLE
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        val errorMessage = jObjError.getString("error")
                        Toast.makeText(
                            this@LoginWithOtpActivity,
                            /*response.body()!!.status*/
                            errorMessage,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<SendLoginOTPResponse>, t: Throwable) {
                    Log.d(TAG, "Response Error : " + t.message)
                    binding.loading.visibility = View.INVISIBLE
                }
            })
    }

    private fun validateForm(): Boolean {
        if (binding.userPhone.text.isEmpty()) {
            Toast.makeText(this@LoginWithOtpActivity, R.string.empty_phone, Toast.LENGTH_LONG)
                .show()
            return false
        }else if (binding.userPhone.text.toString().length<10) {
            Toast.makeText(this@LoginWithOtpActivity, R.string.empty_phone, Toast.LENGTH_LONG)
                .show()
            return false
        }
        return true
    }

}