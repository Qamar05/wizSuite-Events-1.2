package com.wizsuite.event.listeners

import com.wizsuite.event.model.EventDetailsNew

interface OnListItemClickedListener {

    fun onItemClicked(eventDetails: EventDetailsNew)
    fun agendaImage(agendaImage: String)


}