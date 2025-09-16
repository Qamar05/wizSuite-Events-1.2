package com.wizsuite.event.ui.agenda

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import com.wizsuite.event.R
import com.wizsuite.event.databinding.FragmentAgendaBinding
import com.wizsuite.event.utils.AppUtils
import com.wizsuite.event.utils.PreferenceUtils


class AgendaFragment : Fragment() {
    lateinit var binding: FragmentAgendaBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        binding = FragmentAgendaBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        setAgendaImage()
    }

    private fun setAgendaImage() {
        val agendaImageUrl = PreferenceUtils.getString(requireContext(), AppUtils.AGENDA_IMAGE)
        Picasso.get().load(agendaImageUrl).into(binding.imgAgenda)
    }
}