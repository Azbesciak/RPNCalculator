package com.witkups.rpncalculator

import android.widget.Button

object StackOperator {

    fun attachOperator(button: Button, value: String) {
        val operator = NumberValue(value)
        attachOperator(button, operator)
    }

    fun attachOperator(button: Button, operator: StackValue) {
        button.setOnClickListener { StackProvider.update(operator) }
    }
    fun attachOperator(button: Button, op: () -> Unit) {
        button.setOnClickListener{op()}
    }
}