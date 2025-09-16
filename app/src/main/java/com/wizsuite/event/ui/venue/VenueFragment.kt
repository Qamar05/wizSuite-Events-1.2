package com.wizsuite.event.ui.venue

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import com.wizsuite.event.databinding.FragmentVenueBinding
import com.wizsuite.event.utils.AppUtils
import com.wizsuite.event.utils.PreferenceUtils


class VenueFragment : Fragment() {
    val TAG: String = "EVENT_DETAILS"
    lateinit var binding: FragmentVenueBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentVenueBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val venueImage =  PreferenceUtils.getString(requireContext(), AppUtils.VENUE_IMAGE)
        val venueName = PreferenceUtils.getString(requireContext(), AppUtils.VENUE_NAME)
        val venueLink = PreferenceUtils.getString(requireContext(), AppUtils.VENUE_LINK)
        val venueAddress=  PreferenceUtils.getString(requireContext(), AppUtils.VENUE_ADDRESS)
        val date=  PreferenceUtils.getString(requireContext(), AppUtils.EVENT_DATE)
        val time=  PreferenceUtils.getString(requireContext(), AppUtils.EVENT_TIME)
        Picasso.get().load(venueImage).into(binding.venueImage)
        val dateAndTime = "$date - $time"
        binding.txtEventDate.text = dateAndTime
        binding.txtEventVenueName.text = venueName
        binding.txtVenueAddress.text = venueAddress

        binding.txtVenueLink.setOnClickListener {
            if (venueLink != null) {
                Log.d(TAG, "Venue Link$venueLink")
                AppUtils.openBrowser(requireContext(),venueLink)
            }
        }


    }


}