package io.github.wawakaka.basicframeworkproject.presentation.content.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layout_currency_item.view.*

class CurrencyListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bindViews(data: Pair<String, Double>, clickListener: ((kurs: String) -> Unit)?) {
        setCurrencyCode(data.first)
        setCurrencyValue(data.second)
        setClickListener(data.second, clickListener)
    }

    private fun setCurrencyCode(code: String) {
        itemView.text_currency_code.text = code
    }

    private fun setCurrencyValue(value: Double) {
        itemView.text_currency_value.text = value.toString()
    }

    private fun setClickListener(value: Double, clickListener: ((kurs: String) -> Unit)?) {
        itemView.container.setOnClickListener { clickListener?.invoke(value.toString()) }
    }

}