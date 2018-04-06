package com.witkups.rpncalculator.main

class RPNStack(items: List<NumberValue> = listOf(), private val hasError: Boolean = false) {
    val items: List<NumberValue> = if (items.isEmpty()) listOf(NumberValue()) else items

    fun visit(value: StackValue): RPNStack = if (hasError) this else {
        try {
            value.onVisit(this)
        } catch (t: Throwable) {
            RPNStack(items + NumberValue(t.localizedMessage), true)
        }
    }

    fun hasError() = hasError

    fun hasEmptyTopValue() = items.last().isEmpty()

    override fun toString() = "RPNStack(items=$items)"
}