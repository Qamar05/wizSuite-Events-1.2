package com.wizsuite.event.ui.activities.home

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.onNavDestinationSelected
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.wizsuite.event.R
import com.wizsuite.event.api.ApiInterface
import com.wizsuite.event.api.ApiUtility
import com.wizsuite.event.databinding.ActivityMainBinding
import com.wizsuite.event.model.UpdateTokenRequest
import com.wizsuite.event.model.UpdateTokenResponse
import com.wizsuite.event.ui.activities.settings.SettingsActivity
import com.wizsuite.event.ui.activities.login.LoginActivity
import com.wizsuite.event.utils.AppUtils
import com.wizsuite.event.utils.PreferenceUtils
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    val TAG: String = "LOGIN"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)


        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        logoutDialogue(navView, navController, drawerLayout)
        askNotificationPermission()
        val hView: View = navView.getHeaderView(0)
        setUserData(
            hView.findViewById<TextView>(R.id.txtUserName),
            hView.findViewById<TextView>(R.id.txtUserEmail)
        )
    }

    private fun setUserData(txtUserName: TextView, txtUserEmail: TextView) {
        txtUserName.text = PreferenceUtils.getString(this, AppUtils.GUEST_NAME)
        txtUserEmail.text = PreferenceUtils.getString(this, AppUtils.GUEST_EMAIL)
    }


    private fun logoutDialogue(
        navView: NavigationView, navController: NavController, drawerLayout: DrawerLayout
    ) {

        navView.setNavigationItemSelectedListener { menue ->
            if (menue.itemId == R.id.nav_logout) {
                Log.d("LOGIN", "Logout Clicked")

                showDialog()
            }

            //This is for maintaining the behavior of the Navigation view
            onNavDestinationSelected(menue, navController)
            //This is for closing the drawer after acting on it
            drawerLayout.closeDrawer(GravityCompat.START)
            return@setNavigationItemSelectedListener true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {

                startActivity(Intent(this@MainActivity, SettingsActivity::class.java))

            }
            else -> return super.onOptionsItemSelected(item)
        }
        return false

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }



    private fun showDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.logout_dialog)
        dialog.window?.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        dialog.window?.setBackgroundDrawableResource(R.color.black_transparent)

        val header = dialog.findViewById(R.id.txtLogoutHeader) as TextView
        val body = dialog.findViewById(R.id.txtDialogDescription) as TextView
        header.text = getString(R.string.logout_head)
        body.text = getString(R.string.logout_body)

        val yesBtn = dialog.findViewById(R.id.txtYes) as TextView
        val noBtn = dialog.findViewById(R.id.txtNo) as TextView
        yesBtn.text = getString(R.string.yes)
        noBtn.text = getString(R.string.no)

        yesBtn.setOnClickListener {
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            finish()
            PreferenceUtils.saveBoolean(this, AppUtils.IS_LOGGED_IN, false)
            //dialog.dismiss()
        }
        noBtn.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()

    }

    private fun updateDeviceTokenAPI() {
        val deviceToken: String? = PreferenceUtils.getString(this, AppUtils.FCM_TOKEN)
        val token = PreferenceUtils.getString(this, AppUtils.ACCESS_TOKEN)

        Log.i(TAG, "Device Token : $deviceToken \nUser Token: $token")

        val apiService = ApiUtility.getInstance().create(ApiInterface::class.java)
        apiService.upDateDeviceTokenRequest(
            UpdateTokenRequest(
                deviceToken!!, AppUtils.DEVICE_TYPE, token!!
            )
        ).enqueue(object : Callback<UpdateTokenResponse> {
            override fun onResponse(
                call: Call<UpdateTokenResponse>, response: Response<UpdateTokenResponse>
            ) {

                if (response.code() == 200) {
                    Log.i(TAG, "post submitted to API." + response.body()!!)

                } else {
                    val jObjError = JSONObject(response.errorBody()!!.string())
                    val errorMessage = jObjError.getString("message")
                    Toast.makeText(
                        this@MainActivity, errorMessage, Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<UpdateTokenResponse>, t: Throwable) {
                Log.i(TAG, "post submitted to API." + t.message)
            }
        })

    }

    /*****
     * Method Name:- askNotificationPermission()
     * Params:- No Args
     * Purpose:- To request for the  POST_NOTIFICATIONS permission for the Devices with Android 11+
     * This is only necessary for API level >= 33 (TIRAMISU)
     */
    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                Log.d(TAG, "Notification Permission already Granted")
                updateDeviceTokenAPI()
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                Log.d(TAG, "Request for Notification Permission")
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    // Declare the launcher at the top of your Activity/Fragment:
    private val requestPermissionLauncher = registerForActivityResult<String, Boolean>(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
            Log.d(TAG, "Notification Permission Granted")
            updateDeviceTokenAPI()
        } else {
            // TODO: Inform user that that your app will not show notifications.
            Log.d(TAG, "Notification Permission Denied")
        }
    }
}