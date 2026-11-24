package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.SatelliteAlt
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.Municipality
import com.example.model.NasaSoilProfile
import com.example.ui.components.UrgencyPill
import com.example.ui.theme.AzulCieloNasa
import com.example.ui.theme.AzulCieloSuave
import com.example.ui.theme.BlancoPuro
import com.example.ui.theme.BordeSutil
import com.example.ui.theme.DoradoAltiplano
import com.example.ui.theme.TerracotaQuinua
import com.example.ui.theme.UrgenciaVerde
import com.example.ui.theme.VerdeBosqueAndino
import com.example.ui.theme.VerdeHojaCultivo
import com.example.ui.theme.VerdeMentaSuave

@Composable
fun SoilConservationScreen(
    selectedMunicipality: Municipality,
    soilProfile: NasaSoilProfile,
    onSelectMunicipality: (Municipality) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Conservación de Suelos · Satélite NASA",
                    style = MaterialTheme.typography.headlineSmall,
                    color = VerdeBosqueAndino
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Municipality.entries.forEach { muni ->
                        val selected = muni == selectedMunicipality
                        Surface(
                            modifier = Modifier
                                .heightIn(min = 50.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { onSelectMunicipality(muni) }
                                .testTag("soil_muni_${muni.name}"),
                            color = if (selected) AzulCieloNasa else MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.5.dp, if (selected) AzulCieloNasa else BordeSutil),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text(
                                text = muni.displayName,
                                style = MaterialTheme.typography.labelLarge,
                                color = if (selected) BlancoPuro else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
                            )
                        }
                    }
                }
            }
        }

        // Panel Satelital NASA POWER / MODIS NDVI / Soil Moisture
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = AzulCieloSuave),
                border = BorderStroke(1.4.dp, AzulCieloNasa.copy(alpha = 0.55f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Filled.SatelliteAlt, contentDescription = null, tint = AzulCieloNasa)
                        Column {
                            Text(
                                text = "Telemetría NASA · ${soilProfile.municipality.displayName}",
                                style = MaterialTheme.typography.titleLarge,
                                color = AzulCieloNasa
                            )
                            Text(
                                text = soilProfile.satelliteSourceNote,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        NdviGauge(
                            ndvi = soilProfile.ndviValue,
                            modifier = Modifier.size(104.dp)
                        )
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Vigor Vegetativo (MODIS NDVI): ${"%.2f".format(soilProfile.ndviValue)}",
                                style = MaterialTheme.typography.titleSmall,
                                color = VerdeBosqueAndino
                            )
                            Text(
                                text = soilProfile.ndviStatus,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Humedad del suelo (SMAP)", style = MaterialTheme.typography.labelSmall)
                                    Text(
                                        "${soilProfile.soilMoisturePct}%",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = AzulCieloNasa
                                    )
                                }
                                LinearProgressIndicator(
                                    progress = { (soilProfile.soilMoisturePct / 100f).coerceIn(0f, 1f) },
                                    color = AzulCieloNasa,
                                    trackColor = BlancoPuro,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Temp. superficial:", style = MaterialTheme.typography.bodySmall)
                                Text(
                                    "${soilProfile.surfaceTempC} °C",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TerracotaQuinua
                                )
                            }
                        }
                    }
                }
            }
        }

        // Diagnóstico del Suelo
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.2.dp, BordeSutil)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Filled.Terrain, contentDescription = null, tint = TerracotaQuinua)
                            Text(
                                text = "Diagnóstico del Suelo",
                                style = MaterialTheme.typography.titleMedium,
                                color = VerdeBosqueAndino
                            )
                        }
                        UrgencyPill(urgency = soilProfile.erosionRisk)
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(color = VerdeMentaSuave, shape = RoundedCornerShape(8.dp)) {
                            Text(
                                text = "Materia orgánica: ${soilProfile.organicMatterPct}%",
                                style = MaterialTheme.typography.labelSmall,
                                color = VerdeBosqueAndino,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Surface(color = DoradoAltiplano.copy(alpha = 0.25f), shape = RoundedCornerShape(8.dp)) {
                            Text(
                                text = soilProfile.salinityStatus,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Text(
                        text = soilProfile.soilDiagnosisSummary,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Filled.Layers, contentDescription = null, tint = VerdeHojaCultivo)
                Text(
                    text = "Plan de Acción y Recuperación del Terreno",
                    style = MaterialTheme.typography.titleMedium,
                    color = VerdeBosqueAndino
                )
            }
        }

        items(soilProfile.conservationPlan) { practice ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.2.dp, BordeSutil)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = VerdeMentaSuave,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = practice.categoryBadge,
                            style = MaterialTheme.typography.labelSmall,
                            color = VerdeBosqueAndino,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Text(
                        text = practice.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = VerdeBosqueAndino
                    )
                    Text(
                        text = practice.description,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Materiales locales: ${practice.localMaterials}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TerracotaQuinua,
                        fontWeight = FontWeight.SemiBold
                    )
                    practice.implementationSteps.forEachIndexed { idx, step ->
                        Text(
                            text = "${idx + 1}. $step",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NdviGauge(ndvi: Float, modifier: Modifier = Modifier) {
    val sweepColor = when {
        ndvi >= 0.50f -> UrgenciaVerde
        ndvi >= 0.35f -> DoradoAltiplano
        else -> TerracotaQuinua
    }
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = Stroke(width = 11.dp.toPx(), cap = StrokeCap.Round)
            drawArc(
                color = BlancoPuro,
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                style = stroke
            )
            drawArc(
                color = sweepColor,
                startAngle = 135f,
                sweepAngle = 270f * ndvi.coerceIn(0f, 1f),
                useCenter = false,
                style = stroke
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${"%.2f".format(ndvi)}",
                style = MaterialTheme.typography.titleLarge,
                color = VerdeBosqueAndino,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "NDVI",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
