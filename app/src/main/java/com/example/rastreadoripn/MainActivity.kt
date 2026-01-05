package com.example.rastreadoripn

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import android.widget.*

class MainActivity : AppCompatActivity() {

    private lateinit var map: MapView
    private var isIpTheme = true // Para alternar temas
    private val locationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val lat = intent?.getDoubleExtra("lat", 0.0) ?: 0.0
            val lon = intent?.getDoubleExtra("lon", 0.0) ?: 0.0
            updateMap(lat, lon)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Cargar tema guardado
        val prefs = getSharedPreferences("APP_PREFS", MODE_PRIVATE)
        isIpTheme = prefs.getBoolean("THEME_IPN", true)
        setTheme(if (isIpTheme) R.style.Theme_Guinda else R.style.Theme_Azul)

        super.onCreate(savedInstanceState)
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))
        setContentView(R.layout.activity_main)

        // Init Map
        map = findViewById(R.id.map)
        map.setMultiTouchControls(true)
        map.controller.setZoom(18.0)
        // Default point (CDMX)
        map.controller.setCenter(GeoPoint(19.4326, -99.1332))

        setupButtons()
        checkPermissions()
    }

    private fun setupButtons() {
        val rgInterval = findViewById<RadioGroup>(R.id.rgInterval)

        findViewById<Button>(R.id.btnStart).setOnClickListener {
            val interval = when (rgInterval.checkedRadioButtonId) {
                R.id.rb10s -> 10000L
                R.id.rb60s -> 60000L
                else -> 300000L
            }
            val intent = Intent(this, LocationService::class.java)
            intent.putExtra("INTERVAL", interval)
            startForegroundService(intent)
        }

        findViewById<Button>(R.id.btnStop).setOnClickListener {
            stopService(Intent(this, LocationService::class.java))
        }

        findViewById<Button>(R.id.btnHistory).setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }

        findViewById<Button>(R.id.btnTheme).setOnClickListener {
            val prefs = getSharedPreferences("APP_PREFS", MODE_PRIVATE)
            prefs.edit().putBoolean("THEME_IPN", !isIpTheme).apply()
            recreate() // Recargar activity para aplicar tema
        }
    }

    private fun updateMap(lat: Double, lon: Double) {
        val point = GeoPoint(lat, lon)
        map.controller.setCenter(point)

        // Actualizar UI Texto
        findViewById<TextView>(R.id.tvCoords).text = "Lat: $lat, Lon: $lon"

        // Marcador [cite: 25]
        map.overlays.clear()
        val marker = Marker(map)
        marker.position = point
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.title = "Aquí estás"
        map.overlays.add(marker)

        // Aquí podrías leer el JSON completo para dibujar la línea (Polyline) [cite: 26]
        // Por tiempo, el marcador actual es lo crítico.
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(locationReceiver, IntentFilter("UPDATE_UI"), Context.RECEIVER_NOT_EXPORTED)
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(locationReceiver)
        map.onPause()
    }

    private fun checkPermissions() {
        val permissions = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        // Para Android 13+ (Notificaciones)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        // Para Android 14+ (Foreground Location)
        if (android.os.Build.VERSION.SDK_INT >= 34) {
            permissions.add(Manifest.permission.FOREGROUND_SERVICE_LOCATION)
        }

        val missingPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (missingPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, missingPermissions.toTypedArray(), 1)
        }
    }
}