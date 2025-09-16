package com.wizsuite.event.listeners

interface MakeCallListener {
    fun onCallClicked(contactNumber:String,isWhatsapp:Boolean)
}