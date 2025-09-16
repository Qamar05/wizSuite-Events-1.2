package com.wizsuite.event.ui.slideshow

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.wizsuite.event.R
import com.wizsuite.event.api.ApiInterface
import com.wizsuite.event.api.ApiUtility
import com.wizsuite.event.databinding.FragmentSupportBinding
import com.wizsuite.event.model.SupportRequest
import com.wizsuite.event.model.SupportResponse
import com.wizsuite.event.utils.AppUtils
import com.wizsuite.event.utils.PreferenceUtils
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SupportFragment : Fragment(), OnClickListener {
    val TAG: String = "SUPPORT"
    lateinit var binding: FragmentSupportBinding
    lateinit var supportViewModel: SupportViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentSupportBinding.inflate(layoutInflater, container, false)
        supportViewModel = ViewModelProvider(this)[SupportViewModel::class.java]
        observeViewModel()
        return binding.root
    }

    private fun observeViewModel() {
        supportViewModel.supportResponse.observe(viewLifecycleOwner) {response->
            Log.d(TAG, "Response : $response")
            val eventList = response.message
            Toast.makeText(
                context,
                eventList,
                Toast.LENGTH_LONG
            ).show()
        }
        supportViewModel.sendOTPError.observe(viewLifecycleOwner) { errorMessage ->
            Toast.makeText(
                context,
                errorMessage,
                Toast.LENGTH_LONG
            ).show()
        }
        supportViewModel.loading.observe(viewLifecycleOwner) {
            Log.d(TAG, "Loading : $it")
        }
    }

    override fun onResume() {
        super.onResume()
        setOnClickListener()
        setData()
    }

    private fun setData() {
        val guestName = PreferenceUtils.getString(
            requireContext(),
            AppUtils.GUEST_NAME
        )

        val guestEmail = PreferenceUtils.getString(
            requireContext(),
            AppUtils.GUEST_EMAIL
        )

        val guestPhone = PreferenceUtils.getString(
            requireContext(),
            AppUtils.GUEST_PHONE
        )

        binding.txtName.text = guestName
        if (guestPhone != null) {
            binding.txtPhone.text = guestPhone
        } else {
            binding.txtPhone.text = ""
        }


    }

    private fun setOnClickListener() {
        binding.txtSubmit.setOnClickListener(this)

        Log.d(TAG, "Set OnClickListener")
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.txtSubmit -> {
                Log.d(TAG, "In R.id.login")
                if (validateForm()) {
                    val token = PreferenceUtils.getString(requireContext(), AppUtils.ACCESS_TOKEN)
                    Log.d(TAG, "Token : $token")
                    supportViewModel.refreshSupport(token!!, binding.txtMessage.text.toString().trim())
                   // callLoginAPI(binding.txtMessage.text.toString().trim())
                }
            }

            else -> Log.d(TAG, "In Else Part")
        }
    }

    private fun callLoginAPI(message: String) {
        val token = PreferenceUtils.getString(requireContext(), AppUtils.ACCESS_TOKEN)
        Log.d(TAG, "Token : $token")
        val apiService = ApiUtility.getInstance().create(ApiInterface::class.java)
        apiService.supportRequest(SupportRequest(token!!, message))
            .enqueue(object : Callback<SupportResponse> {
                override fun onResponse(
                    call: Call<SupportResponse>,
                    response: Response<SupportResponse>
                ) {

                    if (response.code() == 200) {
                        Log.d(TAG, "Response : " + response.body()!!)
                        val eventList = response.body()!!.message
                        Toast.makeText(
                            context,
                            eventList,
                            Toast.LENGTH_LONG
                        ).show()
                        // binding.txtMessage.text= "not"
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

                override fun onFailure(call: Call<SupportResponse>, t: Throwable) {
                    Log.d(TAG, "post submitted to API." + t.message)
                }
            })
    }

    private fun validateForm(): Boolean {
        if (binding.txtMessage.text.isEmpty()) {
            Toast.makeText(context, R.string.empty_support_message, Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

}