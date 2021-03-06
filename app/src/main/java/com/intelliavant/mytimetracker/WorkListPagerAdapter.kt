package com.intelliavant.mytimetracker

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import java.time.LocalDate

private const val NUM_PAGES = 14

typealias OnResumeWorkListener = OnWorkClickListener

class WorkListPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {

    override fun getItemCount(): Int = NUM_PAGES

    var onResumeWorkListener: OnResumeWorkListener? = null

    override fun createFragment(position: Int): Fragment {
        val fragment = WorkListFragment()
        fragment.arguments = bundleOf("daysBack" to (itemCount - position - 1))
        fragment.onWorkClickListener = onResumeWorkListener

        return fragment
    }
}