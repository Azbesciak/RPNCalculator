package com.witkups.rpncalculator

class RPNStack(items: List<NumberValue> = listOf()) {
    val items: List<NumberValue> = if (items.isEmpty()) listOf(NumberValue()) else items

    fun visit(value: StackValue): RPNStack = value.onVisit(this)

    fun hasEmptyTopValue() = items.last().isEmpty()

    override fun toString() = "RPNStack(items=$items)"
}