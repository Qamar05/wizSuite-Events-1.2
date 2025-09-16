package com.wizsuite.event.ui.upcomingevents

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.wizsuite.event.api.ApiInterface
import com.wizsuite.event.api.ApiUtility
import com.wizsuite.event.databinding.FragmentUpComingEventsBinding
import com.wizsuite.event.listeners.OnListItemClickedListener
import com.wizsuite.event.model.EventDetailsNew
import com.wizsuite.event.model.EventRequest
import com.wizsuite.event.model.EventResponse
import com.wizsuite.event.ui.activities.EventDetailsActivity
import com.wizsuite.event.utils.AppUtils
import com.wizsuite.event.utils.PreferenceUtils
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class UpComingEventsFragment : Fragment(), OnListItemClickedListener {
    val TAG: String = "UPCOMING_EVENT"
    lateinit var binding: FragmentUpComingEventsBinding
    lateinit var upComingEventsViewModel: UpComingEventsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentUpComingEventsBinding.inflate(layoutInflater, container, false)
        upComingEventsViewModel = ViewModelProvider(this)[UpComingEventsViewModel::class.java]
        observeViewModel()
        return binding.root
    }

    private fun observeViewModel() {
        upComingEventsViewModel.upcomingEventResponse.observe(viewLifecycleOwner) {
            setData(it)
        }
        upComingEventsViewModel.sendOTPError.observe(viewLifecycleOwner) {
            Log.d(TAG, "Error : $it")
           // Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            binding.txtMessage.text = it
            binding.txtMessage.visibility = VISIBLE
            binding.imgNoDataFound.visibility = INVISIBLE
        }
        upComingEventsViewModel.loading.observe(viewLifecycleOwner) {

        }
    }

    private fun setData(response: EventResponse) {
        val eventList = response.data

        val itemUpcomingEventAdapter =
            UpcomingEventAdapter(
                eventList as ArrayList<EventDetailsNew>,
                this@UpComingEventsFragment
            )
        binding.rvUpComingEvents.layoutManager = GridLayoutManager(context, 2)
        binding.rvUpComingEvents.adapter = itemUpcomingEventAdapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //callLoginAPI()
        Log.d(TAG, "onViewCreated")
    }

    override fun onResume() {
        super.onResume()
        binding.txtMessage.visibility = INVISIBLE
        binding.imgNoDataFound.visibility = INVISIBLE
        Log.d(TAG, "onResume")
       // callLoginAPI()
        val token = PreferenceUtils.getString(requireContext(), AppUtils.ACCESS_TOKEN)
        Log.d(TAG, "Token : $token")
        upComingEventsViewModel.refreshUpcomingEvent(token!!)
    }

    private fun callLoginAPI() {
        val token = PreferenceUtils.getString(requireContext(), AppUtils.ACCESS_TOKEN)
        Log.d(TAG, "Token : $token")
        val apiService = ApiUtility.getInstance().create(ApiInterface::class.java)
        apiService.upComingEventRequest(EventRequest(token!!))
            .enqueue(object : Callback<EventResponse> {
                override fun onResponse(
                    call: Call<EventResponse>,
                    response: Response<EventResponse>
                ) {

                    if (response.code() == 200) {
                        Log.d(TAG, "Response : " + response.body()!!)
                        val eventList = response.body()!!.data

                        val itemUpcomingEventAdapter =
                            UpcomingEventAdapter(
                                eventList as ArrayList<EventDetailsNew>,
                                this@UpComingEventsFragment
                            )
                        binding.rvUpComingEvents.layoutManager = GridLayoutManager(context, 2)
                        binding.rvUpComingEvents.adapter = itemUpcomingEventAdapter
                    } else {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        val errorMessage = jObjError.getString("message")
                        /* Toast.makeText(
                             context,
                             errorMessage,
                             Toast.LENGTH_LONG
                         ).show()*/
                        binding.txtMessage.text = errorMessage
                        binding.txtMessage.visibility = VISIBLE
                        binding.imgNoDataFound.visibility = INVISIBLE
                    }
                }

                override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                    Log.d(TAG, "post submitted to API." + t.message)
                }
            })
    }


    override fun onItemClicked(eventDetails: EventDetailsNew) {
        PreferenceUtils.saveString(
            requireContext(),
            AppUtils.ASSIGNED_ID,
            eventDetails.assignId
        )
        PreferenceUtils.saveString(
            requireContext(),
            AppUtils.AGENDA_IMAGE,
            eventDetails.upload_agenda
        )
        PreferenceUtils.saveString(
            requireContext(),
            AppUtils.VENUE_IMAGE,
            eventDetails.venue_image
        )
        PreferenceUtils.saveString(
            requireContext(),
            AppUtils.VENUE_NAME,
            eventDetails.venue_name
        )
        PreferenceUtils.saveString(
            requireContext(),
            AppUtils.VENUE_LINK,
            eventDetails.venue_link
        )
        PreferenceUtils.saveString(
            requireContext(),
            AppUtils.VENUE_ADDRESS,
            eventDetails.venue_address
        )
        PreferenceUtils.saveString(
            requireContext(),
            AppUtils.EVENT_DATE,
            eventDetails.events_start_date
        )
        PreferenceUtils.saveString(
            requireContext(),
            AppUtils.EVENT_TIME,
            eventDetails.event_start_time
        )

        startActivity(Intent(context, EventDetailsActivity::class.java))
    }

    override fun agendaImage(agendaImage: String) {

    }


}