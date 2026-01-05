package com.example.rastreadoripn

import android.Manifest
import android.app.*
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.util.Date

class LocationService : Service() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private val gson = Gson()

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        createNotificationChannel()

        // Callback que recibe las coordenadas
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                super.onLocationResult(result)
                result.lastLocation?.let { location ->
                    // 1. Guardar
                    saveLocation(location)

                    // 2. Avisar a la UI (Mapa)
                    val intent = Intent("UPDATE_UI")
                    intent.putExtra("lat", location.latitude)
                    intent.putExtra("lon", location.longitude)
                    sendBroadcast(intent)

                    // DEBUG: Muestra un mensajito abajo cada vez que guarda (para que sepas que funciona)
                    Log.d("RASTREO", "Ubicación guardada: ${location.latitude}, ${location.longitude}")
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val interval = intent?.getLongExtra("INTERVAL", 10000L) ?: 10000L

        // Notificación obligatoria para Foreground Service
        val notification = NotificationCompat.Builder(this, "CHANNEL_ID")
            .setContentTitle("Rastreo IPN Activo")
            .setContentText("Guardando cada ${interval/1000} seg")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        startForeground(1, notification)

        // Verificar permisos ANTES de pedir actualizaciones
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates(interval)
            Toast.makeText(this, "Servicio iniciado. Esperando GPS...", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "ERROR: No hay permisos de ubicación", Toast.LENGTH_LONG).show()
            stopSelf() // Detener servicio si no hay permiso
        }

        return START_STICKY
    }

    private fun startLocationUpdates(interval: Long) {
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, interval)
            .setWaitForAccurateLocation(false) // No esperar precisión perfecta (más rápido)
            .setMinUpdateIntervalMillis(interval)
            .build()

        try {
            fusedLocationClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper())
        } catch (e: SecurityException) {
            Log.e("RASTREO", "Error de seguridad: ${e.message}")
        }
    }

    private fun saveLocation(location: android.location.Location) {
        try {
            val file = File(filesDir, "history.json")
            val list: ArrayList<LocationData> = if (file.exists()) {
                gson.fromJson(file.readText(), object : TypeToken<ArrayList<LocationData>>() {}.type)
            } else ArrayList()

            list.add(LocationData(location.latitude, location.longitude, System.currentTimeMillis(), location.accuracy))
            file.writeText(gson.toJson(list))

        } catch (e: Exception) {
            Log.e("RASTREO", "Error guardando archivo: ${e.message}")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
        Toast.makeText(this, "Rastreo detenido", Toast.LENGTH_SHORT).show()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        val channel = NotificationChannel("CHANNEL_ID", "Rastreo Service", NotificationManager.IMPORTANCE_LOW)
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }
}