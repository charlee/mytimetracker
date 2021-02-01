package com.intelliavant.mytimetracker

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
@AndroidEntryPoint
class WorkListPagerFragment : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var dateTabLayout: TabLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_work_list_pager, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity?.let { activity ->
            val viewPagerAdapter = WorkListPagerAdapter(activity)
            viewPager = view.findViewById(R.id.view_pager)
            viewPager.adapter = viewPagerAdapter
            viewPager.setCurrentItem(viewPagerAdapter.itemCount - 1, false)

            dateTabLayout = view.findViewById(R.id.date_tab_layout)
            TabLayoutMediator(dateTabLayout, viewPager) { tab, position ->
                val lastIndex = viewPagerAdapter.itemCount - 1

                val date = LocalDate.now().minusDays((lastIndex - position).toLong())

                val dateText = when(position) {
                    lastIndex -> getString(R.string.today)
                    lastIndex - 1 -> getString(R.string.yesterday)
                    else -> date.toString()
                }

                val weekdayText = date.format(DateTimeFormatter.ofPattern("E"))

                tab.text = "$dateText ($weekdayText)"
            }.attach()
        }


        // Setup appbar
        // https://developer.android.com/guide/navigation/navigation-ui#support_app_bar_variations
        // TODO: should use this? or use `setSupportActionBar` in the `MainActivity`?
//        val navController = findNavController()
//        val appBarConfiguration = AppBarConfiguration(navController.graph)
//
////        view.findViewById<Toolbar>(R.id.work_list_toolbar).setupWithNavController(navController, appBarConfiguration)
//        setHasOptionsMenu(true)

        super.onViewCreated(view, savedInstanceState)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        Log.d("STOPWATCH", "WorkListPagerFragment.onCreateOptionsMenu() called")
        inflater.inflate(R.menu.menu_work_list, menu)
    }
}