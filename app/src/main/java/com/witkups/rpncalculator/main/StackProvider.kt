package com.witkups.rpncalculator.main

object StackProvider {
    private val stackHistory = mutableListOf(RPNStack())
    private const val maxHistoryLength = 50
    private var listener: ((RPNStack) -> Unit)? = null

    fun registerListener(l: (RPNStack) -> Unit) {
        listener = l
        l(get())
    }
    fun update(value: StackValue) {
        val newOne = stackHistory.last().visit(value)
        if (newOne != stackHistory.last()) {
            if (stackHistory.size >= maxHistoryLength)
                stackHistory.removeAt(0)
            stackHistory.add(newOne)
            listener?.invoke(newOne)
        }
    }

    fun undo() {
        if (stackHistory.size > 1) {
            stackHistory.removeAt(stackHistory.size - 1)
            listener?.invoke(get())
        }
    }

    private fun get() = stackHistory.last()
}