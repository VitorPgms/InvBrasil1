package com.vitordepaula.invbrasil.view

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.vitordepaula.invbrasil.AppDatabase
import com.vitordepaula.invbrasil.R
import com.vitordepaula.invbrasil.dao.CategoryDao
import com.vitordepaula.invbrasil.dao.ProductDao
import com.vitordepaula.invbrasil.databinding.ActivityUpdateProductBinding
import com.vitordepaula.invbrasil.model.Category
import com.vitordepaula.invbrasil.model.Product
import com.vitordepaula.invbrasil.repository.ProductRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UpdateProduct : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateProductBinding
    private lateinit var categoryDao: CategoryDao
    private lateinit var repository: ProductRepository
    private var category: List<Category> = listOf()
    private var categoryIdCurrent: Int? = null


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

        val dao = AppDatabase.getIntance(this).productDao()
        repository = ProductRepository(dao)
        categoryDao = AppDatabase.getIntance(this).categoryDao()

        val uid = intent.extras!!.getInt("uid")
        val nameRecover = intent.extras?.getString("nome")
        val quantityRecover = intent.extras?.getString("quantidade")
        val quantityMinRecover = intent.extras?.getString("quantidadeMinima")
        val colorRecover = intent.extras?.getString("cor")
        val priceRecover = intent.extras?.getString("preco")
        categoryIdCurrent = intent.extras?.getInt("categoriaId")

        binding.editNome.setText(nameRecover)
        binding.editQuantity.setText(quantityRecover)
        binding.editQuantityMin.setText(quantityMinRecover)
        binding.editColor.setText(colorRecover)
        binding.editPrice.setText(priceRecover)

        loadCategory()

        binding.btnUpgrade.setOnClickListener {

            CoroutineScope(Dispatchers.IO).launch {

                val name = binding.editNome.text.toString()
                val quantity = binding.editQuantity.text.toString()
                val quantityMin = binding.editQuantityMin.text.toString()
                val color = binding.editColor.text.toString()
                val price = binding.editPrice.text.toString()
                val categorySelect = binding.spinnerCategory.selectedItemPosition
                val categoryId = category.getOrNull(categorySelect)?.id

                val mensagem = name.isNotEmpty() && quantity.isNotEmpty() &&
                        quantityMin.isNotEmpty() && price.isNotEmpty()

                if(mensagem){
                    val updatedProduct = Product(uid, name, quantity, quantityMin, color, price, categoryId)
                    repository.update(updatedProduct)
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


    private fun loadCategory() {
        CoroutineScope(Dispatchers.IO).launch {
            category = categoryDao.getAll()
            val name = category.map { it.nome }

            withContext(Dispatchers.Main) {
                val adapter = ArrayAdapter(this@UpdateProduct, android.R.layout.simple_spinner_item, name)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerCategory.adapter = adapter

                categoryIdCurrent?.let { id ->
                    val index = category.indexOfFirst { it.id == id }
                    if (index >= 0) {
                        binding.spinnerCategory.setSelection(index)
                    }
                }
            }
        }
    }


}