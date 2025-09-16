package com.wizsuite.event.ui.venue

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wizsuite.event.listeners.OnListItemClickedListener
import com.wizsuite.event.model.FlightDetail
import com.wizsuite.event.databinding.ItemFlightBinding

class VenueDetailsAdapter(
    private val eventList: ArrayList<FlightDetail>,
    private val itemClickedListener: OnListItemClickedListener
) :
    RecyclerView.Adapter<VenueDetailsAdapter.UpcomingEventViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UpcomingEventViewHolder {
        val binding = ItemFlightBinding.inflate(LayoutInflater.from(parent.context))
        return UpcomingEventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UpcomingEventViewHolder, position: Int) {

        val sourceAndDestination =  "${eventList[position].from_location} to ${eventList[position].to_location}"
        holder.binding.txtSourceAndDestination.text = sourceAndDestination
        holder.binding.txtFlightSource.text = eventList[position].from_location
        holder.binding.txtFlightDestination.text = eventList[position].to_location
        holder.binding.txtSourceCode.text = eventList[position].from_location
        holder.binding.txtDestinationCode.text = eventList[position].to_location
       val flightDateAndTime =
            "${eventList[position].checkin_date}  ${eventList[position].checkin_time}"
        holder.binding.txtFlightSourceDateAndTime.text = flightDateAndTime
        holder.binding.txtFlightNumber.text = eventList[position].flight_no
        holder.binding.txtFlightLayoverPlace.text = eventList[position].layover

        holder.binding.txtFlightLayoverPlace.setOnClickListener {
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