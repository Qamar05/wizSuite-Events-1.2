package com.wizsuite.event.ui.activities.verify_otp

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
import com.wizsuite.event.databinding.ActivityVerifyOtpBinding
import com.wizsuite.event.model.SendOTPRequest
import com.wizsuite.event.model.SendOTPResponse
import com.wizsuite.event.model.VerifyOTPRequest
import com.wizsuite.event.model.VerifyOTPResponse
import com.wizsuite.event.ui.activities.create_pssword.SetNewPasswordActivity
import com.wizsuite.event.utils.AppUtils
import com.wizsuite.event.utils.GenericKeyEvent
import com.wizsuite.event.utils.GenericTextWatcher
import com.wizsuite.event.utils.PreferenceUtils
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VerifyOTPActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var binding: ActivityVerifyOtpBinding
    val TAG: String = "VERIFY_OTP"
    lateinit var verifyOtpViewModel: VerifyOtpViewModel
    var email: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityVerifyOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        verifyOtpViewModel = ViewModelProvider(this)[VerifyOtpViewModel::class.java]
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        observeViewModel()
        if (intent.hasExtra(AppUtils.Companion.EMAIL)) {
            email = intent.getStringExtra(AppUtils.Companion.EMAIL)
            Log.d(TAG, "Get Intent : Email $email")
        }
        setOnClickListener()
        setTextChangeListener()
        countDownTimer()
    }

    private fun observeViewModel() {
        verifyOtpViewModel.verifyOTPResponse.observe(this) { response ->
            if (response != null) {
                saveUserData(response)
            }
        }
        verifyOtpViewModel.sendOTPResponse.observe(this) { response ->
            if (response != null) {
                Toast.makeText(
                    this@VerifyOTPActivity,
                    /*response.body()!!.status*/
                    "OTP sent to your registered email, Please check your email",
                    Toast.LENGTH_LONG
                ).show()
                countDownTimer()
            }
        }
        verifyOtpViewModel.sendOTPError.observe(this) {
            Toast.makeText(this@VerifyOTPActivity, it, Toast.LENGTH_LONG).show()
        }
        verifyOtpViewModel.loading.observe(this) {
            if (it) {
                binding.loading.visibility = View.VISIBLE
            } else {
                binding.loading.visibility = View.GONE
            }
        }
    }

    private fun saveUserData(response: VerifyOTPResponse) {
        Toast.makeText(
            this@VerifyOTPActivity,
            response.message,
            Toast.LENGTH_LONG
        ).show()
        PreferenceUtils.Companion.saveString(
            this@VerifyOTPActivity,
            AppUtils.Companion.USER_ID,
            response.user_id
        )
        startActivity(
            Intent(
                this@VerifyOTPActivity,
                SetNewPasswordActivity::class.java
            )
        )
        finish()
    }


    private fun countDownTimer() {
        object : CountDownTimer(30000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                binding.notReceivedOTP.visibility = View.INVISIBLE
                binding.resendOTP.visibility = View.INVISIBLE
                val remainingTime = "${millisUntilFinished / 1000} Sec Remaining"
                binding.txtCountDownTimer.text = remainingTime
            }

            override fun onFinish() {
                binding.txtCountDownTimer.text = ""
                binding.notReceivedOTP.visibility = View.VISIBLE
                binding.resendOTP.visibility = View.VISIBLE
            }
        }.start()
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

    private fun setOnClickListener() {
        binding.sendOTP.setOnClickListener(this)
        binding.resendOTP.setOnClickListener(this)
        Log.d(TAG, "Set OnClickListener")
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.sendOTP -> {
                Log.d(TAG, "Email : $email")
                if (validateForm()) {
                    verifyOtpViewModel.refreshVerifyOtp(email.toString(), getOTP())
                    //callVerifyOTPAPI(email.toString(), getOTP())
                }
            }

            R.id.resendOTP -> {
                Log.d(TAG, "Email : $email")
                if (validateForm()) {
                    verifyOtpViewModel.refreshSendOtp(email.toString())
                    //callSendOTPAPI(email.toString())
                }
            }

            else -> Log.d(TAG, "In Else Part")
        }
    }

    private fun getOTP(): String {
        return binding.otpDigit1.text.toString() + binding.otpDigit2.text.toString() + binding.otpDigit3.text.toString() + binding.otpDigit4.text.toString() + binding.otpDigit5.text.toString() + binding.otpDigit6.text.toString()

    }

    private fun callVerifyOTPAPI(email: String, otp: String) {
        binding.loading.visibility = View.VISIBLE
        Log.d(TAG, "Email : $email OTP :$otp")
        val apiService = ApiUtility.getInstance().create(ApiInterface::class.java)
        apiService.verifyOTPRequest(VerifyOTPRequest(email, otp))
            .enqueue(object : Callback<VerifyOTPResponse> {

                override fun onResponse(
                    call: Call<VerifyOTPResponse>, response: Response<VerifyOTPResponse>
                ) {
                    if (response.code() == 200) {
                        binding.loading.visibility = View.INVISIBLE
                        Log.d(TAG, "Response : " + response.body()!!)
                        Toast.makeText(
                            this@VerifyOTPActivity,
                            response.body()!!.message,
                            Toast.LENGTH_LONG
                        ).show()
                        PreferenceUtils.Companion.saveString(
                            this@VerifyOTPActivity,
                            AppUtils.Companion.USER_ID,
                            response.body()!!.user_id
                        )
                        startActivity(
                            Intent(
                                this@VerifyOTPActivity,
                                SetNewPasswordActivity::class.java
                            )
                        )
                        finish()
                    } else {
                        binding.loading.visibility = View.INVISIBLE
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        val errorMessage = jObjError.getString("message")
                        Toast.makeText(
                            this@VerifyOTPActivity,
                            /*response.body()!!.status*/
                            errorMessage,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<VerifyOTPResponse>, t: Throwable) {
                    Log.d(TAG, "Response Error : " + t.message)
                    binding.loading.visibility = View.INVISIBLE
                }
            })
    }

    private fun validateForm(): Boolean {
        if (binding.otpDigit1.text.isEmpty()) {
            Toast.makeText(this@VerifyOTPActivity, R.string.provide_valid_otp, Toast.LENGTH_LONG)
                .show()
            return false
        } else if (binding.otpDigit2.text.isEmpty()) {
            Toast.makeText(this@VerifyOTPActivity, R.string.provide_valid_otp, Toast.LENGTH_LONG)
                .show()
            return false
        } else if (binding.otpDigit3.text.isEmpty()) {
            Toast.makeText(this@VerifyOTPActivity, R.string.provide_valid_otp, Toast.LENGTH_LONG)
                .show()
            return false
        } else if (binding.otpDigit4.text.isEmpty()) {
            Toast.makeText(this@VerifyOTPActivity, R.string.provide_valid_otp, Toast.LENGTH_LONG)
                .show()
            return false
        } else if (binding.otpDigit5.text.isEmpty()) {
            Toast.makeText(this@VerifyOTPActivity, R.string.provide_valid_otp, Toast.LENGTH_LONG)
                .show()
            return false
        } else if (binding.otpDigit6.text.isEmpty()) {
            Toast.makeText(this@VerifyOTPActivity, R.string.provide_valid_otp, Toast.LENGTH_LONG)
                .show()
            return false
        }
        return true
    }

    private fun callSendOTPAPI(email: String) {
        binding.loading.visibility = View.VISIBLE
        Log.d(TAG, "Request : $email")
        val apiService = ApiUtility.getInstance().create(ApiInterface::class.java)
        apiService.sendOTPRequest(SendOTPRequest(email))
            .enqueue(object : Callback<SendOTPResponse> {
                override fun onResponse(
                    call: Call<SendOTPResponse>,
                    response: Response<SendOTPResponse>
                ) {
                    // Log.d(TAG, "Response : " + response.body()!!)
                    if (response.code() == 200) {
                        binding.loading.visibility = View.INVISIBLE
                        Toast.makeText(
                            this@VerifyOTPActivity,
                            /*response.body()!!.status*/
                            "OTP sent to your registered email, Please check your email",
                            Toast.LENGTH_LONG
                        ).show()
                        countDownTimer()
                    } else {
                        binding.loading.visibility = View.INVISIBLE
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        val errorMessage = jObjError.getString("error")
                        Toast.makeText(
                            this@VerifyOTPActivity,
                            /*response.body()!!.status*/
                            errorMessage,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<SendOTPResponse>, t: Throwable) {
                    Log.d(TAG, "Response Error : " + t.message)
                    binding.loading.visibility = View.INVISIBLE
                }
            })
    }

}

