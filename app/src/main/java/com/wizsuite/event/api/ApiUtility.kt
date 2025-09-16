package com.wizsuite.event.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiUtility {
   private val BASE_URL = "https://xaapps.com/cromptonAPI/mobile/"
    //private val BASE_URL = "https://xaapps.com/cromptonAPI/"

    fun getInstance(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}