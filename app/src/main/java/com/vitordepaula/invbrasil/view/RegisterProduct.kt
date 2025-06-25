package com.vitordepaula.invbrasil.view

import android.content.Intent
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
import com.vitordepaula.invbrasil.databinding.ActivityRegisterProductBinding
import com.vitordepaula.invbrasil.model.Category
import com.vitordepaula.invbrasil.model.Product
import com.vitordepaula.invbrasil.repository.ProductRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterProduct : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterProductBinding
    private lateinit var categoryDao: CategoryDao
    private lateinit var repository: ProductRepository
    private var productDao: ProductDao? = null
    private var category: List<Category> = listOf()
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

        val dao = AppDatabase.getIntance(this).productDao()
        repository = ProductRepository(dao)
        categoryDao = AppDatabase.getIntance(this).categoryDao()

        binding.btnRegister.setOnClickListener {

            CoroutineScope(Dispatchers.IO).launch {

                val name = binding.editNome.text.toString()
                val quantity = binding.editQuantity.text.toString()
                val quantityMin = binding.editQuantityMin.text.toString()
                val color = binding.editColor.text.toString()
                val price = binding.editPrice.text.toString()
                val categorySelect = binding.spinnerCategory.selectedItemPosition
                val categoryId = category.getOrNull(categorySelect)?.id

                val mensagem: Boolean = name.isNotEmpty() && quantity.isNotEmpty() &&
                        quantityMin.isNotEmpty() && price.isNotEmpty()

                if(mensagem){
                    val product = Product(nome = name, quantidade = quantity, quantidadeMinima = quantityMin, cor = color, preco = price, categoriaId = categoryId)
                    listProduct.clear()
                    listProduct.add(product)
                    repository.insert(product)

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

        binding.btNewCategory.setOnClickListener {
            startActivity(Intent(this, NewCategory::class.java))
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

    }

    override fun onResume() {
        super.onResume()
        loadCategory()
    }

    private fun loadCategory() {
        CoroutineScope(Dispatchers.IO).launch {
            category = categoryDao.getAll()
            val nomesCategorias = category.map { it.nome }

            withContext(Dispatchers.Main) {
                val adapter = ArrayAdapter(this@RegisterProduct, android.R.layout.simple_spinner_item, nomesCategorias)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerCategory.adapter = adapter
            }
        }
    }

}