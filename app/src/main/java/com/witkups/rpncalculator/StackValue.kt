package com.witkups.rpncalculator

sealed class StackValue {
    abstract fun onVisit(stack: RPNStack): RPNStack
}

class NumberValue(val value: String) : StackValue() {
    override fun onVisit(stack: RPNStack) =
            RPNStack(NumberValue(value + stack.topValue.value), stack.items)
}

class OperationValue(private val oper: (RPNStack) -> RPNStack) : StackValue() {
    override fun onVisit(stack: RPNStack) = oper(stack)
}

