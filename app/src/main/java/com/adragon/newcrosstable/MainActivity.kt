package com.adragon.newcrosstable

import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.View
import android.webkit.URLUtil
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.FileInputStream


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val path = intent.getStringExtra("excelPath")
        val sheet = getSheetFromAssets(path)

        val brandArr = getColumnAsArray(sheet, 0)
        var modelArr = arrayOf("", "Модель")
        var partArr = arrayOf("", "Наименование (по Керхер)")

        val brandSelector = setSelector(R.id.brandSelector, brandArr)
        var modelSelector = setSelector(R.id.modelSelector, modelArr)
        var partSelector = setSelector(R.id.partSelector, partArr)
        modelSelector.isEnabled = false
        partSelector.isEnabled = false

        brandSelector.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (brandSelector.selectedItem.toString() != brandArr.last()) {
                    val selected = brandSelector.selectedItem.toString()
                    modelArr = getColumnAsArray(sheet, 1, selected to 0)
                    modelSelector = setSelector(R.id.modelSelector, modelArr)
                    setToDefault(
                        R.id.partSelector,
                        R.id.detailsTextView,
                        R.id.vendorCodeTexView,
                        R.id.KarchervendorCodeTexView,
                        R.id.noteTextView,
                        R.id.authorTextView
                    )
                }
            }
        }

        modelSelector.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (modelSelector.selectedItem.toString() != modelArr.last()) {
                    val selected = modelSelector.selectedItem.toString()
                    val selectedBrand = brandSelector.selectedItem.toString()
                    partArr = getColumnAsArray(sheet, 2, selectedBrand to 0, selected to 1)
                    partSelector = setSelector(R.id.partSelector, partArr)
                    partSelector.isEnabled = true

                    setToDefault(
                        -1,
                        R.id.vendorCodeTexView,
                        R.id.KarchervendorCodeTexView,
                        R.id.noteTextView,
                        R.id.authorTextView
                    )
                    val detailsLink =
                        getColumnAsArray(sheet, 7, selectedBrand to 0, selected to 1).first()
                    setTextView(R.id.detailsTextView, detailsLink)
                }
            }
        }

        partSelector.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (partSelector.selectedItem.toString() != partArr.last()) {
                    val selected = partSelector.selectedItem.toString()
                    val selectedBrand = brandSelector.selectedItem.toString()
                    val selectedModel = modelSelector.selectedItem.toString()

                    val vendorCode = getColumnAsArray(
                        sheet,
                        3,
                        selectedBrand to 0,
                        selectedModel to 1,
                        selected to 2
                    ).first()
                    val karcherVendorCode = getColumnAsArray(
                        sheet,
                        4,
                        selectedBrand to 0,
                        selectedModel to 1,
                        selected to 2
                    ).first()
                    val note = getColumnAsArray(
                        sheet,
                        5,
                        selectedBrand to 0,
                        selectedModel to 1,
                        selected to 2
                    ).first()
                    val author = getColumnAsArray(
                        sheet,
                        6,
                        selectedBrand to 0,
                        selectedModel to 1,
                        selected to 2
                    ).first()

                    setTextView(R.id.vendorCodeTexView, " Артиукул: $vendorCode")
                    setTextView(
                        R.id.KarchervendorCodeTexView,
                        " Артикул керхер: $karcherVendorCode"
                    )
                    setTextView(R.id.noteTextView, " Примечание: $note")
                    setTextView(R.id.authorTextView, "   Автор: $author")
                }
            }
        }
    }

    private fun getSheetFromAssets(path: String?): Sheet {
        val am = assets
        val istream = if (path == null) am.open("info.xlsx")
        else FileInputStream(path)
        val wb = XSSFWorkbook(istream)
        return wb.getSheetAt(0)
    }

    private fun setSelector(id: Int, arr: Array<String>): Spinner {
        val adapter = CustomAdapter(applicationContext, arr)
        val spinner = findViewById<Spinner>(id)
        spinner.adapter = adapter
        spinner.setSelection(adapter.count)
        spinner.isEnabled = true
        return spinner
    }

    private fun setTextView(id: Int, text: String) {
        val textView = findViewById<TextView>(id)
        textView.textSize = 20F
        textView.text =
            if (id == R.id.detailsTextView) {
                when {
                    text.lowercase().contains("нет") -> "Деталировки отсутствуют"
                    URLUtil.isValidUrl(text) -> Html.fromHtml("<a href=\"$text\">   Деталировки</a> ")
                    else -> text
                }
            } else text
        textView.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun setToDefault(partSpinnerId: Int, vararg textViewIds: Int) {
        if (partSpinnerId != -1) {
            val spinner = setSelector(R.id.partSelector, arrayOf("", "Наименование (по Керхер)"))
            spinner.isEnabled = false
        }
        for (id in textViewIds) {
            setTextView(id, "")
        }
    }

    private fun getColumnAsArray(
        sheet: Sheet,
        id: Int,
        vararg pairs: Pair<String, Int>
    ): Array<String> {
        val lst = mutableListOf<String>()
        for (i in 1 until sheet.physicalNumberOfRows) {
            val row = sheet.getRow(i)
            val current = row?.getCell(id) ?: continue

            if (pairs.isEmpty()) lst.add(current.toString())
            else
                for (pair in pairs) {
                    if (row.getCell(pair.second) != null && row.getCell(pair.second)
                            .toString() == pair.first
                    )
                        lst.add(current.toString())
                }
        }
        lst.add(sheet.getRow(0)?.getCell(id).toString())
        return lst.distinct().toTypedArray()
    }

    /*
TODO:
Main task's:
* Проверить есть ли загруженная таблица в ассетах (мб другая папка)
* Если нет, то предложить пользователю выбрать таблицу из файловой системы телефона .xls/.xlsx
* Если да, то загрузить её
* Добавить кнопку с возможностью смены таблицы

General task's:
* Шрифт / дизайн текствью
* App theme no title bar
* Дизйан всего приложения в целом
* иконка

Side task's:
* по дефолту почему-то лист отображается снизу
* Проверка воркинга на xls/xlsx (В чём дифф?)
* рефакторинг? (Refactor it later (:)
*/
}