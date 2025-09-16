package com.wizsuite.event.ui.contact

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
import com.wizsuite.event.databinding.FragmentContactBinding
import com.wizsuite.event.listeners.MakeCallListener
import com.wizsuite.event.model.AssistantDetail
import com.wizsuite.event.model.AssistantDetailResponse
import com.wizsuite.event.model.EventAmenityRequest
import com.wizsuite.event.utils.AppUtils
import com.wizsuite.event.utils.PreferenceUtils
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ContactFragment : Fragment(),MakeCallListener {

    val TAG: String = "EVENT_DETAILS"
    lateinit var binding: FragmentContactBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentContactBinding.inflate(layoutInflater, container, false)
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
        apiService.getAssistanceTeamRequest(
            EventAmenityRequest(assignedID!!,
                AppUtils.ASSISTANT,token!!)
        )
            .enqueue(object : Callback<AssistantDetailResponse> {
                override fun onResponse(call: Call<AssistantDetailResponse>, response: Response<AssistantDetailResponse>) {

                    if (response.code() == 200) {
                        Log.d(TAG, "Response : " + response.body()!!)
                        val assistantList = response.body()!!.data

                        val flightAdapter =
                            AssistanceTeamDetailsAdapter(assistantList as ArrayList<AssistantDetail>,this@ContactFragment)
                        binding.rvSupportTeamDetails.layoutManager = LinearLayoutManager(context)
                        binding.rvSupportTeamDetails.adapter = flightAdapter
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

                override fun onFailure(call: Call<AssistantDetailResponse>, t: Throwable) {
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