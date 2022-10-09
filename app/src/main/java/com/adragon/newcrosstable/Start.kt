package com.adragon.newcrosstable

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class Start : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.start)

        val fragment: FileChooserFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_fileChooser) as FileChooserFragment
        fragment.editTextPath.text = "Выбор таблицы с данными(.xls/.xlsx)"
        val next = findViewById<Button>(R.id.next)
        next.setOnClickListener { passIntent(fragment) }
    }

    private fun passIntent(frag: FileChooserFragment) {
        val intent = Intent(
            applicationContext,
            MainActivity::class.java
        )
        println(frag.path.toString())
        intent.putExtra("excelPath", frag.path)
        startActivity(intent)
    }
}