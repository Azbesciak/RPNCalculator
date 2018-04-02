package com.witkups.rpncalculator

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_calculator.*
import kotlinx.android.synthetic.main.content_calculator.*


class CalculatorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculator)
        setSupportActionBar(toolbar)
        attachButtons()
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
            R.id.action_settings -> true
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
                buttonAC to StackCleaner, buttonDrop to DropOperator)
                .forEach { StackOperator.attachOperator(it.first, it.second) }
        StackOperator.attachOperator(buttonUndo, StackProvider::undo)
        stackView.apply {
            layoutManager = LinearLayoutManager(this@CalculatorActivity)
            adapter = StackViewerAdapter(this@CalculatorActivity).apply {
                StackProvider.registerListener(this::update)
            }
        }
    }
}
