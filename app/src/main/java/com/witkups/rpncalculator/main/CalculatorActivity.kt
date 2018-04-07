package com.witkups.rpncalculator.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceActivity
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import com.witkups.rpncalculator.*
import com.witkups.rpncalculator.settings.AppCompatPreferenceActivity
import com.witkups.rpncalculator.settings.SettingsActivity
import kotlinx.android.synthetic.main.activity_calculator.*
import kotlinx.android.synthetic.main.content_calculator.*


class CalculatorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculator)
        setSupportActionBar(toolbar)
        attachButtons()
        val roundingMode = PreferenceManager.getDefaultSharedPreferences(applicationContext)
                .getString(NumberValue.ROUNDING_KEY, NumberValue.DEFAULT_ROUNDING_MODE.toString())
        NumberValue.setRoundingMode(roundingMode.toInt())
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_calculator, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, SettingsActivity.GeneralPreferenceFragment::class.java.name)
                intent.putExtra(PreferenceActivity.EXTRA_NO_HEADERS, true )
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun attachButtons() {
        listOf(button0, button1, button2, button3, button4,
                button5, button6, button7, button8, button9)
                .forEachIndexed { i, btn ->
                    applyTextIncrease(btn)
                    StackOperator.attachOperator(btn, i.toString())
                }
        StackOperator.attachOperator(buttonDecimal, ".")
        applyTextIncrease(buttonDecimal)
        listOf(buttonMul to MulOperator, buttonSub to MinusOperator,
                buttonDiv to DivOperator, buttonAdd to PlusOperator,
                buttonEnter to EnterOperator, buttonSwap to SwapOperator,
                buttonExp to ExpOperator, buttonSqrt to SqrtOperator,
                buttonAC to StackCleaner, buttonDrop to DropOperator,
                buttonBackspace to BackSpaceOperator)
                .forEach {
                    applyTextIncrease(it.first)
                    StackOperator.attachOperator(it.first, it.second)
                }
        StackOperator.attachOperator(buttonUndo, StackProvider::undo)
        applyTextIncrease(buttonUndo)
        stackView.apply {
            layoutManager = LinearLayoutManager(this@CalculatorActivity)
            adapter = StackViewerAdapter(this@CalculatorActivity).apply {
                StackProvider.registerListener(this::update)
            }
        }
    }

    private fun applyTextIncrease(btn: Button) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            btn.setTextAppearance(R.style.ButtonFontStyle)
        }
    }
}
