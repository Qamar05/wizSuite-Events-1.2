package com.wizsuite.event.ui.activities.verify_login_otp

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
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
import com.wizsuite.event.databinding.ActivityVerifyLoginOtpBinding
import com.wizsuite.event.model.LoginResponse
import com.wizsuite.event.model.SendLoginOTPRequest
import com.wizsuite.event.model.SendLoginOTPResponse
import com.wizsuite.event.model.VerifyLoginOTPRequest
import com.wizsuite.event.ui.activities.home.MainActivity
import com.wizsuite.event.utils.AppUtils
import com.wizsuite.event.utils.GenericKeyEvent
import com.wizsuite.event.utils.GenericTextWatcher
import com.wizsuite.event.utils.PreferenceUtils
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VerifyLoginOtpActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var binding: ActivityVerifyLoginOtpBinding
    lateinit var viewModel: VerifyLoginOtpViewModel

    val TAG: String = "VERIFY_OTP"
    var phoneNumber: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityVerifyLoginOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[VerifyLoginOtpViewModel::class.java]

        observeViewModel()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (intent.hasExtra(AppUtils.Companion.PHONE)) {
            phoneNumber = intent.getStringExtra(AppUtils.Companion.PHONE)
            Log.d(TAG, "Get Intent : PhoneNumber" + phoneNumber)
        }

        setOnClickListener()
        setTextChangeListener()
        countDownTimer()
    }

    private fun observeViewModel() {
        viewModel.smsLoginResponse.observe(this) { response ->
            if (response != null) {
                saveLoginData(response)
            }
        }
        viewModel.sendSmsResponse.observe (this){response ->
            if (response != null) {
                saveResponse(response)
            }
        }
        viewModel.sendOTPError.observe(this) { errorMessage ->
            if (errorMessage != null) {
                Toast.makeText(this@VerifyLoginOtpActivity, errorMessage, Toast.LENGTH_LONG).show()
            }
        }
        viewModel.loading.observe(this) { isLoading ->
            if (isLoading) {
                binding.loading.visibility = View.VISIBLE
            } else {
                binding.loading.visibility = View.GONE
            }
        }
    }

    private fun saveResponse(response: SendLoginOTPResponse) {
        Toast.makeText(
            this@VerifyLoginOtpActivity,
            /*response.body()!!.status*/
            "OTP sent to your registered Phone, Please check",
            Toast.LENGTH_LONG
        ).show()
        countDownTimer()
    }

    private fun saveLoginData(response: LoginResponse) {
        Log.d(TAG, "Response : $response")
        PreferenceUtils.Companion.saveBoolean(
            this@VerifyLoginOtpActivity,
            AppUtils.Companion.IS_LOGGED_IN, true
        )
        PreferenceUtils.Companion.saveString(
            this@VerifyLoginOtpActivity,
            AppUtils.Companion.ACCESS_TOKEN,
            response.token
        )
        PreferenceUtils.Companion.saveString(
            this@VerifyLoginOtpActivity,
            AppUtils.Companion.GUEST_NAME,
            response.name
        )
        PreferenceUtils.Companion.saveString(
            this@VerifyLoginOtpActivity,
            AppUtils.Companion.GUEST_PHONE,
            response.phone
        )
        startActivity(Intent(this@VerifyLoginOtpActivity, MainActivity::class.java))
        finish()
    }


    private fun countDownTimer() {
        object : CountDownTimer(30000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                binding.notReceivedOTP.visibility = View.INVISIBLE
                binding.resendOTP.visibility = View.INVISIBLE
                binding.txtCountDownTimer.text = "${millisUntilFinished / 1000} Sec Remaining"
            }

            override fun onFinish() {
                binding.txtCountDownTimer.text = ""
                binding.notReceivedOTP.visibility = View.VISIBLE
                binding.resendOTP.visibility = View.VISIBLE
            }
        }.start()
    }

    private fun setOnClickListener() {
        binding.sendOTP.setOnClickListener(this)
        binding.resendOTP.setOnClickListener(this)
        Log.d(TAG, "Set OnClickListener")
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.sendOTP -> {
                Log.d(TAG, "PhoneNumber : $phoneNumber")
                if (validateForm()) {
                    viewModel.refreshSmsLogin(phoneNumber.toString(), getOTP())
                    // callVerifyOTPAPI(phoneNumber.toString(), getOTP())
                }
            }

            R.id.sendOTP -> {
                Log.d(TAG, "PhoneNumber : $phoneNumber")
                if (validateForm()) {
                    viewModel.refreshSendSms(phoneNumber.toString())
                   // callSendOTPAPI(phoneNumber.toString())
                }
            }

            else -> Log.d(TAG, "In Else Part")
        }
    }

    private fun getOTP(): String {
        return binding.otpDigit1.text.toString() + binding.otpDigit2.text.toString() + binding.otpDigit3.text.toString() + binding.otpDigit4.text.toString() + binding.otpDigit5.text.toString() + binding.otpDigit6.text.toString()

    }

    private fun callVerifyOTPAPI(phoneNumber: String, otp: String) {
        binding.loading.visibility = View.VISIBLE
        Log.d(TAG, "PhoneNumber: $phoneNumber OTP: $otp")
        val apiService = ApiUtility.getInstance().create(ApiInterface::class.java)
        apiService.verifyLoginOTPRequest(VerifyLoginOTPRequest(phoneNumber, otp))
            .enqueue(object : Callback<LoginResponse> {

                override fun onResponse(
                    call: Call<LoginResponse>, response: Response<LoginResponse>
                ) {
                    if (response.code() == 200) {
                        binding.loading.visibility = View.INVISIBLE
                        Log.d(TAG, "Response : " + response.body()!!)
                        PreferenceUtils.Companion.saveBoolean(
                            this@VerifyLoginOtpActivity,
                            AppUtils.Companion.IS_LOGGED_IN, true
                        )
                        PreferenceUtils.Companion.saveString(
                            this@VerifyLoginOtpActivity,
                            AppUtils.Companion.ACCESS_TOKEN,
                            response.body()!!.token
                        )
                        PreferenceUtils.Companion.saveString(
                            this@VerifyLoginOtpActivity,
                            AppUtils.Companion.GUEST_NAME,
                            response.body()!!.name
                        )
                        PreferenceUtils.Companion.saveString(
                            this@VerifyLoginOtpActivity,
                            AppUtils.Companion.GUEST_PHONE,
                            response.body()!!.phone
                        )
                        startActivity(Intent(this@VerifyLoginOtpActivity, MainActivity::class.java))
                        finish()
                    } else {
                        binding.loading.visibility = View.INVISIBLE
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        val errorMessage = jObjError.getString("message")
                        Toast.makeText(
                            this@VerifyLoginOtpActivity,
                            /*response.body()!!.status*/
                            errorMessage,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Log.d(TAG, "Response Error : " + t.message)
                    binding.loading.visibility = View.INVISIBLE
                }
            })
    }

    private fun validateForm(): Boolean {
        if (binding.otpDigit1.text.isEmpty()) {
            Toast.makeText(
                this@VerifyLoginOtpActivity,
                R.string.provide_valid_otp,
                Toast.LENGTH_LONG
            )
                .show()
            return false
        } else if (binding.otpDigit2.text.isEmpty()) {
            Toast.makeText(
                this@VerifyLoginOtpActivity,
                R.string.provide_valid_otp,
                Toast.LENGTH_LONG
            )
                .show()
            return false
        } else if (binding.otpDigit3.text.isEmpty()) {
            Toast.makeText(
                this@VerifyLoginOtpActivity,
                R.string.provide_valid_otp,
                Toast.LENGTH_LONG
            )
                .show()
            return false
        } else if (binding.otpDigit4.text.isEmpty()) {
            Toast.makeText(
                this@VerifyLoginOtpActivity,
                R.string.provide_valid_otp,
                Toast.LENGTH_LONG
            )
                .show()
            return false
        } else if (binding.otpDigit5.text.isEmpty()) {
            Toast.makeText(
                this@VerifyLoginOtpActivity,
                R.string.provide_valid_otp,
                Toast.LENGTH_LONG
            )
                .show()
            return false
        } else if (binding.otpDigit6.text.isEmpty()) {
            Toast.makeText(
                this@VerifyLoginOtpActivity,
                R.string.provide_valid_otp,
                Toast.LENGTH_LONG
            )
                .show()
            return false
        }
        return true
    }

    private fun setTextChangeListener() {
        binding.otpDigit1.addTextChangedListener(
            GenericTextWatcher(
                binding.otpDigit1,
                binding.otpDigit2
            )
        )
        binding.otpDigit2.addTextChangedListener(
            GenericTextWatcher(
                binding.otpDigit2,
                binding.otpDigit3
            )
        )
        binding.otpDigit3.addTextChangedListener(
            GenericTextWatcher(
                binding.otpDigit3,
                binding.otpDigit4
            )
        )
        binding.otpDigit4.addTextChangedListener(
            GenericTextWatcher(
                binding.otpDigit4,
                binding.otpDigit5
            )
        )
        binding.otpDigit5.addTextChangedListener(
            GenericTextWatcher(
                binding.otpDigit5,
                binding.otpDigit6
            )
        )

        binding.otpDigit1.setOnKeyListener(GenericKeyEvent(binding.otpDigit1, null))
        binding.otpDigit2.setOnKeyListener(GenericKeyEvent(binding.otpDigit2, binding.otpDigit1))
        binding.otpDigit3.setOnKeyListener(GenericKeyEvent(binding.otpDigit3, binding.otpDigit2))
        binding.otpDigit4.setOnKeyListener(GenericKeyEvent(binding.otpDigit4, binding.otpDigit3))
        binding.otpDigit5.setOnKeyListener(GenericKeyEvent(binding.otpDigit5, binding.otpDigit4))
        binding.otpDigit6.setOnKeyListener(GenericKeyEvent(binding.otpDigit6, binding.otpDigit5))
    }

    private fun callSendOTPAPI(phone: String) {
        binding.loading.visibility = View.VISIBLE
        Log.d(TAG, "Request :  $phone")
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
                            this@VerifyLoginOtpActivity,
                            /*response.body()!!.status*/
                            "OTP sent to your registered Phone, Please check",
                            Toast.LENGTH_LONG
                        ).show()
                        countDownTimer()
                    } else {
                        binding.loading.visibility = View.INVISIBLE
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        val errorMessage = jObjError.getString("error")
                        Toast.makeText(
                            this@VerifyLoginOtpActivity,
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


}