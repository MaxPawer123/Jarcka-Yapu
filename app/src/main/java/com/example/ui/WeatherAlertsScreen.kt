package com.example.ui

import android.content.Context
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
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.LocalAgronomyDataset
import com.example.model.ConnectivityMode
import com.example.model.Municipality
import com.example.model.UrgencyLevel
import com.example.model.WeatherAlert
import com.example.ui.components.UrgencyPill
import com.example.ui.theme.AzulCieloNasa
import com.example.ui.theme.AzulCieloSuave
import com.example.ui.theme.BlancoPuro
import com.example.ui.theme.BordeSutil
import com.example.ui.theme.DoradoAltiplano
import com.example.ui.theme.DoradoSuave
import com.example.ui.theme.TerracotaQuinua
import com.example.ui.theme.UrgenciaAmbar
import com.example.ui.theme.UrgenciaRojo
import com.example.ui.theme.UrgenciaVerde
import com.example.ui.theme.VerdeBosqueAndino
import com.example.ui.theme.VerdeHojaCultivo
import com.example.ui.theme.VerdeMentaSuave
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val WEATHER_CACHE_PREFS = "jarka_weather_offline_cache"
private const val KEY_LAST_SYNC_PREFIX = "last_sync_"

/**
 * Simulador de caché offline para alertas climáticas de la Provincia Aroma
 * utilizando el modelo Room [WeatherAlert] de `JarkaYapuModels.kt`.
 */
object OfflineWeatherMockCache {

