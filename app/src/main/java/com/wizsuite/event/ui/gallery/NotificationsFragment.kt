package com.wizsuite.event.ui.gallery

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.wizsuite.event.api.ApiInterface
import com.wizsuite.event.api.ApiUtility

import com.wizsuite.event.databinding.FragmentNotificationsBinding
import com.wizsuite.event.model.EventRequest
import com.wizsuite.event.model.Notification
import com.wizsuite.event.model.NotificationResponse
import com.wizsuite.event.utils.AppUtils
import com.wizsuite.event.utils.PreferenceUtils
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    val TAG: String = "UPCOMING_EVENT"


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        callNotificationAPI()
    }


    private fun callNotificationAPI() {
        val token = PreferenceUtils.getString(requireContext(), AppUtils.ACCESS_TOKEN)
        Log.d(TAG, "Token : $token")
        val apiService = ApiUtility.getInstance().create(ApiInterface::class.java)
        apiService.getNotificationRequest(EventRequest(token!!))
            .enqueue(object : Callback<NotificationResponse> {
                override fun onResponse(call: Call<NotificationResponse>, response: Response<NotificationResponse>) {
                    if (response.code() == 200) {
                        Log.d(TAG, "Response : " + response.body()!!)
                        val notificationList = response.body()!!.data

                        val notificationAdapter =
                            NotificationAdapter(notificationList as ArrayList<Notification>)
                        binding.rvNotificationList.layoutManager =  LinearLayoutManager(context)
                        binding.rvNotificationList.adapter = notificationAdapter
                    } else {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        val errorMessage = jObjError.getString("message")
                        Toast.makeText(
                            context,
                            errorMessage,
                            Toast.LENGTH_LONG
                        ).show()
                        /* binding.txtMessage.text = errorMessage
                         binding.txtMessage.visibility = View.VISIBLE
                         binding.imgNoDataFound.visibility = View.INVISIBLE*/
                    }
                }

                override fun onFailure(call: Call<NotificationResponse>, t: Throwable) {
                    Log.d(TAG, "post submitted to API." + t.message)
                }
            })
    }



}