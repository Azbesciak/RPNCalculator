package com.witkups.rpncalculator

class RPNStack(val topValue: NumberValue, val items: List<StackValue>) {
    fun visit(value: StackValue): RPNStack = value.onVisit(this)
}