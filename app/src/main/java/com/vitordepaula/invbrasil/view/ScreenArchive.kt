package com.vitordepaula.invbrasil.view

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.itextpdf.io.exceptions.IOException
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.vitordepaula.invbrasil.AppDatabase
import com.vitordepaula.invbrasil.databinding.ActivityScreenArchiveBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Locale

class ScreenArchive : AppCompatActivity() {

    private lateinit var binding: ActivityScreenArchiveBinding
    private val saleDao by lazy { AppDatabase.getIntance(this).saleDao() }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScreenArchiveBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val months = listOf("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12")
        val years = listOf("2025", "2026", "2027")

        binding.spinnerMonth.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, months)
        binding.spinnerYear.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, years)

        binding.btnArchive.setOnClickListener {
            val selectedMonth = binding.spinnerMonth.selectedItem.toString()
            val selectedYear = binding.spinnerYear.selectedItem.toString()
            val monthYear = "$selectedMonth/$selectedYear"
            generatePdfSummary(monthYear)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun generatePdfSummary(month: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val sdfInput = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val sdfFilter = SimpleDateFormat("MM/yyyy", Locale.getDefault())

                val sales = saleDao.listAll().filter {
                    val date = sdfInput.parse(it.dataSale)
                    sdfFilter.format(date!!) == month
                }

                val allProducts = AppDatabase.getIntance(this@ScreenArchive).productDao().get()

                val outputStream = ByteArrayOutputStream()
                val writer = PdfWriter(outputStream)
                val pdfDoc = PdfDocument(writer)
                val document = Document(pdfDoc)

                document.add(Paragraph("Resumo de Vendas - $month").setBold().setFontSize(18f))
                document.add(Paragraph(" "))

                var totalMonth = 0.0

                for (sale in sales) {
                    val product = allProducts.find { it.uid == sale.productId }
                    val price = product?.preco?.replace(",", ".")?.toDoubleOrNull() ?: 0.0
                    val totalSale = price * sale.quantitySale
                    totalMonth += totalSale


                    document.add(Paragraph("Cliente: ${sale.nameClient}"))
                    document.add(Paragraph("Endereço: ${sale.address}"))
                    document.add(Paragraph("Produto: ${sale.nameProduct}"))
                    document.add(Paragraph("Qtd: ${sale.quantitySale}"))
                    document.add(Paragraph("Preço unitário: R$ %.2f".format(price)))
                    document.add(Paragraph("Preço Total: R$ %.2f".format(totalSale)))
                    document.add(Paragraph("Data: ${sale.dataSale}"))
                    document.add(Paragraph("-----------------------------"))
                }

                document.add(Paragraph("TOTAL DO MÊS: R$ %.2f".format(totalMonth)).setBold())
                document.close()

                val fileName = "Resumo_Vendas_06_2025_${System.currentTimeMillis()}.pdf"
                val sucesso = savePdfToDownloads(this@ScreenArchive, fileName, outputStream.toByteArray())

                runOnUiThread {
                    if (sucesso) {
                        binding.txtResult.text = "Arquivo salvo em: Download/$fileName"
                        Toast.makeText(this@ScreenArchive, "PDF salvo com sucesso!", Toast.LENGTH_LONG).show()
                    } else {
                        binding.txtResult.text = "Erro ao salvar PDF"
                        Toast.makeText(this@ScreenArchive, "Erro ao salvar PDF", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@ScreenArchive, "Erro: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun savePdfToDownloads(context: Context, fileName: String, content: ByteArray): Boolean {
        return try {
            val resolver = context.contentResolver

            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS) // <- Correto
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }

            val uri = resolver.insert(MediaStore.Files.getContentUri("external"), contentValues)
                ?: throw IOException("Erro ao criar URI no MediaStore.")

            resolver.openOutputStream(uri)?.use { it.write(content) }

            contentValues.clear()
            contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
            resolver.update(uri, contentValues, null, null)

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }


}
