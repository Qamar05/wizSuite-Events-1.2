package com.wizsuite.event.utils

import android.view.KeyEvent
import android.view.View
import android.view.View.OnKeyListener
import android.widget.EditText

class GenericKeyEvent internal constructor(private val currentTextView: EditText, private val previousTextView: EditText?):OnKeyListener{
    override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
        if(event!!.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL && !currentTextView.text.isEmpty()) {
            //If current is empty then previous EditText's number will also be deleted
            currentTextView!!.text = null
            previousTextView?.requestFocus()
            return true
        }
        return false
    }
}