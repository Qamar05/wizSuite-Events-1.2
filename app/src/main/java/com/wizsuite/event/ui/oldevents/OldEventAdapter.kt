package com.wizsuite.event.ui.oldevents

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wizsuite.event.listeners.OnListItemClickedListener
import com.wizsuite.event.model.EventDetailsNew
import com.wizsuite.event.databinding.ItemEventBinding
import com.wizsuite.event.utils.DateFormatHelper

class OldEventAdapter(private val eventList: ArrayList<EventDetailsNew>, private val itemClickedListener: OnListItemClickedListener) :
    RecyclerView.Adapter<OldEventAdapter.UpcomingEventViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UpcomingEventViewHolder {
        val binding = ItemEventBinding.inflate(LayoutInflater.from(parent.context))
        return UpcomingEventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UpcomingEventViewHolder, position: Int) {
        holder.binding.eventName.text = eventList[position].events_name
        holder.binding.eventDate.text = DateFormatHelper.dateFormat(
            DateFormatHelper.yyyy_MM_dd,
            DateFormatHelper.EEE_MMM_dd_yyyy,eventList[position].events_start_date)
        holder.binding.rlItemEvent.setOnClickListener {
            Log.d("ITEM","Item Clicked, Position : $position")
            itemClickedListener.onItemClicked(eventList[position])
        }
    }

    override fun getItemCount(): Int {
        return eventList.size
    }

    class UpcomingEventViewHolder(val binding: ItemEventBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }
}