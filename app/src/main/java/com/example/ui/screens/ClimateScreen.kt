package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.ConnectivityMode
import com.example.model.Municipality
import com.example.model.MunicipalityClimate
import com.example.ui.components.UrgencyPill
import com.example.ui.theme.AzulCieloNasa
import com.example.ui.theme.AzulCieloSuave
import com.example.ui.theme.BlancoPuro
import com.example.ui.theme.BordeSutil
import com.example.ui.theme.DoradoAltiplano
import com.example.ui.theme.DoradoSuave
import com.example.ui.theme.TerracotaQuinua
import com.example.ui.theme.VerdeBosqueAndino
import com.example.ui.theme.VerdeHojaCultivo
import com.example.ui.theme.VerdeMentaSuave

@Composable
fun ClimateScreen(
    selectedMunicipality: Municipality,
    climate: MunicipalityClimate,
    connectivityMode: ConnectivityMode,
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
                    text = "Alertas Climáticas · Provincia Aroma",
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
                                .heightIn(min = 52.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { onSelectMunicipality(muni) }
                                .testTag("muni_chip_${muni.name}"),
                            color = if (selected) VerdeBosqueAndino else MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.5.dp, if (selected) VerdeBosqueAndino else BordeSutil),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.LocationOn,
                                    contentDescription = null,
                                    tint = if (selected) DoradoAltiplano else VerdeHojaCultivo,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = muni.displayName,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = if (selected) BlancoPuro else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = AzulCieloSuave),
                border = BorderStroke(1.2.dp, AzulCieloNasa.copy(alpha = 0.45f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = climate.municipality.displayName,
                                style = MaterialTheme.typography.headlineSmall,
                                color = AzulCieloNasa
                            )
                            Text(
                                text = "${climate.municipality.altitudeMeters} m s.n.m. · ${climate.municipality.zoneNote}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Surface(
                            color = if (connectivityMode == ConnectivityMode.OFFLINE_CAMPO) DoradoSuave else VerdeMentaSuave,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.CloudOff,
                                    contentDescription = null,
                                    tint = VerdeBosqueAndino,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "Caché Offline",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = VerdeBosqueAndino,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        WeatherStatTile(
                            icon = Icons.Filled.Thermostat,
                            label = "Mín. noche",
                            value = "${climate.minNightTempC} °C",
                            tint = if (climate.minNightTempC <= 0) TerracotaQuinua else AzulCieloNasa,
                            modifier = Modifier.weight(1f)
                        )
                        WeatherStatTile(
                            icon = Icons.Filled.Air,
                            label = "Viento",
                            value = "${climate.windKmh} km/h",
                            tint = VerdeBosqueAndino,
                            modifier = Modifier.weight(1f)
                        )
                        WeatherStatTile(
                            icon = Icons.Filled.WaterDrop,
                            label = "Lluvia",
                            value = "${climate.rainProbabilityPct}%",
                            tint = AzulCieloNasa,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Text(
                        text = climate.cacheTimestamp,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item {
            Text(
                text = "Eventos Críticos (Heladas, Granizo, Sequía y Viento)",
                style = MaterialTheme.typography.titleMedium,
                color = VerdeBosqueAndino
            )
        }

        items(climate.hazardAlerts) { alert ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.2.dp, BordeSutil)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        UrgencyPill(urgency = alert.riskLevel)
                        Text(
                            text = alert.windowLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Filled.AcUnit, contentDescription = null, tint = AzulCieloNasa)
                        Text(
                            text = alert.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = alert.detail,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item {
            Text(
                text = "Acciones Inmediatas por Cultivo",
                style = MaterialTheme.typography.titleMedium,
                color = VerdeBosqueAndino
            )
        }

        items(climate.cropRecommendations) { advice ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = VerdeMentaSuave.copy(alpha = 0.55f)),
                border = BorderStroke(1.dp, VerdeHojaCultivo.copy(alpha = 0.35f))
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = advice.cropName,
                        style = MaterialTheme.typography.titleMedium,
                        color = VerdeBosqueAndino,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = advice.actionNow,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun WeatherStatTile(
    icon: ImageVector,
    label: String,
    value: String,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, BordeSutil)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(icon, contentDescription = label, tint = tint, modifier = Modifier.size(20.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = tint,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
