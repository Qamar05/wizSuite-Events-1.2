package com.wizsuite.event.ui.activities.forgot_password

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
import com.wizsuite.event.databinding.ActivityForgotPasswordBinding
import com.wizsuite.event.model.SendOTPRequest
import com.wizsuite.event.model.SendOTPResponse
import com.wizsuite.event.ui.activities.verify_otp.VerifyOTPActivity
import com.wizsuite.event.utils.AppUtils
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotPasswordActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var binding: ActivityForgotPasswordBinding
    val TAG: String = "LOGIN"
    lateinit var forgotPasswordViewModel: ForgotPasswordViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setOnClickListener()
        forgotPasswordViewModel = ViewModelProvider(this)[ForgotPasswordViewModel::class.java]
        observeViewModel()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun observeViewModel() {
        forgotPasswordViewModel.sendOTPResponse.observe(this) { response ->
            Toast.makeText(
                this@ForgotPasswordActivity,
                /*response.body()!!.status*/
                "OTP sent to your registered email, Please check your email",
                Toast.LENGTH_LONG
            ).show()
            val intent =
                Intent(this@ForgotPasswordActivity, VerifyOTPActivity::class.java)
            intent.putExtra(AppUtils.Companion.EMAIL, binding.userEmail.text.toString())
            startActivity(intent)
            finish()
        }
        forgotPasswordViewModel.sendOTPError.observe(this) {
            Toast.makeText(this@ForgotPasswordActivity, it, Toast.LENGTH_LONG).show()
        }

        forgotPasswordViewModel.loading.observe(this) {
            if (it) {
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
                   // callSendOTPAPI(binding.userEmail.text.toString())
                    forgotPasswordViewModel.refresh(binding.userEmail.text.toString())
                }

                /*val intent = Intent(this@ForgotPasswordActivity, VerifyOTPActivity::class.java)
                startActivity(intent)*/
            }

            else -> Log.d(TAG, "In Else Part")
        }
    }

    private fun callSendOTPAPI(email: String) {
        binding.loading.visibility = View.VISIBLE
        Log.d(TAG, "Request : " + email)
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
                            this@ForgotPasswordActivity,
                            /*response.body()!!.status*/
                            "OTP sent to your registered email, Please check your email",
                            Toast.LENGTH_LONG
                        ).show()
                        val intent =
                            Intent(this@ForgotPasswordActivity, VerifyOTPActivity::class.java)
                        intent.putExtra(AppUtils.Companion.EMAIL, email)
                        startActivity(intent)
                        finish()
                    } else {
                        binding.loading.visibility = View.INVISIBLE
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        val errorMessage = jObjError.getString("error")
                        Toast.makeText(
                            this@ForgotPasswordActivity,
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

    private fun validateForm(): Boolean {
        if (binding.userEmail.text.isEmpty()) {
            Toast.makeText(this@ForgotPasswordActivity, R.string.empty_email, Toast.LENGTH_LONG)
                .show()
            return false
        }
        return true
    }


}