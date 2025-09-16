package com.wizsuite.event.ui.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.wizsuite.event.R
import com.wizsuite.event.ui.activities.home.MainActivity
import com.wizsuite.event.ui.activities.login.LoginActivity
import com.wizsuite.event.utils.AppUtils
import com.wizsuite.event.utils.PreferenceUtils

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        Handler(Looper.getMainLooper()).postDelayed(Runnable {

            if (PreferenceUtils.getBoolean(this@SplashActivity,AppUtils.IS_LOGGED_IN)){
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish()
            }else{
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                finish()
            }


        }, 2000)
    }
}