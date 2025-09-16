package com.wizsuite.event.ui.activities.change_password

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
import com.wizsuite.event.databinding.ActivityChangePasswordBinding
import com.wizsuite.event.model.SendOTPResponse
import com.wizsuite.event.model.UpdatePasswordRequest
import com.wizsuite.event.utils.AppUtils
import com.wizsuite.event.utils.PreferenceUtils
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePasswordActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var binding: ActivityChangePasswordBinding
    lateinit var changePasswordViewModel: ChangePasswordViewModel
    val TAG: String = "SET_NEW_PASSWORD"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        changePasswordViewModel = ViewModelProvider(this)[ChangePasswordViewModel::class.java]
        setOnClickListener()
        observeViewModel()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun observeViewModel() {
        changePasswordViewModel.updatePasswordPResponse.observe(this) { response ->
            Log.i(TAG, "Response : $response")
            Toast.makeText(
                this@ChangePasswordActivity,
                response.status,
                Toast.LENGTH_LONG
            ).show()
            // startActivity(Intent(this@SetNewPasswordActivity, SetNewPasswordActivity::class.java))
            finish()
        }
        changePasswordViewModel.sendOTPError.observe(this) { error ->
            Toast.makeText(
                this@ChangePasswordActivity,
                error,
                Toast.LENGTH_LONG
            ).show()
        }
        changePasswordViewModel.loading.observe(this) { loading ->
            if (loading) {
                binding.loading.visibility = View.VISIBLE
            } else {
                binding.loading.visibility = View.GONE
            }
        }
    }


    private fun setOnClickListener() {
        binding.submit.setOnClickListener(this)
        Log.d(TAG, "Set OnClickListener")
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.submit -> {

                if (validateForm()) {
                    val userId: String? =
                        PreferenceUtils.Companion.getString(
                            this@ChangePasswordActivity,
                            AppUtils.Companion.USER_ID
                        )
                    if (userId != null) {
                       changePasswordViewModel.refreshUpdatePassword(binding.password.text.toString(), userId)
                        //callUpdatePasswordAPI(userId, binding.password.text.toString())
                    }
                }
            }

            else -> Log.d(TAG, "In Else Part")
        }
    }


    private fun callUpdatePasswordAPI(userId: String, password: String) {
        binding.loading.visibility = View.VISIBLE
        Log.d(TAG, "User ID : " + userId + " Password : " + password)
        val apiService = ApiUtility.getInstance().create(ApiInterface::class.java)
        apiService.updatePasswordRequest(UpdatePasswordRequest(password, userId))
            .enqueue(object : Callback<SendOTPResponse> {

                override fun onResponse(
                    call: Call<SendOTPResponse>,
                    response: Response<SendOTPResponse>
                ) {
                    if (response.code() == 200) {
                        binding.loading.visibility = View.INVISIBLE
                        Log.i(TAG, "Response : " + response.body()!!)
                        Toast.makeText(
                            this@ChangePasswordActivity,
                            response.body()!!.status,
                            Toast.LENGTH_LONG
                        ).show()
                        // startActivity(Intent(this@SetNewPasswordActivity, SetNewPasswordActivity::class.java))
                        finish()
                    } else {
                        binding.loading.visibility = View.INVISIBLE
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        val errorMessage = jObjError.getString("error")
                        Toast.makeText(
                            this@ChangePasswordActivity,
                            /*response.body()!!.status*/
                            errorMessage,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<SendOTPResponse>, t: Throwable) {
                    Log.i(TAG, "Response Error : " + t.message)
                    binding.loading.visibility = View.INVISIBLE
                }
            })
    }

    private fun validateForm(): Boolean {
        if (binding.password.text.isEmpty()) {
            Toast.makeText(
                this@ChangePasswordActivity,
                R.string.empty_password,
                Toast.LENGTH_LONG
            ).show()
            return false
        } else if (binding.password.text.length < 6) {
            Toast.makeText(
                this@ChangePasswordActivity,
                R.string.password_length,
                Toast.LENGTH_LONG
            ).show()
            return false
        } else if (binding.confirmPassword.text.isEmpty()) {
            Toast.makeText(
                this@ChangePasswordActivity,
                R.string.empty_confirm_password,
                Toast.LENGTH_LONG
            ).show()
            return false
        } else if (binding.password.text.toString()
                .trim() != binding.confirmPassword.text.toString().trim()
        ) {
            Toast.makeText(
                this@ChangePasswordActivity,
                R.string.confirm_password_not_match,
                Toast.LENGTH_LONG
            )
                .show()
            return false
        }
        return true
    }
}