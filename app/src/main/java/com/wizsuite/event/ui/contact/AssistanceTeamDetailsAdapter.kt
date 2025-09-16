package com.wizsuite.event.ui.contact

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wizsuite.event.model.AssistantDetail
import com.wizsuite.event.databinding.ItemAssistanceTeamBinding

class AssistanceTeamDetailsAdapter(
    private val eventList: ArrayList<AssistantDetail>, private val callListener: ContactFragment
) :
    RecyclerView.Adapter<AssistanceTeamDetailsAdapter.UpcomingEventViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UpcomingEventViewHolder {
        val binding = ItemAssistanceTeamBinding.inflate(LayoutInflater.from(parent.context))
        return UpcomingEventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UpcomingEventViewHolder, position: Int) {

        holder.binding.txtAssistantName.text = eventList[position].name
        holder.binding.txtAssistantDesignation.text = eventList[position].designation
        holder.binding.txtAssistanceType.text = eventList[position].support_type

        holder.binding.rlCallAssistant.setOnClickListener {
            callListener.onCallClicked(eventList[position].contact_no,false)
        }

        holder.binding.rlWhatsAppAssistant.setOnClickListener {
            callListener.onCallClicked(eventList[position].whatsapp_no,true)
        }



    }

    override fun getItemCount(): Int {
        return eventList.size
    }

    class UpcomingEventViewHolder(val binding: ItemAssistanceTeamBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }
}