package com.example.cafecornerapp.Adapter

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cafecornerapp.Activity.MainActivity
import com.example.cafecornerapp.Domain.CartCustomModel
import com.example.cafecornerapp.Domain.CartModel
import com.example.cafecornerapp.Domain.ProductModel
import com.example.cafecornerapp.ViewModel.CartViewModel
import com.example.cafecornerapp.databinding.ViewHolderCardcartBinding
import com.example.cafecornerapp.databinding.ViewHolderCardproductListBinding


class CardProductListCartAdapter(
    private val onKurangClick: (CartCustomModel) -> Unit,
    private val onPlusClick: (CartCustomModel) -> Unit,
    val items: MutableList<CartCustomModel>
):
    RecyclerView.Adapter<CardProductListCartAdapter.Viewholder>() {
    private val viewModel = CartViewModel()
    lateinit var context: Context
    class Viewholder(val binding: ViewHolderCardcartBinding):
        RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardProductListCartAdapter.Viewholder {
        context= parent.context
        val binding = ViewHolderCardcartBinding.
        inflate(LayoutInflater.from(context),parent,false)
        return Viewholder(binding)
    }

    fun submitList(newItems: List<CartCustomModel>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: CardProductListCartAdapter.Viewholder, position: Int) {
        var qtyTotal : Long = items[position].jumlah.toLong()
        holder.binding.tvMinus.setOnClickListener {
            if (qtyTotal <= 1) {
                qtyTotal = 1
            }else {
                qtyTotal = qtyTotal - 1
                onKurangClick(items[position])
            }
            holder.binding.tvQty.text= qtyTotal.toString()
        }

        holder.binding.tvPlus.setOnClickListener {
            qtyTotal = qtyTotal + 1
            holder.binding.tvQty.text= qtyTotal.toString()
            onPlusClick(items[position])
        }

        holder.binding.tvNamaMenu.text= items[position].nama
        holder.binding.tvHarga.text="$"+items[position].harga.toString()
        holder.binding.tvKategori.text= items[position].kategori.toString()
        holder.binding.tvQty.text= items[position].jumlah.toString()



        Glide.with(context).load(items[position].imgUrl).into(holder.binding.imgMenu)

        holder.binding.btnDelete.setOnClickListener {
            viewModel.deleteCart(items[position].cartId)
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(context, MainActivity::class.java)
                ContextCompat.startActivity(context, intent, null)
            }, 500)
        }
    }

    override fun getItemCount(): Int =items.size
}