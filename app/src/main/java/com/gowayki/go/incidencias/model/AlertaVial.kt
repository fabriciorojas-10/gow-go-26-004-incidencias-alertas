package com.gowayki.go.incidencias.model

import androidx.compose.ui.graphics.Color
import com.gowayki.go.incidencias.R

enum class Severidad(
    val nombre: String,
    val color: Color
) {
    URGENTE("Urgente", Color(0xFFDC2626)), // Rojo
    AVISO("Aviso", Color(0xFFEA580C)),     // Naranja
    INFO("Info", Color(0xFFCA8A04))        // Amarillo
}

enum class TipoAlerta(
    val tituloTipo: String,
    val severidad: Severidad
) {
    CIERRE_VIA("Cierre de vía", Severidad.URGENTE),
    DESVIO("Desvío", Severidad.AVISO),
    OBRA("Obra", Severidad.AVISO),
    ACCIDENTE("Accidente", Severidad.URGENTE),
    CONGESTION("Congestión", Severidad.INFO)
}

data class ElementoEscena(
    val id: String,
    val drawableRes: Int,
    val latitud: Double,
    val longitud: Double,
    val titulo: String,
    val esPrincipal: Boolean = false
)

data class AlertaVial(
    val id: String,
    val titulo: String,
    val tipo: TipoAlerta,
    val descripcion: String,
    val ubicacion: String,
    val latitud: Double,
    val longitud: Double,
    val retrasoEstimado: String,
    val accionSugerida: String,
    val elementosEscena: List<ElementoEscena>
)

object AlertasRepository {
    val alertasArequipa = listOf(
        AlertaVial(
            id = "1",
            titulo = "Cierre total en Av. Ejército",
            tipo = TipoAlerta.CIERRE_VIA,
            descripcion = "Cierre total por evento y mantenimiento vial. Vía inhabilitada.",
            ubicacion = "Av. Ejército - Mall Plaza, Yanahuara",
            latitud = -16.3892,
            longitud = -71.5430,
            retrasoEstimado = "+20 min",
            accionSugerida = "Toma la vía auxiliar por Calle Los Arces",
            elementosEscena = listOf(
                ElementoEscena("cierre_1", R.drawable.cierre_barrera, -16.3892, -71.5430, "Barrera Principal", esPrincipal = true),
                ElementoEscena("cierre_2", R.drawable.cierre_barrera, -16.3885, -71.5422, "Barrera Norte"),
                ElementoEscena("cierre_3", R.drawable.cierre_barrera, -16.3898, -71.5438, "Barrera Sur"),
                ElementoEscena("cierre_4", R.drawable.cierre_barrera, -16.3905, -71.5445, "Barrera Auxiliar")
            )
        ),
        AlertaVial(
            id = "2",
            titulo = "Desvío obligatorio en Puente Chilina",
            tipo = TipoAlerta.DESVIO,
            descripcion = "Desvío de tránsito pesado por trabajos de inspección estructural.",
            ubicacion = "Puente Chilina - Acceso Cayma",
            latitud = -16.3835,
            longitud = -71.5380,
            retrasoEstimado = "+12 min",
            accionSugerida = "Gira hacia la rampa de Av. Bolognesi",
            elementosEscena = listOf(
                ElementoEscena("desvio_1", R.drawable.desvio_senal, -16.3835, -71.5380, "Señal Desvío Principal", esPrincipal = true),
                ElementoEscena("desvio_2", R.drawable.auto_cola_blue, -16.3840, -71.5375, "Auto desviándose"),
                ElementoEscena("desvio_3", R.drawable.auto_cola_amber, -16.3830, -71.5385, "Vehículo en ruta alternativa")
            )
        ),
        AlertaVial(
            id = "3",
            titulo = "Obras y excavación en Av. La Marina",
            tipo = TipoAlerta.OBRA,
            descripcion = "Trabajos de recarpeteo y excavación con maquinaria pesada.",
            ubicacion = "Av. La Marina - Cercado de Arequipa",
            latitud = -16.3995,
            longitud = -71.5410,
            retrasoEstimado = "+15 min",
            accionSugerida = "Carril izquierdo bloqueado, reduce la velocidad",
            elementosEscena = listOf(
                ElementoEscena("obra_1", R.drawable.obra_excavadora, -16.3995, -71.5410, "Excavadora en Zona", esPrincipal = true),
                ElementoEscena("obra_2", R.drawable.obra_cono, -16.3990, -71.5405, "Cono de seguridad Norte"),
                ElementoEscena("obra_3", R.drawable.obra_cono, -16.4000, -71.5415, "Cono de seguridad Centro"),
                ElementoEscena("obra_4", R.drawable.obra_cono, -16.4005, -71.5420, "Cono de seguridad Sur")
            )
        ),
        AlertaVial(
            id = "4",
            titulo = "Accidente choque en Variante Uchumayo",
            tipo = TipoAlerta.ACCIDENTE,
            descripcion = "Colisión múltiple entre vehículos. Tránsito detenido.",
            ubicacion = "Variante de Uchumayo - Km 2.5",
            latitud = -16.4150,
            longitud = -71.5680,
            retrasoEstimado = "+25 min",
            accionSugerida = "Utiliza la Vía de Evitamiento como ruta alternativa",
            elementosEscena = listOf(
                ElementoEscena("accidente_1", R.drawable.accidente_choque, -16.4150, -71.5680, "Punto de Choque", esPrincipal = true),
                ElementoEscena("accidente_2", R.drawable.auto_cola_red, -16.4154, -71.5675, "Auto detenido 1"),
                ElementoEscena("accidente_3", R.drawable.auto_cola_amber, -16.4158, -71.5670, "Auto detenido 2"),
                ElementoEscena("accidente_4", R.drawable.auto_cola_slate, -16.4162, -71.5665, "Auto en cola")
            )
        ),
        AlertaVial(
            id = "5",
            titulo = "Congestión alta en Av. Avelino Cáceres",
            tipo = TipoAlerta.CONGESTION,
            descripcion = "Corredor vehicular saturado por hora punta y zona comercial.",
            ubicacion = "Av. Avelino Cáceres - Bustamante y Rivero",
            latitud = -16.4270,
            longitud = -71.5310,
            retrasoEstimado = "+18 min",
            accionSugerida = "Avanza con precaución a vuelta de rueda",
            elementosEscena = listOf(
                ElementoEscena("congestion_1", R.drawable.auto_cola_red, -16.4270, -71.5310, "Tráfico intenso Centro", esPrincipal = true),
                ElementoEscena("congestion_2", R.drawable.auto_cola_amber, -16.4265, -71.5315, "Fila de vehículos Norte"),
                ElementoEscena("congestion_3", R.drawable.auto_cola_blue, -16.4275, -71.5305, "Fila de vehículos Sur"),
                ElementoEscena("congestion_4", R.drawable.auto_cola_red, -16.4280, -71.5300, "Vehículos detenidos"),
                ElementoEscena("congestion_5", R.drawable.auto_cola_slate, -16.4285, -71.5295, "Cola de tráfico")
            )
        )
    )
}
