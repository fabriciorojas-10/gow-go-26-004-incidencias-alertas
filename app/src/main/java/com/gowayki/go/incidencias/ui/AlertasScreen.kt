package com.gowayki.go.incidencias.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gowayki.go.incidencias.model.AlertasRepository
import com.gowayki.go.incidencias.model.AlertaVial
import com.gowayki.go.incidencias.model.TipoAlerta

// Paleta de Colores de Marca Gowayki
private val GowaykiOrange = Color(0xFFE85D04)
private val GowaykiGlassSlate = Color(0xCC1E2229)
private val GowaykiOrangeSubtleBorder = Color(0x40E85D04)
private val GowaykiTextPrimary = Color(0xFFFFFFFF)
private val GowaykiTextSecondary = Color(0xFFA0AAB8)

@Composable
fun AlertasScreen() {
    val alertas = remember { AlertasRepository.alertasArequipa }
    var selectedAlerta by remember { mutableStateOf<AlertaVial?>(null) }

    Scaffold(
        containerColor = Color(0xFF1E2229)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 1. Mapa MapLibre con teselas OpenStreetMap como fondo completo
            MapLibreViewComponent(
                alertas = alertas,
                selectedAlerta = selectedAlerta,
                onAlertaSelected = { alerta ->
                    selectedAlerta = alerta
                },
                modifier = Modifier.fillMaxSize()
            )

            // 2. Header Superior Integrado (Píldora flotante compacta de 48.dp de alto, esquinas 16.dp, translúcido 0xDD1E2229)
            CompactGowaykiHeader(
                selectedAlerta = selectedAlerta,
                onReset = { selectedAlerta = null },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .fillMaxWidth(0.90f)
                    .padding(top = 8.dp)
            )

            // 3. Cuadro Informativo Blanco (Callout) al 80% de ancho y 10dp de padding interno
            AnimatedVisibility(
                visible = selectedAlerta != null,
                enter = fadeIn() + slideInVertically(initialOffsetY = { -it }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { -it }),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(top = 66.dp)
            ) {
                selectedAlerta?.let { alerta ->
                    FeatureDiscoveryBanner(
                        alerta = alerta,
                        onClose = { selectedAlerta = null }
                    )
                }
            }

            // 4. Lista Inferior Flotante (LazyRow flotando directamente sobre el mapa, con Safe Insets para botones Android)
            LazyRow(
                contentPadding = PaddingValues(horizontal = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(bottom = 26.dp)
            ) {
                items(alertas, key = { it.id }) { alerta ->
                    CompactAlertaCard(
                        alerta = alerta,
                        isSelected = alerta.id == selectedAlerta?.id,
                        onSelect = { selectedAlerta = alerta }
                    )
                }
            }
        }
    }
}

@Composable
fun CompactGowaykiHeader(
    selectedAlerta: AlertaVial?,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color(0xDD1E2229),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, GowaykiOrangeSubtleBorder),
        shadowElevation = 6.dp,
        modifier = modifier
            .height(48.dp)
            .graphicsLayer { alpha = 0.95f }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = GowaykiOrange,
                    modifier = Modifier.size(26.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Gowayki Logo",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Gowayki GO",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = GowaykiTextPrimary
                    )
                    Text(
                        text = "Monitor Vial Arequipa",
                        fontSize = 9.sp,
                        color = GowaykiTextSecondary
                    )
                }
            }

            if (selectedAlerta != null) {
                Button(
                    onClick = onReset,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GowaykiOrange
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.height(28.dp)
                ) {
                    Text("Recentrar", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun FeatureDiscoveryBanner(
    alerta: AlertaVial,
    onClose: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(1.dp, alerta.tipo.severidad.color.copy(alpha = 0.4f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = Modifier.fillMaxWidth(0.80f)
    ) {
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = alerta.tipo.severidad.color,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = alerta.tipo.severidad.nombre.uppercase(),
                            color = Color.White,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = alerta.ubicacion,
                        color = Color(0xFF64748B),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar Guía",
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = alerta.titulo,
                color = Color(0xFF0F172A),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = alerta.descripcion,
                color = Color(0xFF475569),
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    color = Color(0xFFFEF2F2),
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(0.5.dp, GowaykiOrange.copy(alpha = 0.3f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = GowaykiOrange,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Column {
                            Text("Retraso", fontSize = 8.sp, color = Color(0xFF64748B))
                            Text(alerta.retrasoEstimado, fontSize = 10.sp, color = Color(0xFF0F172A), fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Surface(
                    color = Color(0xFFF0F9FF),
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(0.5.dp, Color(0xFF0284C7).copy(alpha = 0.3f)),
                    modifier = Modifier.weight(1.3f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = null,
                            tint = Color(0xFF0284C7),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Column {
                            Text("Acción", fontSize = 8.sp, color = Color(0xFF64748B))
                            Text(alerta.accionSugerida, fontSize = 9.sp, color = Color(0xFF0F172A), maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CompactAlertaCard(
    alerta: AlertaVial,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val severidadColor = alerta.tipo.severidad.color
    val borderColor = if (isSelected) GowaykiOrange else GowaykiOrangeSubtleBorder
    val cardBgColor = GowaykiGlassSlate

    Card(
        modifier = Modifier
            .width(185.dp)
            .height(80.dp)
            .clickable { onSelect() }
            .graphicsLayer { alpha = 0.95f },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardBgColor),
        border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 6.dp else 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    color = severidadColor.copy(alpha = 0.2f),
                    shape = CircleShape,
                    modifier = Modifier.size(22.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = getIconoDiferenciado(alerta.tipo),
                            contentDescription = alerta.tipo.tituloTipo,
                            tint = severidadColor,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = alerta.titulo,
                        color = GowaykiTextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = alerta.ubicacion,
                        color = GowaykiTextSecondary,
                        fontSize = 9.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    color = severidadColor,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = alerta.tipo.severidad.nombre.uppercase(),
                        color = Color.White,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                    )
                }

                Text(
                    text = "Retraso: ${alerta.retrasoEstimado}",
                    color = if (isSelected) GowaykiOrange else GowaykiTextPrimary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// Íconos de UI diferenciados para la regla de QA
private fun getIconoDiferenciado(tipo: TipoAlerta): ImageVector {
    return when (tipo) {
        TipoAlerta.CIERRE_VIA -> Icons.Default.Warning
        TipoAlerta.DESVIO -> Icons.Default.Place
        TipoAlerta.OBRA -> Icons.Default.Build
        TipoAlerta.ACCIDENTE -> Icons.Default.Warning
        TipoAlerta.CONGESTION -> Icons.Default.Info
    }
}
