# Rastreador de Ubicación IPN - Examen Final

Aplicación nativa en Android desarrollada para la unidad de aprendizaje **Desarrollo de Aplicaciones Móviles Nativas** de la **Escuela Superior de Cómputo (ESCOM - IPN)**.

La aplicación permite el rastreo de la ubicación del usuario en tiempo real, persistiendo los datos localmente y dibujando la ruta en un mapa, con soporte para ejecución en segundo plano (Background Service).

## 📋 Requerimientos del Examen

Este proyecto cumple con la totalidad de los requerimientos solicitados en la evaluación:

### 1. Rastreo de Ubicación
- **GPS en Tiempo Real:** Obtención de coordenadas (latitud, longitud) mediante `FusedLocationProviderClient`.
- **Intervalos Configurables:** Selector para actualizar la ubicación cada 10s, 60s o 5 minutos.
- **Segundo Plano (Background):** Implementación de un **Foreground Service** con notificación persistente para garantizar el rastreo incluso si la app se minimiza o el teléfono se bloquea.

### 2. Visualización en Mapa
- **Motor de Mapas:** Integración de **OpenStreetMap (osmdroid)**.
- **Marcadores y Rutas:** Visualización de la posición actual y dibujo de una `Polyline` (línea roja) que conecta todo el historial de movimiento.
- **Actualización UI:** Refresco automático del mapa mediante `BroadcastReceiver` cuando llega una nueva coordenada.

### 3. Almacenamiento (Persistencia)
- **Historial Local:** Guardado de coordenadas, timestamp y precisión en un archivo local `history.json`.
- **Formato:** Serialización y deserialización de objetos usando la librería **Gson**.

### 4. Interfaz y Personalización (Temas IPN)
- **Pantalla Principal:** Mapa interactivo, coordenadas en tiempo real y controles de servicio.
- **Pantalla Historial:** Visualización del log completo de ubicaciones.
- **Temas Dinámicos:** Cambio de tema en tiempo de ejecución cumpliendo la paleta de colores institucional:
  - 🔴 **Tema Guinda (IPN):** `#6C1D45`
  - 🔵 **Tema Azul (ESCOM):** `#00679E`

## 🛠️ Tecnologías y Librerías Utilizadas

* **Lenguaje:** Kotlin
* **Mapas:** `org.osmdroid:osmdroid-android:6.1.16` (OpenStreetMap)
* **Ubicación:** `com.google.android.gms:play-services-location:21.0.1`
* **Datos/JSON:** `com.google.code.gson:gson:2.10.1`
* **Componentes de Arquitectura:**
    * `Foreground Service`: Para procesos de larga duración.
    * `BroadcastReceiver`: Comunicación entre Servicio y Activity.
    * `SharedPreferences`: Persistencia de configuración de temas.

## 🚀 Instalación y Ejecución

1.  **Clonar el repositorio:**
    ```bash
    git clone [https://github.com/Alejandro261102/ExamenMoviles.git](https://github.com/Alejandro261102/ExamenMoviles.git)
    ```
2.  **Abrir en Android Studio:**
    * Selecciona la carpeta del proyecto.
    * Espera a que Gradle sincronice las dependencias.
3.  **Permisos:**
    * Al ejecutar la aplicación por primera vez, **acepta todos los permisos** de ubicación y notificaciones solicitados.
    * *Nota:* Para pruebas en emulador, asegúrate de simular una ruta en las herramientas extendidas del emulador (Location -> Load GPX/KML o Set Location).

## 📸 Capturas de Pantalla

| Pantalla Principal (Ruta) | Historial de Ubicaciones | Cambio de Tema (IPN/ESCOM) |
|:-------------------------:|:------------------------:|:--------------------------:|
| *(Inserte captura aquí)* | *(Inserte captura aquí)* | *(Inserte captura aquí)* |

## 📄 Estructura del Proyecto

* `MainActivity.kt`: Lógica de UI, manejo del Mapa (OSM) y dibujo de rutas (`Polyline`).
* `LocationService.kt`: Servicio en primer plano que gestiona el GPS y guarda el JSON.
* `HistoryActivity.kt`: Lectura y visualización del archivo `history.json`.
* `LocationData.kt`: Modelo de datos (Data Class).

---
**Instituto Politécnico Nacional - Escuela Superior de Cómputo**
*Examen Final 2026-1*
