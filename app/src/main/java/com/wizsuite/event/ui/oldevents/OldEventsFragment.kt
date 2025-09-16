package com.wizsuite.event.ui.oldevents

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.wizsuite.event.api.ApiInterface
import com.wizsuite.event.api.ApiUtility
import com.wizsuite.event.databinding.FragmentOldEventsBinding
import com.wizsuite.event.listeners.OnListItemClickedListener
import com.wizsuite.event.model.EventDetailsNew
import com.wizsuite.event.model.EventRequest
import com.wizsuite.event.model.EventResponse
import com.wizsuite.event.ui.activities.EventDetailsActivity
import com.wizsuite.event.ui.upcomingevents.UpcomingEventAdapter
import com.wizsuite.event.utils.AppUtils
import com.wizsuite.event.utils.PreferenceUtils
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class OldEventsFragment : Fragment(), OnListItemClickedListener {
    val TAG: String = "OLD_EVENT"
    lateinit var binding: FragmentOldEventsBinding
    lateinit var oldEventViewModel: OldEventViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentOldEventsBinding.inflate(layoutInflater, container, false)
        oldEventViewModel = ViewModelProvider(this)[OldEventViewModel::class.java]
        observeViewModel()
        return binding.root
    }

    private fun observeViewModel() {
        oldEventViewModel.pastEventResponse.observe(viewLifecycleOwner) {
            Log.d(TAG, "Response : $it")
            val eventList = it.data

            val itemUpcomingEventAdapter =
                UpcomingEventAdapter(
                    eventList as ArrayList<EventDetailsNew>,
                    this@OldEventsFragment
                )
            binding.rvOldEvents.layoutManager = GridLayoutManager(context, 2)
            binding.rvOldEvents.adapter = itemUpcomingEventAdapter
        }
        oldEventViewModel.sendOTPError.observe(viewLifecycleOwner) {
            Log.d(TAG, "Error : $it")
        }
        oldEventViewModel.loading.observe(viewLifecycleOwner) {
            if (it) {

            } else {
            }
        }

    }

    override fun onResume() {
        super.onResume()
        binding.txtMessage.visibility = INVISIBLE
        binding.imgNoDataFound.visibility = INVISIBLE
        Log.d(TAG, "onResume")
        val token = PreferenceUtils.getString(requireContext(), AppUtils.ACCESS_TOKEN)
        oldEventViewModel.refreshPastEvent(token!!)
        // callLoginAPI()
    }

    private fun callLoginAPI() {
        val token = PreferenceUtils.getString(requireContext(), AppUtils.ACCESS_TOKEN)
        Log.d(TAG, "Token : $token")
        val apiService = ApiUtility.getInstance().create(ApiInterface::class.java)
        apiService.pastEventRequest(EventRequest(token!!))
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
                                this@OldEventsFragment
                            )
                        binding.rvOldEvents.layoutManager = GridLayoutManager(context, 2)
                        binding.rvOldEvents.adapter = itemUpcomingEventAdapter
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
        PreferenceUtils.saveString(requireContext(), AppUtils.ASSIGNED_ID, eventDetails.assignId)
        PreferenceUtils.saveString(
            requireContext(),
            AppUtils.AGENDA_IMAGE,
            eventDetails.upload_agenda
        )
        startActivity(Intent(context, EventDetailsActivity::class.java))
    }

    override fun agendaImage(agendaImage: String) {

    }

}