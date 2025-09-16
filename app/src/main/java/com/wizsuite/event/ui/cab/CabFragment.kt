package com.wizsuite.event.ui.cab

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.wizsuite.event.R
import com.wizsuite.event.api.ApiInterface
import com.wizsuite.event.api.ApiUtility
import com.wizsuite.event.databinding.FragmentCabBinding
import com.wizsuite.event.listeners.MakeCallListener
import com.wizsuite.event.model.CabDetail
import com.wizsuite.event.model.CabDetailsResponse
import com.wizsuite.event.model.EventAmenityRequest
import com.wizsuite.event.utils.AppUtils
import com.wizsuite.event.utils.PreferenceUtils
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CabFragment : Fragment(), MakeCallListener {
    val TAG: String = "EVENT_DETAILS"
    lateinit var binding: FragmentCabBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCabBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        callFlightDetailsAPI()
    }

    private fun callFlightDetailsAPI() {
        //  binding.loading.visibility = View.INVISIBLE
        val assignedID = PreferenceUtils.getString(requireContext(), AppUtils.ASSIGNED_ID)
        val token = PreferenceUtils.getString(requireContext(), AppUtils.ACCESS_TOKEN)
        Log.d(TAG, "Assigned ID : $assignedID Token: $token")
        val apiService = ApiUtility.getInstance().create(ApiInterface::class.java)
        apiService.getCabDetailsRequest(
            EventAmenityRequest(assignedID!!, AppUtils.CAB, token!!)
        )
            .enqueue(object : Callback<CabDetailsResponse> {
                override fun onResponse(
                    call: Call<CabDetailsResponse>,
                    response: Response<CabDetailsResponse>
                ) {

                    if (response.code() == 200) {
                        Log.d(TAG, "Response : " + response.body()!!)
                        val eventList = response.body()!!.cab_detail

                        val flightAdapter =
                            CabDetailsAdapter(eventList as ArrayList<CabDetail>, this@CabFragment)
                        binding.rvCabDetails.layoutManager = LinearLayoutManager(context)
                        binding.rvCabDetails.adapter = flightAdapter
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

                override fun onFailure(call: Call<CabDetailsResponse>, t: Throwable) {
                    Log.d(TAG, "post submitted to API." + t.message)
                }
            })
    }

    override fun onCallClicked(contactNumber: String, isWhatsapp: Boolean) {
        Log.d(TAG, "Phone Number : $contactNumber")
        if (isWhatsapp) {
            AppUtils.openWhatsapp(requireContext(), contactNumber)
        } else {
            AppUtils.call(requireContext(), contactNumber)
        }

    }
}