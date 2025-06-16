package com.vitordepaula.invbrasil.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.vitordepaula.invbrasil.AppDatabase
import com.vitordepaula.invbrasil.R
import com.vitordepaula.invbrasil.dao.ProductDao
import com.vitordepaula.invbrasil.databinding.ActivityRegisterProductBinding
import com.vitordepaula.invbrasil.model.Product
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterProduct : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterProductBinding
    private var productDao: ProductDao? = null
    private val listProduct: MutableList<Product> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterProductBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnRegister.setOnClickListener {

            CoroutineScope(Dispatchers.IO).launch {

                val name = binding.editNome.text.toString()
                val quantity = binding.editQuantity.text.toString()
                val quantityMin = binding.editQuantityMin.text.toString()
                val color = binding.editColor.text.toString()
                val price = binding.editPrice.text.toString()
                val mensagem: Boolean

                if(name.isEmpty() || quantity.isEmpty() || quantityMin.isEmpty() || price.isEmpty()){
                    mensagem = false
                } else {
                    mensagem = true
                    register(name, quantity, quantityMin, color, price)
                }

                withContext(Dispatchers.Main){
                    if(mensagem){
                        Toast.makeText(applicationContext, "Sucesso ao Registrar Produto", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(applicationContext, "Preencha Todos os Campos", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

    }

    private fun register(name: String, quantity: String, quantityMin: String, color: String, price: String){
        val product = Product(nome = name, quantidade = quantity, quantidadeMinima = quantityMin, cor = color, preco = price)
        listProduct.add(product)
        productDao = AppDatabase.getIntance(this).productDao()
        productDao!!.inserir(listProduct)

    }

}