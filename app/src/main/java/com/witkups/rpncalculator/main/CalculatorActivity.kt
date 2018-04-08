package com.witkups.rpncalculator.main

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceActivity
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.witkups.rpncalculator.R
import com.witkups.rpncalculator.settings.SettingsActivity
import com.witkups.rpncalculator.settings.updateTheme
import kotlinx.android.synthetic.main.activity_calculator.*
import kotlinx.android.synthetic.main.content_calculator.*


class CalculatorActivity : AppCompatActivity() {

    companion object {
        const val SETTINGS_RESULT_CODE = 10
    }

    private lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        setPreferences()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculator)
        setSupportActionBar(toolbar)
        attachButtons()
    }

    private fun setPreferences() {
        preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val roundingMode = preferences
                .getString(NumberValue.ROUNDING_KEY, NumberValue.DEFAULT_ROUNDING_MODE)
        NumberValue.setRoundingMode(roundingMode)
        val precision = preferences.getString(NumberValue.PRECISION_KEY, "")
        NumberValue.setPrecision(precision)
        updateTheme(false, preferences)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_calculator, menu)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == SETTINGS_RESULT_CODE) {
            recreate()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, SettingsActivity.GeneralPreferenceFragment::class.java.name)
                intent.putExtra(PreferenceActivity.EXTRA_NO_HEADERS, true)
                startActivityForResult(intent, SETTINGS_RESULT_CODE)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun attachButtons() {
        listOf(button0, button1, button2, button3, button4,
                button5, button6, button7, button8, button9)
                .forEachIndexed { i, btn -> StackOperator.attachOperator(btn, i.toString()) }
        StackOperator.attachOperator(buttonDecimal, ".")
        listOf(buttonMul to MulOperator, buttonSub to MinusOperator,
                buttonDiv to DivOperator, buttonAdd to PlusOperator,
                buttonEnter to EnterOperator, buttonSwap to SwapOperator,
                buttonExp to ExpOperator, buttonSqrt to SqrtOperator,
                buttonAC to StackCleaner, buttonDrop to DropOperator,
                buttonBackspace to BackSpaceOperator)
                .forEach {
                    StackOperator.attachOperator(it.first, it.second)
                }
        StackOperator.attachOperator(buttonUndo, StackProvider::undo)
        stackView.apply {
            layoutManager = LinearLayoutManager(this@CalculatorActivity)
            adapter = StackViewerAdapter(this@CalculatorActivity).apply {
                StackProvider.registerListener(this::update)
            }
        }
    }
}
