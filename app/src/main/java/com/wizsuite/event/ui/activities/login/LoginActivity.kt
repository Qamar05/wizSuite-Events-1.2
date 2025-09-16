package com.wizsuite.event.ui.activities.login

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
import com.wizsuite.event.databinding.ActivityLoginBinding
import com.wizsuite.event.model.LoginRequest
import com.wizsuite.event.model.LoginResponse
import com.wizsuite.event.ui.activities.forgot_password.ForgotPasswordActivity
import com.wizsuite.event.ui.activities.login_with_otp.LoginWithOtpActivity
import com.wizsuite.event.ui.activities.sign_up.SignUpActivity
import com.wizsuite.event.ui.activities.home.MainActivity
import com.wizsuite.event.utils.AppUtils
import com.wizsuite.event.utils.PreferenceUtils
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var binding: ActivityLoginBinding
    val TAG: String = "LOGIN"

    lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        observeViewModel()
        setOnClickListener()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun observeViewModel() {
        loginViewModel.loginResponse.observe(this) { response ->
            Toast.makeText(this@LoginActivity, response.message, Toast.LENGTH_LONG).show()
            saveLoginData(response)
        }
        loginViewModel.loginError.observe(this) {
            Toast.makeText(this@LoginActivity, it, Toast.LENGTH_LONG).show()
        }
        loginViewModel.loading.observe(this) {
            if (it) {
                binding.loading.visibility = View.VISIBLE
            } else {
                binding.loading.visibility = View.GONE
            }
        }
    }

    private fun saveLoginData(response: LoginResponse) {
        PreferenceUtils.Companion.saveBoolean(
            this@LoginActivity,
            AppUtils.Companion.IS_LOGGED_IN, true
        )
        PreferenceUtils.Companion.saveString(
            this@LoginActivity,
            AppUtils.Companion.ACCESS_TOKEN,
            response.token
        )
        PreferenceUtils.Companion.saveString(
            this@LoginActivity,
            AppUtils.Companion.GUEST_NAME,
            response.name
        )
        PreferenceUtils.Companion.saveString(
            this@LoginActivity,
            AppUtils.Companion.GUEST_PHONE,
            response.phone
        )
        PreferenceUtils.Companion.saveString(
            this@LoginActivity,
            AppUtils.Companion.GUEST_EMAIL,
            response.email
        )
        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
        finish()
    }

    private fun setOnClickListener() {
        binding.login.setOnClickListener(this)
        binding.txtForgotPassword.setOnClickListener(this)
        binding.txtLoginWithOTP.setOnClickListener(this)
        binding.txtSignUpHere.setOnClickListener(this)
        Log.d(TAG, "Set OnClickListener")
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.login -> {
                Log.d(TAG, "In R.id.login")
                if (validateForm()) {
                    //callLoginAPI(binding.username.text.toString(), binding.password.text.toString())
                    loginViewModel.refresh(
                        binding.username.text.toString(),
                        binding.password.text.toString()
                    )
                }
            }

            R.id.txtForgotPassword -> {
                Log.d(TAG, "In R.id.txtForgotPassword")
                startActivity(Intent(this@LoginActivity, ForgotPasswordActivity::class.java))
                // finish()
            }

            R.id.txtLoginWithOTP -> {
                Log.d(TAG, "In R.id.txtLoginWithOTP")
                startActivity(Intent(this@LoginActivity, LoginWithOtpActivity::class.java))
                //finish()
            }

            R.id.txtSignUpHere -> {
                Log.d(TAG, "In R.id.txtSignUpHere")
                startActivity(Intent(this@LoginActivity, SignUpActivity::class.java))
                // finish()
            }

            else -> Log.d(TAG, "In Else Part")
        }
    }


    private fun callLoginAPI(email: String, password: String) {
        binding.loading.visibility = View.VISIBLE
        val apiService = ApiUtility.getInstance().create(ApiInterface::class.java)
        apiService.loginRequest(LoginRequest(email, password))
            .enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {

                    if (response.code() == 200) {
                        binding.loading.visibility = View.INVISIBLE
                        Log.i(TAG, "post submitted to API." + response.body()!!)
                        PreferenceUtils.Companion.saveBoolean(
                            this@LoginActivity,
                            AppUtils.Companion.IS_LOGGED_IN, true
                        )
                        PreferenceUtils.Companion.saveString(
                            this@LoginActivity,
                            AppUtils.Companion.ACCESS_TOKEN,
                            response.body()!!.token
                        )
                        PreferenceUtils.Companion.saveString(
                            this@LoginActivity,
                            AppUtils.Companion.GUEST_NAME,
                            response.body()!!.name
                        )
                        PreferenceUtils.Companion.saveString(
                            this@LoginActivity,
                            AppUtils.Companion.GUEST_PHONE,
                            response.body()!!.phone
                        )
                        PreferenceUtils.Companion.saveString(
                            this@LoginActivity,
                            AppUtils.Companion.GUEST_EMAIL,
                            response.body()!!.email
                        )
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    } else {
                        binding.loading.visibility = View.INVISIBLE
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        val errorMessage = jObjError.getString("message")
                        Toast.makeText(
                            this@LoginActivity,
                            errorMessage,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Log.i(TAG, "post submitted to API." + t.message)
                    binding.loading.visibility = View.INVISIBLE
                }
            })
    }

    private fun validateForm(): Boolean {
        if (binding.username.text.isEmpty()) {
            Toast.makeText(this@LoginActivity, R.string.empty_email, Toast.LENGTH_LONG).show()
            return false
        } else if (binding.password.text.isEmpty()) {
            Toast.makeText(this@LoginActivity, R.string.empty_password, Toast.LENGTH_LONG).show()
            return false
        }/*else if (binding.password.text.toString().length<6){
            Toast.makeText(this@LoginActivity,R.string.empty_password,Toast.LENGTH_LONG).show()
            return false
        }*/
        return true
    }
}


