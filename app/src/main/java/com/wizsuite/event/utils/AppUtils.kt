package com.wizsuite.event.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat.startActivity


class AppUtils {
    companion object{
        //Preference Constants
        val FCM_TOKEN:String = "fcmToken"
        val DEVICE_TYPE:String = "android"

        val IS_LOGGED_IN:String = "isLoggedIn"
        val ACCESS_TOKEN:String = "accessToken"
        val GUEST_NAME:String = "guestName"
        val GUEST_EMAIL:String = "guestEmail"
        val GUEST_PHONE:String = "guestPhone"
        val USER_ID:String = "userId"
        val ASSIGNED_ID:String = "assignedId"
        val AGENDA_IMAGE:String = "agendaImage"
        val VENUE_IMAGE:String = "venueImage"
        val VENUE_NAME:String = "venueName"
        val VENUE_LINK:String = "venueLink"
        val VENUE_ADDRESS:String = "venueAddress"
        val EVENT_DATE:String = "eventDate"
        val EVENT_TIME:String = "eventTime"


        // External URLs
        val TERMS_AND_CONDITIONS:String = "https://www.wizsuite.com/termsAndonditionWizSuiteEvents.html"
        val PRIVACY_POLICY:String = "https://www.wizsuite.com/privacyPolicyWizSuiteEvents.html"






        val EVENT_ID:String = "EventId"
        val TO_EVENT_FLIGHT_ID:String = "upFlightId"
        val RETURN_FLIGHT_ID:String = "returnFlightId"
        val TO_EVENT_CAB_ID:String = "airportToEventCabId"
        val RETURN_CAB_ID:String = "eventToAirportCabID"
        val ASSISTANCE_TEAM_ID:String = "assistanceTeamId"

        /*"events_id": "1",
        "user_id": "7",
        "flight_in": "3",
        "flight_out": "1",
        "cab_id_in": "1",
        "cab_id_out": "8",
        "assistance_team_id": "1,2,3",*/

        //Intent Constants
        val EMAIL:String = "email"
        val PHONE:String = "phone"

        //API Constants

        val FLIGHT:String = "flight"
        val CAB:String = "cab"
        val ASSISTANT:String = "assistant"

        fun call(context:Context, phoneNumber:String) {
            val dialIntent = Intent(Intent.ACTION_DIAL)
            dialIntent.data = Uri.parse("tel:" + phoneNumber)
            startActivity(context,dialIntent,null)
        }
        fun openWhatsapp(context:Context, phoneNumber:String) {
            val url = "https://api.whatsapp.com/send?phone=$phoneNumber"
            val whatsappIntent = Intent(Intent.ACTION_VIEW)
            whatsappIntent.data = Uri.parse(url)
            startActivity(context,whatsappIntent,null)
        }
        fun openBrowser(context:Context, url:String) {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(context,browserIntent,null)
        }
    }
}