package com.gowayki.go.incidencias.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.gowayki.go.incidencias.model.AlertaVial
import org.maplibre.android.annotations.IconFactory
import org.maplibre.android.annotations.Marker
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style

private const val AREQUIPA_LAT = -16.4090
private const val AREQUIPA_LNG = -71.5375
private const val DEFAULT_ZOOM = 12.5
private const val ALERT_ZOOM = 16.5
private const val MAP_STYLE_URL = "https://demotiles.maplibre.org/style.json"

@Suppress("DEPRECATION")
@Composable
fun MapLibreViewComponent(
    alertas: List<AlertaVial>,
    selectedAlerta: AlertaVial?,
    onAlertaSelected: (AlertaVial) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val mapView = remember {
        MapView(context).apply {
            onCreate(Bundle())
        }
    }

    var maplibreMap by remember { mutableStateOf<MapLibreMap?>(null) }
    val principalMarkerMap = remember { mutableMapOf<String, Marker>() }

    // Manejo del ciclo de vida de MapView
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapView.onDestroy()
        }
    }

    // Cargar mapa y renderizar escenas realistas
    AndroidView(
        factory = { mapView },
        modifier = modifier
    ) {
        if (maplibreMap == null) {
            mapView.getMapAsync { map ->
                maplibreMap = map
                map.setStyle(Style.Builder().fromUri(MAP_STYLE_URL)) { _ ->
                    // Posición inicial centrada en Arequipa
                    val initialPosition = CameraPosition.Builder()
                        .target(LatLng(AREQUIPA_LAT, AREQUIPA_LNG))
                        .zoom(DEFAULT_ZOOM)
                        .build()
                    map.cameraPosition = initialPosition

                    map.clear()
                    principalMarkerMap.clear()

                    // Renderizar cada escena con sus elementos vectoriales combinar
                    alertas.forEach { alerta ->
                        alerta.elementosEscena.forEach { elemento ->
                            val bitmap = drawableToBitmap(
                                context = context,
                                drawableId = elemento.drawableRes,
                                sizeDp = if (elemento.esPrincipal) 44 else 32
                            )
                            val icon = IconFactory.getInstance(context).fromBitmap(bitmap)

                            val markerOptions = MarkerOptions()
                                .position(LatLng(elemento.latitud, elemento.longitud))
                                .title("${alerta.tipo.tituloTipo}: ${alerta.titulo}")
                                .snippet(alerta.descripcion)
                                .icon(icon)

                            val marker = map.addMarker(markerOptions)
                            if (elemento.esPrincipal) {
                                principalMarkerMap[alerta.id] = marker
                            }
                        }
                    }

                    // Evento de toque en cualquier marcador del mapa
                    map.setOnMarkerClickListener { marker ->
                        val matchedAlert = alertas.find { alerta ->
                            alerta.elementosEscena.any { el ->
                                el.latitud == marker.position.latitude &&
                                        el.longitud == marker.position.longitude
                            }
                        }
                        matchedAlert?.let {
                            onAlertaSelected(it)
                        }
                        false
                    }
                }
            }
        }
    }

    // Animación de cámara al seleccionar una alerta (Zoom 16.5)
    LaunchedEffect(selectedAlerta, maplibreMap) {
        val map = maplibreMap ?: return@LaunchedEffect
        val alerta = selectedAlerta ?: return@LaunchedEffect

        val targetPosition = LatLng(alerta.latitud, alerta.longitud)
        val cameraUpdate = CameraUpdateFactory.newCameraPosition(
            CameraPosition.Builder()
                .target(targetPosition)
                .zoom(ALERT_ZOOM)
                .bearing(15.0)
                .tilt(30.0)
                .build()
        )

        map.animateCamera(cameraUpdate, 1500)

        // Resaltar marcador principal de la escena
        principalMarkerMap[alerta.id]?.let { marker ->
            map.selectMarker(marker)
        }
    }
}

private fun drawableToBitmap(context: Context, drawableId: Int, sizeDp: Int): Bitmap {
    val drawable = ContextCompat.getDrawable(context, drawableId)
        ?: return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    val density = context.resources.displayMetrics.density
    val px = (sizeDp * density).toInt()
    val bitmap = Bitmap.createBitmap(px, px, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
}
