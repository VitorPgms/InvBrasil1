package com.vitordepaula.invbrasil.view

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
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

    private fun generatePdfSummary(month: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val sdfInput = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val sdfFilter = SimpleDateFormat("MM/yyyy", Locale.getDefault())

                val sales = saleDao.listAll().filter {
                    val date = sdfInput.parse(it.dataSale)
                    sdfFilter.format(date!!) == month
                }

                //val total = sales.sumOf { it.quantitySale * it.priceUnit }

                val outputStream = ByteArrayOutputStream()
                val writer = PdfWriter(outputStream)
                val pdfDoc = PdfDocument(writer)
                val document = Document(pdfDoc)

                document.add(Paragraph("Resumo de Vendas - $month").setBold().setFontSize(18f))
                document.add(Paragraph(" "))

                for (sale in sales) {
                    document.add(Paragraph("Cliente: ${sale.nameClient}"))
                    document.add(Paragraph("Endereço: ${sale.address}"))
                    document.add(Paragraph("Produto: ${sale.nameProduct}"))
                    document.add(Paragraph("Qtd: ${sale.quantitySale}"))
                    //document.add(Paragraph("Valor: R$ %.2f".format(sale.quantitySale * sale.priceUnit)))
                    document.add(Paragraph("Data: ${sale.dataSale}"))
                    document.add(Paragraph("-----------------------------"))
                }

                //document.add(Paragraph("TOTAL DO MÊS: R$ %.2f".format(total)).setBold())
                document.close()

                val fileName = "Resumo_Vendas_${month.replace("/", "_")}.pdf"
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
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, fileName)
            put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
            put(MediaStore.Downloads.IS_PENDING, 1)
        }

        val collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val uri = resolver.insert(collection, contentValues) ?: return false

        resolver.openOutputStream(uri)?.use { outputStream ->
            outputStream.write(content)
        }

        contentValues.clear()
        contentValues.put(MediaStore.Downloads.IS_PENDING, 0)
        resolver.update(uri, contentValues, null, null)

        return true
    }
}
