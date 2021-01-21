package com.intelliavant.mytimetracker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.intelliavant.mytimetracker.utils.StopwatchManager
import com.intelliavant.mytimetracker.viewmodel.WorkListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val workListViewModel: WorkListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // init service manager
        StopwatchManager.getInstance(this)

        // Request foereground service permission
        requestPermissions(
            arrayOf(android.Manifest.permission.FOREGROUND_SERVICE),
            PackageManager.PERMISSION_GRANTED
        )

        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        // show the work type bottom sheet when FAB is clicked
        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            val fragment = WorkTypeListFragment()
            fragment.onCreateWorkListener = { workType ->
                Log.d("STOPWATCH", "workType ${workType.id} clicked")
                lifecycleScope.launch {
                    val workId = workListViewModel.createWork(workType)

                    // Move to stopwatch fragment
                    val bundle = bundleOf("workId" to workId)
                    findNavController(R.id.nav_host_fragment).navigate(R.id.action_workListFragment_to_stopwatchFragment, bundle)

//                    // Start StopwatchActivity
//                    val intent = Intent(context, StopwatchFragment::class.java).apply {
//                        putExtra("workId", workId)
//                    }
//                    startActivity(intent)
                }
            }
            fragment.show(supportFragmentManager, fragment.tag)
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
}