package com.witkups.rpncalculator.settings

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.preference.ListPreference
import android.preference.Preference
import android.preference.PreferenceActivity
import android.preference.PreferenceFragment
import android.preference.PreferenceManager
import android.view.MenuItem
import com.witkups.rpncalculator.R
import com.witkups.rpncalculator.main.NumberValue

/**
 * A [PreferenceActivity] that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 *
 * See [Android Design: Settings](http://developer.android.com/design/patterns/settings.html)
 * for design guidelines and the [Settings API Guide](http://developer.android.com/guide/topics/ui/settings.html)
 * for more information on developing a Settings UI.
 */
class SettingsActivity : AppCompatPreferenceActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        updateTheme(true)
        super.onCreate(savedInstanceState)
        setupActionBar()
    }

    /**
     * Set up the [android.app.ActionBar], if the API is available.
     */
    private fun setupActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    /**
     * {@inheritDoc}
     */
    override fun onIsMultiPane(): Boolean {
        return isXLargeTablet(this)
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    override fun isValidFragment(fragmentName: String): Boolean {
        return PreferenceFragment::class.java.name == fragmentName
                || GeneralPreferenceFragment::class.java.name == fragmentName
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    class GeneralPreferenceFragment : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.pref_general)
            setHasOptionsMenu(true)

            bindPreferenceSummaryToValue(findPreference(NumberValue.PRECISION_KEY), PrefLis { pref, value ->
                val strVal = value.toString()
                NumberValue.setPrecision(strVal)
                updatePrecision(pref, strVal)
                true
            })
            bindPreferenceSummaryToValue(findPreference(NumberValue.ROUNDING_KEY), PrefLis { pref, value ->
                val stringValue = value.toString()
                NumberValue.setRoundingMode(stringValue)
                updateSummaryForList(pref, stringValue)
                true
            })
            bindPreferenceSummaryToValue(findPreference(ThemeManager.THEME_PREF_KEY), PrefLis { pref, value ->
                val stringValue = value.toString()
                updateSummaryForList(pref, stringValue)
                ThemeManager.updateTheme(stringValue)
                activity.recreate()
                true
            })
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            val id = item.itemId
            if (id == android.R.id.home) {
                startActivity(Intent(activity, SettingsActivity::class.java))
                return true
            }
            return super.onOptionsItemSelected(item)
        }
    }

    companion object {

        /**
         * A preference value change listener that updates the preference's summary
         * to reflect its new value.
         */
        private val sBindPreferenceSummaryToValueListener = PrefLis { preference, value ->
            val stringValue = value.toString()
            if (preference is ListPreference) {
               updateSummaryForList(preference, stringValue)
            } else {
                updatePrecision(preference, stringValue)
            }
            true
        }

        private fun updateSummaryForList(pref: Preference, value: String) {
            if (pref is ListPreference) {
                val index = pref.findIndexOfValue(value)
                pref.summary = if (index >= 0) pref.entries[index] else null
            }
        }

        private fun updatePrecision(pref: Preference, value: String) {
            if (value.isBlank()) {
                pref.summary = "Unlimited"
            } else {
                pref.summary = value
            }
        }

        /**
         * Helper method to determine if the device has an extra-large screen. For
         * example, 10" tablets are extra-large.
         */
        private fun isXLargeTablet(context: Context): Boolean {
            return context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_XLARGE
        }

        /**
         * Binds a preference's summary to its value. More specifically, when the
         * preference's value is changed, its summary (line of text below the
         * preference title) is updated to reflect the value. The summary is also
         * immediately updated upon calling this method. The exact display format is
         * dependent on the type of preference.

         * @see .sBindPreferenceSummaryToValueListener
         */
        private fun bindPreferenceSummaryToValue(preference: Preference, listener: PrefLis = sBindPreferenceSummaryToValueListener) {
            // Set the listener to watch for value changes.
            preference.onPreferenceChangeListener = listener

            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.context)
                            .getString(preference.key, ""))
        }
    }

}
typealias PrefLis = Preference.OnPreferenceChangeListener
