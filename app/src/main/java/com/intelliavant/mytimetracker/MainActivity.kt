package com.intelliavant.mytimetracker

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.intelliavant.mytimetracker.utils.StopwatchManager
import com.intelliavant.mytimetracker.viewmodel.WorkListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val workListViewModel: WorkListViewModel by viewModels()
    private lateinit var sm: StopwatchManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request foereground service permission
        requestPermissions(
            arrayOf(android.Manifest.permission.FOREGROUND_SERVICE),
            PackageManager.PERMISSION_GRANTED
        )

        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        sm = StopwatchManager.getInstance(this)
        sm.create()

        // show the work type bottom sheet when FAB is clicked
        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            val fragment = WorkTypeListFragment()
            fragment.onCreateWorkListener = { workType ->
                Log.d("STOPWATCH", "workType ${workType.id} clicked")
                lifecycleScope.launch {
                    val workId = workListViewModel.createWork(workType.name, workType)
                    val workName = workType.name

                    // Start StopwatchService
                    sm.start(workId, workName)

                    // Move to stopwatch fragment
                    findNavController(R.id.nav_host_fragment).navigate(R.id.action_workListFragment_to_stopwatchFragment)
                }
            }
            fragment.show(supportFragmentManager, fragment.tag)
        }
    }

    override fun onResume() {
        super.onResume()
        // Recover stopwatch fragment if service is running and current fragment is the main fragment
        if (sm.isStopwatchServiceRunning()) {
            val navController = findNavController(R.id.nav_host_fragment)
            if (navController.currentDestination?.id == R.id.workListFragment) {
                navController.navigate(R.id.action_workListFragment_to_stopwatchFragment)
            }
        }

    }

    override fun onDestroy() {
        StopwatchManager.getInstance(this).destroy()
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        // If back button pressed, find out if current fragment is the stopwatch fragment
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        if (navHostFragment != null) {
            val fragment = navHostFragment.childFragmentManager.fragments[0]

            // if currently showing stopwatch, then call its stopWork
            if (fragment is StopwatchFragment) {
                fragment.stopWork()
                return
            }
        }

        // otherwise simply pause the activity
        super.onBackPressed()
    }
}