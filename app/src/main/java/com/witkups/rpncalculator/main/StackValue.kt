package com.witkups.rpncalculator.main

import java.lang.Math.*
import java.math.BigDecimal
import java.math.RoundingMode

sealed class StackValue {
    abstract fun onVisit(stack: RPNStack): RPNStack
}

data class NumberValue(val value: String = "") : StackValue() {
    companion object {
        const val PRECISION_KEY = "value_precision"
        const val ROUNDING_KEY = "rounding_mode"
        const val DEFAULT_ROUNDING_MODE = "4"
        const val PRECISION_NOT_SET_LABEL = "Unlimited"
        private var precision: Int? = null
        private var roundingMode: RoundingMode? = null
        private fun BigDecimal.align() =
                if (precision != null) setScale(precision!!, roundingMode!!).stripTrailingZeros()
                else this

        fun setRoundingMode(index: String) {
            roundingMode = RoundingMode.valueOf(index.toInt())
        }

        fun setPrecision(value: String) {
            precision = if (value.isBlank()) null else value.toInt()
        }

    }
    constructor(value: BigDecimal) : this(value = value.align().stripTrailingZeros().toPlainString())

    override fun onVisit(stack: RPNStack) =
            if (stack.items.isNotEmpty()) {
                val lastValue = stack.items.last().value
                if (value == "." && lastValue.contains(".")) {
                    stack
                } else {
                    val newValue = NumberValue(lastValue + value)
                    RPNStack(stack.items.dropLast(1) + newValue)
                }
            } else {
                RPNStack(listOf(this))
            }

    operator fun plus(other: NumberValue) = merge(other) { a, b -> a + b }

    operator fun unaryMinus() = NumberValue(
            when {
                isEmpty() -> "-"
                value.startsWith("-") -> value.drop(1)
                else -> "-$value"
            }
    )

    operator fun times(other: NumberValue) = merge(other) { a, b -> a * b }

    operator fun div(other: NumberValue) = merge(other) { a, b -> a / b }

    private fun merge(other: NumberValue, oper: (BigDecimal, BigDecimal) -> BigDecimal) =
            NumberValue(oper(getVal(), other.getVal()))

    fun sqrt() = NumberValue(BigDecimal(sqrt(getVal().toDouble())))

    fun exp(other: NumberValue) = merge(other) { a, b -> BigDecimal(pow(a.toDouble(), b.toDouble()))}

    private fun getVal() = if (isEmpty()) BigDecimal.ZERO else BigDecimal(value)

    fun isEmpty() = value.isBlank()
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
    override fun onVisit(stack: RPNStack) =
            if (stack.hasEmptyTopValue()) stack
            else RPNStack(stack.items + NumberValue())
}

object StackCleaner : StackValue() {
    override fun onVisit(stack: RPNStack) = RPNStack()
}

object DropOperator : StackValue() {
    override fun onVisit(stack: RPNStack) = RPNStack(stack.items.dropLast(1))
}

object BackSpaceOperator: UnaryOperator() {
    override fun change(value: NumberValue) =
            if (value.isEmpty()) value
            else NumberValue(value.value.dropLast(1))

}