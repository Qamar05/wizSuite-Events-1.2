package com.wizsuite.event.ui.activities.home

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.wizsuite.event.ui.oldevents.OldEventsFragment
import com.wizsuite.event.ui.upcomingevents.UpComingEventsFragment

class ViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        // Return a NEW fragment instance in createFragment(int)
        //val fragment
        return when (position) {
            0 -> UpComingEventsFragment()
            1 -> OldEventsFragment()
            else -> UpComingEventsFragment()
        }
    }

}