    val cachedAlertsByMunicipality: Map<Municipality, List<WeatherAlert>> = mapOf(
        Municipality.SICA_SICA to listOf(
            WeatherAlert(
                id = "sica_helada_01",
                municipality = Municipality.SICA_SICA,
                title = "Helada nocturna moderada (-2 °C)",
                hazardType = "Helada",
                riskLevel = UrgencyLevel.ALTO,
                windowLabel = "Madrugada 02:00 – 06:00",
                minTempC = -2,
                windKmh = 24,
                rainProbabilityPct = 35,
                detail = "Cielo despejado provocará descenso térmico brusco en pampas abiertas de Sica Sica.",
                cropRecommendation = "Quinua y Papa: activar humo controlado con paja húmeda o bosta a las 03:30 AM en bordes expuestos."
            ),
            WeatherAlert(
                id = "sica_granizo_02",
                municipality = Municipality.SICA_SICA,
                title = "Granizada aislada en serranía",
                hazardType = "Granizo",
                riskLevel = UrgencyLevel.MEDIO,
                windowLabel = "Próximas 48 horas",
                minTempC = -2,
                windKmh = 24,
                rainProbabilityPct = 35,
                detail = "Formación convectiva sobre zonas altas; probabilidad de granizo menudo en la tarde.",
                cropRecommendation = "Papa y Zanahoria: limpiar surcos de drenaje para evitar encharcamientos tras el granizo."
            ),
            WeatherAlert(
                id = "sica_sequia_03",
                municipality = Municipality.SICA_SICA,
                title = "Viento seco de tarde (24 km/h)",
                hazardType = "Sequía",
                riskLevel = UrgencyLevel.MEDIO,
                windowLabel = "13:00 – 17:30",
                minTempC = -2,
                windKmh = 24,
                rainProbabilityPct = 35,
                detail = "Pérdida rápida de humedad superficial; evitar fumigaciones foliares al mediodía.",
                cropRecommendation = "Zanahoria: regar temprano o al atardecer manteniendo humedad uniforme en cama."
            )
        ),
        Municipality.PATACAMAYA to listOf(
            WeatherAlert(
                id = "pata_granizo_01",
                municipality = Municipality.PATACAMAYA,
                title = "Tormenta con riesgo de granizo y lluvia (65%)",
                hazardType = "Granizo",
                riskLevel = UrgencyLevel.ALTO,
                windowLabel = "Hoy 16:00 – 21:00",
                minTempC = 1,
                windKmh = 18,
                rainProbabilityPct = 65,
                detail = "Nubosidad densa ingresando desde el noreste con alta humedad (76%) que dispara Rancha y Mildiu.",
                cropRecommendation = "Papa: reforzar aporque y aplicar Caldo Bordelés preventivo antes de la lluvia."
            ),
            WeatherAlert(
                id = "pata_helada_02",
                municipality = Municipality.PATACAMAYA,
                title = "Helada leve al amanecer (+1 °C)",
                hazardType = "Helada",
                riskLevel = UrgencyLevel.BAJO,
                windowLabel = "Madrugada 04:00 – 06:00",
                minTempC = 1,
                windKmh = 18,
                rainProbabilityPct = 65,
                detail = "La cobertura nubosa protege parcialmente de heladas blancas severas en el valle.",
                cropRecommendation = "Quinua: revisar envés de hojas bajeras por Mildiu después del rocío matinal."
            ),
            WeatherAlert(
                id = "pata_sequia_03",
                municipality = Municipality.PATACAMAYA,
                title = "Balance hídrico favorable (sin sequía)",
                hazardType = "Sequía",
                riskLevel = UrgencyLevel.BAJO,
                windowLabel = "Próximos 5 días",
                minTempC = 1,
                windKmh = 18,
                rainProbabilityPct = 65,
                detail = "Humedad suficiente en perfil superficial; suspender riegos hoy para evitar pudrición.",
                cropRecommendation = "Zanahoria: suspender riego hoy y vigilar manchas de Alternaria."
            )
        ),
        Municipality.AYO_AYO to listOf(
            WeatherAlert(
                id = "ayo_sequia_01",
                municipality = Municipality.AYO_AYO,
                title = "Déficit hídrico / veranillo seco (20% lluvia)",
                hazardType = "Sequía",
                riskLevel = UrgencyLevel.ALTO,
                windowLabel = "Próximos 5 días",
                minTempC = 0,
                windKmh = 31,
                rainProbabilityPct = 20,
                detail = "Suelos franco-arenosos secos con vientos de 31 km/h que favorecen ataque de polilla Kcona kcona.",
                cropRecommendation = "Papa y Zanahoria: programar riego de auxilio en horas frescas y aporcar para conservar humedad."
            ),
            WeatherAlert(
                id = "ayo_helada_02",
                municipality = Municipality.AYO_AYO,
                title = "Helada ligera en bordes bajos (0 °C)",
                hazardType = "Helada",
                riskLevel = UrgencyLevel.MEDIO,
                windowLabel = "04:00 – 06:00",
                minTempC = 0,
                windKmh = 31,
                rainProbabilityPct = 20,
                detail = "Microcuencas cercanas al río pueden registrar escarcha ligera al amanecer.",
                cropRecommendation = "Quinua: aporcar ligeramente la base para resistir viento y frío nocturno."
            ),
            WeatherAlert(
                id = "ayo_granizo_03",
                municipality = Municipality.AYO_AYO,
                title = "Riesgo bajo de granizo",
                hazardType = "Granizo",
                riskLevel = UrgencyLevel.BAJO,
                windowLabel = "Próximas 48 horas",
                minTempC = 0,
                windKmh = 31,
                rainProbabilityPct = 20,
                detail = "Atmósfera seca con baja formación de nubes de tormenta.",
                cropRecommendation = "Aprovechar el amanecer sin viento (06:00–08:30) para aplicar bioinsumos."
            )
        ),
        Municipality.UMALA to listOf(
            WeatherAlert(
                id = "umala_helada_01",
                municipality = Municipality.UMALA,
                title = "Helada negra severa (-4 °C)",
                hazardType = "Helada",
                riskLevel = UrgencyLevel.ALTO,
                windowLabel = "01:00 – 06:30",
                minTempC = -4,
                windKmh = 20,
                rainProbabilityPct = 25,
                detail = "Descenso térmico crítico en cabeceras de Umala; alto riesgo de daño celular en brotes.",
                cropRecommendation = "Quinua y Papa: encender fogatas con bosta seca y paja húmeda desde las 02:30 AM y cubrir brotes tiernos."
            ),
            WeatherAlert(
                id = "umala_sequia_02",
                municipality = Municipality.UMALA,
                title = "Sequedad superficial y viento frío (20 km/h)",
                hazardType = "Sequía",
                riskLevel = UrgencyLevel.MEDIO,
                windowLabel = "15:00 – 19:00",
                minTempC = -4,
                windKmh = 20,
                rainProbabilityPct = 25,
                detail = "Aumenta la pérdida térmica en parcelas sin barreras vivas de thola o ichu.",
                cropRecommendation = "Zanahoria: aplicar riego por surco antes de las 17:00 para liberar calor latente en la noche."
            ),
            WeatherAlert(
                id = "umala_granizo_03",
                municipality = Municipality.UMALA,
                title = "Probabilidad baja de granizo (25%)",
                hazardType = "Granizo",
                riskLevel = UrgencyLevel.BAJO,
                windowLabel = "Próximas 24 horas",
                minTempC = -4,
                windKmh = 20,
                rainProbabilityPct = 25,
                detail = "Cielo mayormente despejado reduce el riesgo de granizada intensa.",
                cropRecommendation = "No aplicar químicos foliares hoy; priorizar protección térmica."
            )
        )
    )

