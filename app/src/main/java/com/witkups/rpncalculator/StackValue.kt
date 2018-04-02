package com.witkups.rpncalculator

import java.lang.Math.*

sealed class StackValue {
    abstract fun onVisit(stack: RPNStack): RPNStack
}

data class NumberValue(val value: String = "") : StackValue() {
    override fun onVisit(stack: RPNStack) =
            if (stack.items.isNotEmpty()) {
                val newValue = NumberValue(stack.items.last().value + value)
                RPNStack(stack.items.dropLast(1) + newValue)
            } else {
                RPNStack(listOf(this))
            }

    operator fun plus(other: NumberValue) = merge(other) { a, b -> a + b }

    operator fun unaryMinus() = NumberValue((-getVal()).toString())

    operator fun times(other: NumberValue) = merge(other) { a, b -> a * b }

    operator fun div(other: NumberValue) = merge(other) { a, b -> a / b }

    private fun merge(other: NumberValue, oper: (Double, Double) -> Double) =
            NumberValue(oper(getVal(), other.getVal()).toString())

    fun sqrt() = NumberValue(sqrt(getVal()).toString())

    fun exp(other: NumberValue) = merge(other, ::pow)

    private fun getVal() = if (value.isBlank()) 0.0 else value.toDouble()
}

abstract class UnaryOperator : StackValue() {
    override fun onVisit(stack: RPNStack): RPNStack {
        if (stack.items.isNotEmpty()) {
            val newTopValue = change(stack.items.last())
            val newItems = stack.items.dropLast(1) + newTopValue
            return RPNStack(newItems)
        }
        return stack
    }

    abstract fun change(value: NumberValue): NumberValue
}

abstract class BiOperator : StackValue() {
    override fun onVisit(stack: RPNStack): RPNStack {
        if (stack.items.size >= 2) {
            val newTopValue = stack.items.takeLast(2).reduce(this::merge)
            val newItems = stack.items.dropLast(2) + newTopValue
            return RPNStack(newItems)
        }
        return stack
    }

    abstract fun merge(val1: NumberValue, val2: NumberValue): NumberValue
}

object SwapOperator : StackValue() {
    override fun onVisit(stack: RPNStack): RPNStack {
        if (stack.items.size >= 2) {
            val newItems = stack.items.dropLast(2) + stack.items.takeLast(2).reversed()
            return RPNStack(newItems)
        }
        return stack
    }
}

object PlusOperator : BiOperator() {
    override fun merge(val1: NumberValue, val2: NumberValue) = val1 + val2
}

object MulOperator : BiOperator() {
    override fun merge(val1: NumberValue, val2: NumberValue) = val1 * val2
}

object DivOperator : BiOperator() {
    override fun merge(val1: NumberValue, val2: NumberValue) = val1 / val2
}

object MinusOperator : UnaryOperator() {
    override fun change(value: NumberValue) = -value
}

object SqrtOperator : UnaryOperator() {
    override fun change(value: NumberValue) = value.sqrt()
}

object ExpOperator : BiOperator() {
    override fun merge(val1: NumberValue, val2: NumberValue) = val1.exp(val2)
}

object EnterOperator : StackValue() {
    override fun onVisit(stack: RPNStack) = RPNStack(stack.items + NumberValue())
}

object StackCleaner: StackValue() {
    override fun onVisit(stack: RPNStack) = RPNStack()
}

object DropOperator: StackValue() {
    override fun onVisit(stack: RPNStack) = RPNStack(stack.items.dropLast(1))
}