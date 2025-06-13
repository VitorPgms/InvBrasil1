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
import com.vitordepaula.invbrasil.databinding.ActivityUpdateProductBinding
import com.vitordepaula.invbrasil.model.Product
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UpdateProduct : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateProductBinding
    private lateinit var productDao: ProductDao


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateProductBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val nameRecover = intent.extras?.getString("nome")
        val quantityRecover = intent.extras?.getString("quantidade")
        val uid = intent.extras!!.getInt("uid")

        binding.editNome.setText(nameRecover)
        binding.editQuantity.setText(quantityRecover)

        binding.btnUpgrade.setOnClickListener {

            CoroutineScope(Dispatchers.IO).launch {

                val name = binding.editNome.text.toString()
                val quantity = binding.editQuantity.text.toString()

                val mensagem: Boolean

                if(name.isEmpty() || quantity.isEmpty()){
                    mensagem = false
                } else {
                    mensagem = true
                    updateProduct(uid, name, quantity)
                }

                withContext(Dispatchers.Main){
                    if(mensagem){
                        Toast.makeText(applicationContext, "Sucesso ao Atualizar Produto", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(applicationContext, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }


    private fun updateProduct(uid: Int, name: String, quantity: String) {
        productDao = AppDatabase.getIntance(this).productDao()
        val updatedProduct = Product(uid, name, quantity)
        productDao.update(updatedProduct)
    }


}