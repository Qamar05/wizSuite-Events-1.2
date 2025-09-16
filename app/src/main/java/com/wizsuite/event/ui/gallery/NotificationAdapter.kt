package com.wizsuite.event.ui.gallery

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wizsuite.event.databinding.ItemNotificationBinding
import com.wizsuite.event.model.Notification
import com.wizsuite.event.utils.DateFormatHelper
import kotlin.toString

class NotificationAdapter(private val notificationList: ArrayList<Notification>) :
    RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding = ItemNotificationBinding.inflate(LayoutInflater.from(parent.context))
        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.binding.txtHeading.text = notificationList[position].title
        holder.binding.txtMessage.text = notificationList[position].message_body
        Log.d("DATE", "Date : "+DateFormatHelper.dateFormat(
            DateFormatHelper.yyyy_MM_dd_HH_mm_ss,
            DateFormatHelper.EEE_MMM_dd_yyyy,notificationList[position].created_at).toString()
        )

        Log.d("DATE","Time : "+ DateFormatHelper.dateFormat(
            DateFormatHelper.yyyy_MM_dd_HH_mm_ss,
            DateFormatHelper.h_mm_AM_PM,notificationList[position].created_at).toString()
        )
        holder.binding.txtDate.text = DateFormatHelper.dateFormat(DateFormatHelper.yyyy_MM_dd_HH_mm_ss, DateFormatHelper.EEE_MMM_dd_yyyy,notificationList[position].created_at).toString()
        holder.binding.txtTime.text = DateFormatHelper.dateFormat(DateFormatHelper.yyyy_MM_dd_HH_mm_ss, DateFormatHelper.h_mm_AM_PM,notificationList[position].created_at).toString()
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }

    class NotificationViewHolder(val binding: ItemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root)
}