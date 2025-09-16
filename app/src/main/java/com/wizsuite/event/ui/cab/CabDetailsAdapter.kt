package com.wizsuite.event.ui.cab

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wizsuite.event.listeners.MakeCallListener

import com.wizsuite.event.model.CabDetail
import com.wizsuite.event.databinding.ItemCabBinding
import com.wizsuite.event.utils.DateFormatHelper

class CabDetailsAdapter(
    private val eventList: ArrayList<CabDetail>, private val callListener: MakeCallListener
) :
    RecyclerView.Adapter<CabDetailsAdapter.CabViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CabViewHolder {
        val binding = ItemCabBinding.inflate(LayoutInflater.from(parent.context))
        return CabViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CabViewHolder, position: Int) {
        holder.binding.txtCabNumber.text = eventList[position].cab_no
        holder.binding.txtDriverName.text = eventList[position].driver_name
        holder.binding.txtCabSourceCode.text = eventList[position].pickup_point
        holder.binding.txtCabDestinationCode.text = eventList[position].drop_point

        val pickUpDateAndTime =  "${eventList[position].pick_up_date}  ${eventList[position].pickup_time}:00"
        holder.binding.txtPickupTime.text = DateFormatHelper.dateFormat(
            DateFormatHelper.yyyy_MM_dd_HH_mm_ss,
            DateFormatHelper.EEE_MMM_dd_yyyy_hh_mm,pickUpDateAndTime)

        holder.binding.txtDescription.text = eventList[position].description
        holder.binding.txtCallDriver.setOnClickListener {
            callListener.onCallClicked(eventList[position].driver_contact_no,false)
        }
        holder.binding.txtWhatsAppDriver.setOnClickListener {
            callListener.onCallClicked(eventList[position].driver_contact_no,true)
        }

    }

    override fun getItemCount(): Int {
        return eventList.size
    }

    class CabViewHolder(val binding: ItemCabBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }
}