package com.intelliavant.mytimetracker

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat

/**
 * A simple [Fragment] subclass.
 * Use the [SettingsAboutFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingsAboutFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_about, rootKey)

        val context = requireActivity()
        val info = context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_ACTIVITIES)

        val versionPref = findPreference<Preference>("version")
        versionPref?.summary = info.versionName
    }
}