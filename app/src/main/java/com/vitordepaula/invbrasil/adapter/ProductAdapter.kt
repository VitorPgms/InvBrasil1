package com.vitordepaula.invbrasil.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.vitordepaula.invbrasil.AppDatabase
import com.vitordepaula.invbrasil.R
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
        holder.txtNome.text = "Nome: ${product.nome}"
        holder.txtQuantity.text = "Qtd: ${product.quantidade}"
        holder.txtPrice.text = "R$${product.preco}"

        val quantityCurrent = product.quantidade.toIntOrNull() ?: 0
        val quantityMin = product.quantidadeMinima.toIntOrNull() ?: 0

        when {
            quantityCurrent == 0 -> {
                holder.txtQuantity.text = "Esgotado"
                holder.txtQuantity.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_light))
                holder.container.background = ContextCompat.getDrawable(context, R.drawable.bg_card_red)
            }
            quantityCurrent < quantityMin -> {
                holder.txtQuantity.text = "Baixo: ${product.quantidade}"
                holder.txtQuantity.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark))
                holder.container.background = ContextCompat.getDrawable(context, R.drawable.bg_card_red)
            }
            else -> {
                holder.txtQuantity.text = "Qtd: ${product.quantidade}"
                holder.txtQuantity.setTextColor(ContextCompat.getColor(context, android.R.color.black))
            }
        }

        holder.itemView.setOnClickListener {
            if (quantityCurrent == 0) {
                AlertDialog.Builder(context)
                    .setTitle("Estoque zerado")
                    .setMessage("O produto ${product.nome} está com estoque esgotado!")
                    .setPositiveButton("OK", null)
                    .show()
            }
        }

        holder.btnAtualizar.setOnClickListener {
            val intent = Intent(context, UpdateProduct::class.java ).apply {
                putExtra("uid", listProduct[position].uid)
                putExtra("nome", listProduct[position].nome)
                putExtra("quantidade", listProduct[position].quantidade)
                putExtra("quantidadeMinima", listProduct[position].quantidadeMinima)
                putExtra("cor", listProduct[position].cor)
                putExtra("preco", listProduct[position].preco)
                putExtra("categoriaId", listProduct[position].categoriaId)
            }
            context.startActivity(intent)

        }

        holder.btnDeletar.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Confirmação")
                .setMessage("Tem certeza que deseja excluir este produto?")
                .setPositiveButton("Sim") {_,_ ->
                CoroutineScope(Dispatchers.IO).launch {
                    val productToDelete = listProduct[holder.adapterPosition]
                    val productDao = AppDatabase.getIntance(context).productDao()
                    productDao.delete(productToDelete.uid)

                    withContext(Dispatchers.Main) {
                        val index = holder.adapterPosition
                        if(index != RecyclerView.NO_POSITION){
                            listProduct.removeAt(index)
                            notifyItemRemoved(index)
                        }
                    }
                }
            }
                .setNegativeButton("Cancelar", null)
                .show()
        }

    }



    inner class ProductViewHolder(binding: ProductItemBinding): RecyclerView.ViewHolder(binding.root) {
        val txtNome = binding.txtNome
        val txtQuantity = binding.txtQuantity
        val txtPrice = binding.txtPrice
        val container = binding.containerMain

        val btnAtualizar = binding.btnAtualizar
        val btnDeletar = binding.btnDeletar
    }


}