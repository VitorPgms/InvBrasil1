package com.vitordepaula.invbrasil.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.vitordepaula.invbrasil.AppDatabase
import com.vitordepaula.invbrasil.databinding.ProductItemBinding
import com.vitordepaula.invbrasil.model.Product
import com.vitordepaula.invbrasil.view.UpdateProduct
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProductAdapter (
    private val context: Context,
    private val listProduct: MutableList<Product>): RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val itemLista = ProductItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ProductViewHolder(itemLista)
    }

    override fun getItemCount(): Int = listProduct.size

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = listProduct[position]
        holder.txtNome.text = product.nome
        holder.txtQuantity.text = "Qtd: ${product.quantidade}"

        val quantityCurrent = product.quantidade.toIntOrNull() ?: 0
        val quantityMin = product.quantidadeMinima.toIntOrNull() ?: 0

        if (quantityCurrent < quantityMin){
            holder.txtQuantity.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_light))
            holder.txtQuantity.text = "Baixo: ${product.quantidade}"
        } else {
            holder.txtQuantity.setTextColor(ContextCompat.getColor(context, android.R.color.black))
        }

        holder.btnAtualizar.setOnClickListener {
            val intent = Intent(context, UpdateProduct::class.java )
            intent.putExtra("uid", listProduct[position].uid)
            intent.putExtra("nome", listProduct[position].nome)
            intent.putExtra("quantidade", listProduct[position].quantidade)
            intent.putExtra("quantidadeMinima", listProduct[position].quantidadeMinima)
            context.startActivity(intent)

        }

        holder.btnDeletar.setOnClickListener {

            CoroutineScope(Dispatchers.IO).launch {
                val product = listProduct[position]
                val productDao = AppDatabase.getIntance(context).productDao()
                productDao.delete(product.uid)
                listProduct.remove(product)

                withContext(Dispatchers.Main){
                    notifyDataSetChanged()
                }

            }
        }
    }



    inner class ProductViewHolder(binding: ProductItemBinding): RecyclerView.ViewHolder(binding.root) {
        val txtNome = binding.txtNome
        val txtQuantity = binding.txtQuantity

        val btnAtualizar = binding.btnAtualizar
        val btnDeletar = binding.btnDeletar
    }


}