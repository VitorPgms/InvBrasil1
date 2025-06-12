package com.vitordepaula.invbrasil.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
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
        holder.txtNome.text = listProduct[position].nome
        holder.txtQuantity.text = listProduct[position].quantidade

        holder.btnAtualizar.setOnClickListener {
            val intent = Intent(context, UpdateProduct::class.java )
            intent.putExtra("nome", listProduct[position].nome)
            intent.putExtra("quantidade", listProduct[position].quantidade)
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