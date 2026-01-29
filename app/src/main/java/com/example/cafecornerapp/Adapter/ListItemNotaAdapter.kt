package com.example.cafecornerapp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cafecornerapp.Domain.HistoryProductModel
import com.example.cafecornerapp.databinding.ViewHolderTableItemListBinding


class ListItemNotaAdapter(val items: MutableList<HistoryProductModel>):
    RecyclerView.Adapter<ListItemNotaAdapter.Viewholder>() {

    lateinit var context: Context
    class Viewholder(val binding: ViewHolderTableItemListBinding):
        RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItemNotaAdapter.Viewholder {
        context= parent.context
        val binding = ViewHolderTableItemListBinding.
        inflate(LayoutInflater.from(context),parent,false)
        return Viewholder(binding)
    }

    override fun onBindViewHolder(holder: ListItemNotaAdapter.Viewholder, position: Int) {
        holder.binding.nameProductNota.text = items[position]
            .nama
        holder.binding.qtyProductNota.text = items[position]
            .jumlah
            .toString()
        holder.binding.hargaProductNota.text = items[position]
            .harga
            .toString()
        holder.binding.totalHargaNota.text = (items[position]
            .harga * items[position].jumlah)
            .toString()
    }
    override fun getItemCount(): Int =items.size
}