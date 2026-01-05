package com.example.rastreadoripn

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.util.Date

class HistoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        loadHistory()

        findViewById<Button>(R.id.btnClear).setOnClickListener {
            File(filesDir, "history.json").delete()
            loadHistory()
        }
    }

    private fun loadHistory() {
        val tv = findViewById<TextView>(R.id.tvHistory)
        val file = File(filesDir, "history.json")
        if (file.exists()) {
            val list: ArrayList<LocationData> = Gson().fromJson(file.readText(), object : TypeToken<ArrayList<LocationData>>() {}.type)
            val sb = StringBuilder()
            list.reversed().forEach {
                sb.append("Fecha: ${Date(it.timestamp)}\nLat: ${it.latitude}, Lon: ${it.longitude}\nPrecisión: ${it.accuracy}m\n\n")
            }
            tv.text = sb.toString()
        } else {
            tv.text = "No hay historial."
        }
    }
}