package com.wizsuite.event.ui.activities.settings

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.wizsuite.event.R
import com.wizsuite.event.api.ApiInterface
import com.wizsuite.event.api.ApiUtility
import com.wizsuite.event.databinding.ActivitySettingsBinding
import com.wizsuite.event.model.EventRequest
import com.wizsuite.event.model.SupportResponse
import com.wizsuite.event.ui.activities.change_password.ChangePasswordActivity
import com.wizsuite.event.ui.activities.login.LoginActivity
import com.wizsuite.event.utils.AppUtils
import com.wizsuite.event.utils.PreferenceUtils
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SettingsActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var binding: ActivitySettingsBinding
    val TAG: String = "SETTINGS"
    lateinit var settingsViewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setOnClickListener()
        settingsViewModel = ViewModelProvider(this)[SettingsViewModel::class.java]
        observeViewModel()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun observeViewModel() {
        settingsViewModel.deleteAccountResponse.observe(this) { response ->
            Log.i(TAG, "post submitted to API.$response")
            PreferenceUtils.Companion.saveBoolean(this@SettingsActivity,
                AppUtils.Companion.IS_LOGGED_IN,false)
            startActivity(Intent(this@SettingsActivity, LoginActivity::class.java))
            finish()
        }
        settingsViewModel.sendOTPError.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()

        }
        settingsViewModel.loading.observe(this) {
        }
    }

    private fun setOnClickListener() {
        binding.txtFaq.setOnClickListener(this)
        binding.txtChangePassword.setOnClickListener(this)
        binding.txtPrivacyPolicy.setOnClickListener(this)
        binding.txtTermsAndConditions.setOnClickListener(this)
        binding.txtDeleteMyAccount.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.txtFaq -> {
                // startActivity(Intent(this@SettingsActivity, SignUpActivity::class.java))
            }
            R.id.txtChangePassword -> {
                startActivity(Intent(this@SettingsActivity, ChangePasswordActivity::class.java))
                // finish()
            }
            R.id.txtPrivacyPolicy -> {
                AppUtils.Companion.openBrowser(this, AppUtils.Companion.PRIVACY_POLICY)
            }
            R.id.txtTermsAndConditions -> {
                AppUtils.Companion.openBrowser(this, AppUtils.Companion.TERMS_AND_CONDITIONS)
            }
            R.id.txtDeleteMyAccount -> {
                // startActivity(Intent(this@SettingsActivity, SignUpActivity::class.java))
                // finish()
                showDialog()
            }
            else -> Log.d(TAG, "In Else Part")
        }
    }


    private fun showDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.logout_dialog)
        dialog.window?.setLayout(CoordinatorLayout.LayoutParams.MATCH_PARENT, CoordinatorLayout.LayoutParams.MATCH_PARENT)
        dialog.window?.setBackgroundDrawableResource(R.color.black_transparent)

        val header = dialog.findViewById(R.id.txtLogoutHeader) as TextView
        val body = dialog.findViewById(R.id.txtDialogDescription) as TextView
        header.text = getString(R.string.delete_account_head)
        body.text = getString(R.string.delete_account_body)

        val yesBtn = dialog.findViewById(R.id.txtYes) as TextView
        val noBtn = dialog.findViewById(R.id.txtNo) as TextView
        yesBtn.text = getString(R.string._continue)
        noBtn.text = getString(R.string._cancel)

        yesBtn.setOnClickListener {
            /*  startActivity(Intent(this@MainActivity, LoginActivity::class.java))
              finish()
              PreferenceUtils.saveBoolean(this, AppUtils.IS_LOGGED_IN, false)
             */ //dialog.dismiss()

           // callDeleteAccountAPI()
            val token = PreferenceUtils.Companion.getString(this, AppUtils.Companion.ACCESS_TOKEN)
            Log.d(TAG, "Token : $token")
            settingsViewModel.refreshDeleteAccount(token!!)
        }
        noBtn.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()

    }


    private fun callDeleteAccountAPI() {
        val token = PreferenceUtils.Companion.getString(this, AppUtils.Companion.ACCESS_TOKEN)
        Log.d(TAG, "Token : " + token)
        val apiService = ApiUtility.getInstance().create(ApiInterface::class.java)
        apiService.deleteUserRequest(EventRequest(token!!))
            .enqueue(object : Callback<SupportResponse> {
                override fun onResponse(
                    call: Call<SupportResponse>,
                    response: Response<SupportResponse>
                ) {

                    if (response.code()==200) {
                        // binding.loading.visibility = View.INVISIBLE
                        Log.i(TAG, "post submitted to API." + response.body()!!)
                        PreferenceUtils.Companion.saveBoolean(this@SettingsActivity,
                            AppUtils.Companion.IS_LOGGED_IN,false)
                        startActivity(Intent(this@SettingsActivity, LoginActivity::class.java))
                        finish()
                    }else{
                        // binding.loading.visibility = View.INVISIBLE
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        val errorMessage = jObjError.getString("message")
                        Toast.makeText(
                            this@SettingsActivity,
                            errorMessage,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<SupportResponse>, t: Throwable) {
                    Log.i(TAG, "post submitted to API." + t.message)
                    // binding.loading.visibility = View.INVISIBLE
                }
            })
    }
}