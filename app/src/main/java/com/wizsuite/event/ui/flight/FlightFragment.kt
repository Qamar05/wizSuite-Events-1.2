package com.wizsuite.event.ui.flight

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.wizsuite.event.api.ApiInterface
import com.wizsuite.event.api.ApiUtility
import com.wizsuite.event.databinding.FragmentFlightBinding
import com.wizsuite.event.listeners.OnListItemClickedListener
import com.wizsuite.event.model.EventAmenityRequest
import com.wizsuite.event.model.EventDetailsNew
import com.wizsuite.event.model.FlightDetail
import com.wizsuite.event.model.FlightDetailResponse
import com.wizsuite.event.utils.AppUtils
import com.wizsuite.event.utils.PreferenceUtils
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class FlightFragment : Fragment(), OnListItemClickedListener {
    val TAG: String = "EVENT_DETAILS"
    lateinit var binding: FragmentFlightBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFlightBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        callFlightDetailsAPI()
    }

    private fun callFlightDetailsAPI() {
        val assignedID = PreferenceUtils.getString(requireContext(), AppUtils.ASSIGNED_ID)
        val token = PreferenceUtils.getString(requireContext(), AppUtils.ACCESS_TOKEN)
        Log.d(TAG, "Assigned ID : $assignedID Token: $token")
        val apiService = ApiUtility.getInstance().create(ApiInterface::class.java)
        apiService.getFlightDetailsRequest(EventAmenityRequest(assignedID!!,AppUtils.FLIGHT,token!!))
            .enqueue(object : Callback<FlightDetailResponse> {
                override fun onResponse(call: Call<FlightDetailResponse>, response: Response<FlightDetailResponse>) {

                    if (response.code() == 200) {
                        Log.d(TAG, "Response : " + response.body()!!)
                        val eventList = response.body()!!.flight_detail

                        val flightAdapter =
                            FlightDetailsAdapter(eventList as ArrayList<FlightDetail>,this@FlightFragment)
                        binding.rvFlightDetails.layoutManager = LinearLayoutManager(context)
                        binding.rvFlightDetails.adapter = flightAdapter
                    } else {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        val errorMessage = jObjError.getString("message")
                        Toast.makeText(
                            context,
                            errorMessage,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<FlightDetailResponse>, t: Throwable) {
                    Log.d(TAG, "post submitted to API." + t.message)
                }
            })
    }



    override fun onItemClicked(eventDetails: EventDetailsNew) {
        Log.d(TAG, "onItemClicked")
    }

    override fun agendaImage(agendaImage: String) {
        Log.d(TAG, "agendaImage")
        AppUtils.openBrowser(requireContext(),agendaImage)
    }
}