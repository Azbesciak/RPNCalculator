package com.witkups.rpncalculator

class RPNStack(items: List<NumberValue> = listOf()) {
    val items: List<NumberValue>
    init {
        if (items.isEmpty()) {
            this.items = listOf(NumberValue())
        } else {
            this.items = items
        }
    }
    fun visit(value: StackValue): RPNStack = value.onVisit(this)
    override fun toString(): String {
        return "RPNStack(items=$items)"
    }
}