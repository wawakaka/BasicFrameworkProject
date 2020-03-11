package io.github.wawakaka.basicframeworkproject.presentation.content.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.wawakaka.basicframeworkproject.R

class CurrencyListAdapter(private val data: List<Pair<String, Double>>) :
    RecyclerView.Adapter<CurrencyListViewHolder>() {

    var clickListener: ((kurs: String) -> Unit)? = null

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyListViewHolder {
        return CurrencyListViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_currency_item,
                null
            )
        )
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: CurrencyListViewHolder, position: Int) {
        holder.bindViews(data[position], clickListener)
    }
}