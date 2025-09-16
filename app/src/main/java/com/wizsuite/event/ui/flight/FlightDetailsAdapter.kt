package com.wizsuite.event.ui.flight

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wizsuite.event.databinding.ItemFlightBinding
import com.wizsuite.event.listeners.OnListItemClickedListener
import com.wizsuite.event.model.FlightDetail
import com.wizsuite.event.utils.DateFormatHelper

class FlightDetailsAdapter(
    private val eventList: ArrayList<FlightDetail>,
    private val itemClickedListener: OnListItemClickedListener
) :
    RecyclerView.Adapter<FlightDetailsAdapter.UpcomingEventViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UpcomingEventViewHolder {
        val binding = ItemFlightBinding.inflate(LayoutInflater.from(parent.context))
        return UpcomingEventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UpcomingEventViewHolder, position: Int) {

        val sourceDestination =
            "${eventList[position].from_location} to ${eventList[position].to_location}"
        holder.binding.txtSourceAndDestination.text = sourceDestination
        holder.binding.txtFlightSource.text = eventList[position].from_location
        holder.binding.txtFlightDestination.text = eventList[position].to_location
        holder.binding.txtSourceCode.text = eventList[position].from_location
        holder.binding.txtDestinationCode.text = eventList[position].to_location

        val checkInDateAndTime =
            "${eventList[position].checkin_date} ${eventList[position].checkin_time}:00"

        holder.binding.txtFlightSourceDateAndTime.text = DateFormatHelper.dateFormat(
            DateFormatHelper.yyyy_MM_dd_HH_mm_ss,
            DateFormatHelper.EEE_MMM_dd_yyyy_hh_mm, checkInDateAndTime
        )

        val checkOutDateAndTime =
            "${eventList[position].checkin_date} ${eventList[position].checkin_time}:00"
        Log.d("DATE", checkOutDateAndTime)
        holder.binding.txtFlightDestinationDateAndTime.text = DateFormatHelper.dateFormat(
            DateFormatHelper.yyyy_MM_dd_HH_mm_ss,
            DateFormatHelper.EEE_MMM_dd_yyyy, checkOutDateAndTime
        )

        holder.binding.txtFlightNumber.text = eventList[position].flight_no
        holder.binding.txtFlightLayoverPlace.text = eventList[position].layover
        Log.d("DATE", eventList[position].checkin_link)

        holder.binding.txtCheckIn.setOnClickListener {
            itemClickedListener.agendaImage(eventList[position].checkin_link)
        }

        holder.binding.txtDescription.text = eventList[position].description


    }

    override fun getItemCount(): Int {
        return eventList.size
    }

    class UpcomingEventViewHolder(val binding: ItemFlightBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }
}