    fun readLastSyncLabel(context: Context, municipality: Municipality): String {
        val prefs = context.getSharedPreferences(WEATHER_CACHE_PREFS, Context.MODE_PRIVATE)
        return prefs.getString("${KEY_LAST_SYNC_PREFIX}${municipality.name}", null)
            ?: "Caché local guardado · 06:00 AM"
    }

    fun simulateOfflineCacheRefresh(context: Context, municipality: Municipality): String {
        val formatter = SimpleDateFormat("dd/MM HH:mm", Locale("es", "BO"))
        val label = "Sincronizado en caché local (${formatter.format(Date())})"
        val prefs = context.getSharedPreferences(WEATHER_CACHE_PREFS, Context.MODE_PRIVATE)
        prefs.edit().putString("${KEY_LAST_SYNC_PREFIX}${municipality.name}", label).apply()
        return label
    }
}

@Composable
fun WeatherAlertsScreen(
    selectedMunicipality: Municipality = Municipality.SICA_SICA,
    connectivityMode: ConnectivityMode = ConnectivityMode.OFFLINE_CAMPO,
    onSelectMunicipality: (Municipality) -> Unit = {}
) {
    val context = LocalContext.current
    var activeMunicipality by remember(selectedMunicipality) {
        mutableStateOf(selectedMunicipality)
    }
    var syncStatusLabel by remember(activeMunicipality) {
        mutableStateOf(OfflineWeatherMockCache.readLastSyncLabel(context, activeMunicipality))
    }

    val alerts = remember(activeMunicipality) {
        OfflineWeatherMockCache.cachedAlertsByMunicipality[activeMunicipality].orEmpty()
    }
    val climateSummary = remember(activeMunicipality) {
        LocalAgronomyDataset.municipalityClimates[activeMunicipality]!!
    }

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
                    text = "Alertas de Climatización · Provincia Aroma",
                    style = MaterialTheme.typography.headlineSmall,
                    color = VerdeBosqueAndino
                )
                Text(
                    text = "Selecciona tu municipio para consultar alertas de heladas, granizo y sequía sin conexión.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Municipality.entries.forEach { municipality ->
                        val isSelected = municipality == activeMunicipality
                        Surface(
                            modifier = Modifier
                                .heightIn(min = 52.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable {
                                    activeMunicipality = municipality
                                    syncStatusLabel = OfflineWeatherMockCache.readLastSyncLabel(
                                        context,
                                        municipality
                                    )
                                    onSelectMunicipality(municipality)
                                }
                                .testTag("muni_chip_${municipality.name}"),
                            color = if (isSelected) VerdeBosqueAndino else MaterialTheme.colorScheme.surface,
                            border = BorderStroke(
                                1.5.dp,
                                if (isSelected) VerdeBosqueAndino else BordeSutil
                            ),
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
                                    tint = if (isSelected) DoradoAltiplano else VerdeHojaCultivo,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = municipality.displayName,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = if (isSelected) BlancoPuro else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }

        // Tarjeta resumen del municipio con estado de caché offline simulado
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
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = activeMunicipality.displayName,
                                style = MaterialTheme.typography.headlineSmall,
                                color = AzulCieloNasa
                            )
                            Text(
                                text = "${activeMunicipality.altitudeMeters} m s.n.m. · ${activeMunicipality.zoneNote}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Surface(
                            color = if (connectivityMode == ConnectivityMode.OFFLINE_CAMPO) {
                                DoradoSuave
                            } else {
                                VerdeMentaSuave
                            },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = if (connectivityMode == ConnectivityMode.OFFLINE_CAMPO) {
                                        Icons.Filled.CloudOff
                                    } else {
                                        Icons.Filled.CloudDone
                                    },
                                    contentDescription = null,
                                    tint = VerdeBosqueAndino,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = if (connectivityMode == ConnectivityMode.OFFLINE_CAMPO) {
                                        "Caché Offline"
                                    } else {
                                        "Nube Activa"
                                    },
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
                        ClimateMetricBox(
                            icon = Icons.Filled.Thermostat,
                            label = "Mín. noche",
                            value = "${climateSummary.minNightTempC} °C",
                            tint = if (climateSummary.minNightTempC <= 0) TerracotaQuinua else AzulCieloNasa,
                            modifier = Modifier.weight(1f)
                        )
                        ClimateMetricBox(
                            icon = Icons.Filled.Air,
                            label = "Viento",
                            value = "${climateSummary.windKmh} km/h",
                            tint = VerdeBosqueAndino,
                            modifier = Modifier.weight(1f)
                        )
                        ClimateMetricBox(
                            icon = Icons.Filled.WaterDrop,
                            label = "Lluvia",
                            value = "${climateSummary.rainProbabilityPct}%",
                            tint = AzulCieloNasa,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = syncStatusLabel,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedButton(
                            onClick = {
                                syncStatusLabel = OfflineWeatherMockCache.simulateOfflineCacheRefresh(
                                    context,
                                    activeMunicipality
                                )
                            },
                            modifier = Modifier
                                .heightIn(min = 48.dp)
                                .testTag("refresh_weather_cache_button")
                        ) {
                            Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Guardar caché")
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "Alertas de Heladas, Granizo y Sequía",
                style = MaterialTheme.typography.titleMedium,
                color = VerdeBosqueAndino
            )
        }

        items(alerts, key = { it.id }) { alert ->
            WeatherAlertCard(alert = alert)
        }

        item {
            Text(
                text = "Recomendaciones Inmediatas por Cultivo",
                style = MaterialTheme.typography.titleMedium,
                color = VerdeBosqueAndino
            )
        }

        items(climateSummary.cropRecommendations) { advice ->
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
private fun WeatherAlertCard(alert: WeatherAlert) {
    val hazardIcon = when {
        alert.hazardType.contains("Helada", ignoreCase = true) -> Icons.Filled.AcUnit
        alert.hazardType.contains("Granizo", ignoreCase = true) -> Icons.Filled.Grain
        else -> Icons.Filled.WbSunny
    }
    val hazardTint = when (alert.riskLevel) {
        UrgencyLevel.ALTO -> UrgenciaRojo
        UrgencyLevel.MEDIO -> UrgenciaAmbar
        UrgencyLevel.BAJO -> UrgenciaVerde
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("weather_alert_card_${alert.id}"),
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
                Surface(
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, BordeSutil)
                ) {
                    Text(
                        text = "${alert.hazardType} · ${alert.windowLabel}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = hazardIcon,
                    contentDescription = alert.hazardType,
                    tint = hazardTint
                )
                Text(
                    text = alert.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = alert.detail,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Surface(
                color = VerdeMentaSuave.copy(alpha = 0.65f),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Acción en parcela: ${alert.cropRecommendation}",
                    style = MaterialTheme.typography.bodySmall,
                    color = VerdeBosqueAndino,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }
    }
}

@Composable
private fun ClimateMetricBox(
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
