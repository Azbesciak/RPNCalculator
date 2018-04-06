package com.witkups.rpncalculator.main

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.witkups.rpncalculator.R


class StackViewerAdapter(context: Context) : RecyclerView.Adapter<StackViewHolder>() {
    private val inflater = LayoutInflater.from(context)
    private var items = listOf<NumberValue>()
    private var hasError = false
    fun update(stack: RPNStack) {
        items = stack.items.reversed()
        hasError = stack.hasError()
        notifyDataSetChanged()
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: StackViewHolder?, position: Int) {
        val newValue = items[position]

        holder?.textView?.text = Html.fromHtml(
                if (position == 0 && hasError) "<span style='color:#E91E63'>${newValue.value}</span>"
                else "<span style='color:#9E9E9E'>${position + 1}.</span>  ${newValue.value}"
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): StackViewHolder {
        val view = inflater.inflate(R.layout.stack_view_row, parent, false)
        return StackViewHolder(view)
    }

    override fun getItemCount() = items.size

}

class StackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val textView: TextView = itemView.findViewById(R.id.stackValue)
}