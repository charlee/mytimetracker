package com.intelliavant.mytimetracker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private fun createNotificationChannel() {
        val channelId = getString(R.string.notification_channel_id)
        val name = getString(R.string.notification_channel_name)
        val descriptionText = getString(R.string.notification_channel_description)
        val mChannel = NotificationChannel(channelId, name, NotificationManager.IMPORTANCE_DEFAULT)
        mChannel.description = descriptionText

        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request foereground service permission
        requestPermissions(
            arrayOf(android.Manifest.permission.FOREGROUND_SERVICE),
            PackageManager.PERMISSION_GRANTED
        )

        // create notification channel
        createNotificationChannel()


        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        // show the work type bottom sheet when FAB is clicked
        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            val fragment = WorkTypeListFragment()
            fragment.show(supportFragmentManager, fragment.tag)
        }
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