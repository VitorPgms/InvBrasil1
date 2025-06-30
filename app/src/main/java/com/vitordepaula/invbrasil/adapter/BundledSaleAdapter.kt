package com.vitordepaula.invbrasil.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vitordepaula.invbrasil.model.BundledSale
import com.vitordepaula.invbrasil.model.Sale

class BundledSaleAdapter (
    private val context: Context,
    private val list: List<BundledSale>
    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_HEADER = 0
        const val VIEW_TYPE_ITEM = 1
    }

    private val formattedItems = mutableListOf<Any>()

    init {
        for (group in list) {
            formattedItems.add(group.mouthYear)
            formattedItems.addAll(group.sale)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (formattedItems[position] is String) VIEW_TYPE_HEADER else VIEW_TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_HEADER) {
            val view = LayoutInflater.from(context)
                .inflate(android.R.layout.simple_list_item_1, parent, false)
            HeaderViewHolder(view)
        } else {
            val view = LayoutInflater.from(context)
                .inflate(android.R.layout.simple_list_item_2, parent, false)
            SaleViewHolder(view)
        }
    }

    override fun getItemCount() = formattedItems.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HeaderViewHolder) {
            holder.bind(formattedItems[position] as String)
        } else if (holder is SaleViewHolder) {
            holder.bind(formattedItems[position] as Sale)
        }
    }


    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(header: String) {
            (itemView as TextView).text = "$header"
        }
    }

    class SaleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(sale: Sale) {
            (itemView.findViewById(android.R.id.text1) as TextView).text = sale.nameClient
            (itemView.findViewById(android.R.id.text2) as TextView).text =
                "${sale.dataSale}x ${sale.nameProduct} | ${sale.quantitySale}"
        }
    }
}