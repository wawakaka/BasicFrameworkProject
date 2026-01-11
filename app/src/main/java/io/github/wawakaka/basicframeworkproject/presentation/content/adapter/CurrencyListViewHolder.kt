package io.github.wawakaka.basicframeworkproject.presentation.content.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import io.github.wawakaka.basicframeworkproject.databinding.LayoutCurrencyItemBinding

class CurrencyListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val binding = LayoutCurrencyItemBinding.bind(itemView)

    fun bindViews(data: Pair<String, Double>, clickListener: ((kurs: String) -> Unit)?) {
        setCurrencyCode(data.first)
        setCurrencyValue(data.second)
        setClickListener(data.second, clickListener)
    }

    private fun setCurrencyCode(code: String) {
        binding.textCurrencyCode.text = code
    }

    private fun setCurrencyValue(value: Double) {
        binding.textCurrencyValue.text = value.toString()
    }

    private fun setClickListener(value: Double, clickListener: ((kurs: String) -> Unit)?) {
        binding.container.setOnClickListener { clickListener?.invoke(value.toString()) }
    }

}