package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.BookmarkAdded
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.data.LocalAgronomyDataset
import com.example.model.Diagnosis
import com.example.model.DiagnosisResult
import com.example.model.Municipality
import com.example.model.MunicipalityClimate
import com.example.model.UrgencyLevel
import com.example.ui.components.CategoryBadge
import com.example.ui.components.ExpenseEntryDialog
import com.example.ui.components.UrgencyPill
import com.example.ui.components.shareDiagnosisViaWhatsApp
import com.example.ui.theme.AzulCieloNasa
import com.example.ui.theme.AzulCieloSuave
import com.example.ui.theme.BordeSutil
import com.example.ui.theme.DoradoAltiplano
import com.example.ui.theme.DoradoSuave
import com.example.ui.theme.TerracotaQuinua
import com.example.ui.theme.UrgenciaAmbar
import com.example.ui.theme.UrgenciaAmbarFondo
import com.example.ui.theme.UrgenciaRojo
import com.example.ui.theme.UrgenciaRojoFondo
import com.example.ui.theme.UrgenciaVerde
import com.example.ui.theme.UrgenciaVerdeFondo
import com.example.ui.theme.VerdeBosqueAndino
import com.example.ui.theme.VerdeHojaCultivo
import com.example.ui.theme.VerdeMentaSuave

/**
 * Convierte un [DiagnosisResult] a la entidad [Diagnosis] de `JarkaYapuModels.kt`
 * para renderizado uniforme y persistencia offline en Room.
 */
fun DiagnosisResult.toDiagnosisModel(
    municipality: Municipality,
    parcelaName: String = "Parcela ${municipality.displayName}",
    climateCrossAlert: String? = null
): Diagnosis {
    val combinedCrossAlert = when {
        !climateCrossAlert.isNullOrBlank() && isFromCloudAi ->
            "$defaultCrossClimateAlert | $climateCrossAlert"
        !climateCrossAlert.isNullOrBlank() ->
            "$climateCrossAlert $defaultCrossClimateAlert"
        else -> defaultCrossClimateAlert
    }

    return Diagnosis(
        parcelaName = parcelaName,
        municipality = municipality,
        cropId = cropType.name.lowercase(),
        cropName = cropDisplayName,
        pestId = id,
        commonName = commonName,
        localName = localName,
        scientificName = scientificName,
        category = category,
        urgency = urgency,
        observedSymptoms = keySymptoms,
        recommendedOrganic = organicTreatment.name,
        recommendedChemical = chemicalTreatment.activeIngredient,
        crossClimateAlert = combinedCrossAlert,
        harvestImpactAlert = harvestImpactAlert,
        organicIngredients = organicTreatment.ingredients,
        organicSteps = organicTreatment.preparationSteps,
        organicApplication = organicTreatment.applicationGuide,
        chemicalDosage = chemicalTreatment.dosagePer20L,
        chemicalFrequency = chemicalTreatment.frequency,
        preHarvestDays = chemicalTreatment.preHarvestDays,
        chemicalSafety = chemicalTreatment.safetyNote,
        culturalPrevention = culturalPrevention,
        referenceResKey = LocalAgronomyDataset.getReferenceResKey(cropType),
        photoUri = capturedPhotoUri,
        isFromCloudAi = isFromCloudAi
    )
}

/**
 * Sobrecarga de compatibilidad que adapta [DiagnosisResult] hacia el modelo Room [Diagnosis].
 */
@Composable
fun DiagnosisResultScreen(
    diagnosis: DiagnosisResult,
    municipality: Municipality,
    climate: MunicipalityClimate,
    onBack: () -> Unit,
    onSaveToParcela: (parcelaName: String) -> Unit,
    onSaveExpense: (
        parcelaName: String,
        cropName: String,
        diagnosisTitle: String,
        inputName: String,
        inputCostBs: Double,
        laborDays: Double,
        laborDailyRateBs: Double,
        notes: String
    ) -> Unit
) {
    val model = remember(diagnosis, municipality, climate) {
        diagnosis.toDiagnosisModel(
            municipality = municipality,
            parcelaName = "Parcela ${municipality.displayName}",
            climateCrossAlert = climate.activeCrossAlertForSpray
        )
    }

    DiagnosisResultScreen(
        diagnosis = model,
        climate = climate,
        onBack = onBack,
        onSaveToParcela = onSaveToParcela,
        onSaveExpense = onSaveExpense
    )
}

/**
 * Pantalla de Resultados del Diagnóstico en Jetpack Compose que consume directamente
 * el modelo [Diagnosis] de `JarkaYapuModels.kt` mostrando dinámicamente los 5 bloques obligatorios:
 * 1) Identificación y Confirmación Visual
 * 2) Nivel de Urgencia y Riesgo
 * 3) Tratamiento Paso a Paso (Orgánico vs. Químico)
 * 4) Prevención Cultural
 * 5) Conexión Directa con el Ecosistema Jark'a Yapu
 */
@Composable
fun DiagnosisResultScreen(
    diagnosis: Diagnosis,
    climate: MunicipalityClimate? = null,
    onBack: () -> Unit,
    onSaveToParcela: (parcelaName: String) -> Unit,
    onSaveExpense: (
        parcelaName: String,
        cropName: String,
        diagnosisTitle: String,
        inputName: String,
        inputCostBs: Double,
        laborDays: Double,
        laborDailyRateBs: Double,
        notes: String
    ) -> Unit
) {
    BackHandler(onBack = onBack)

    val context = LocalContext.current
    var treatmentTab by remember { mutableIntStateOf(0) }
    var showExpenseDialog by remember { mutableStateOf(false) }
    var showParcelaDialog by remember { mutableStateOf(false) }
    var parcelaInput by remember(diagnosis) {
        mutableStateOf(diagnosis.parcelaName.ifBlank { "Parcela ${diagnosis.municipality.displayName}" })
    }

    val referenceDrawableRes = remember(diagnosis.referenceResKey, diagnosis.cropName) {
        LocalAgronomyDataset.findReferenceResByKey(diagnosis.referenceResKey)
            ?: when {
                diagnosis.cropName.contains("Quinua", ignoreCase = true) ->
                    LocalAgronomyDataset.findReferenceResByKey("img_quinua_ref")
                diagnosis.cropName.contains("Papa", ignoreCase = true) ->
                    LocalAgronomyDataset.findReferenceResByKey("img_papa_ref")
                diagnosis.cropName.contains("Zanahoria", ignoreCase = true) ->
                    LocalAgronomyDataset.findReferenceResByKey("img_zanahoria_ref")
                else -> null
            }
    }

    val crossAlert = remember(diagnosis, climate) {
        diagnosis.crossClimateAlert.ifBlank {
            climate?.activeCrossAlertForSpray
                ?: "Verifica el pronóstico de heladas y viento de ${diagnosis.municipality.displayName} antes de aplicar."
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .heightIn(min = 48.dp)
                    .testTag("back_from_diagnosis_button")
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                Spacer(Modifier.width(6.dp))
                Text("Volver al selector")
            }
        }

        // BLOQUE 1 - Identificación y Confirmación Visual
        item {
            BlockCard(blockNumber = "BLOQUE 1 · IDENTIFICACIÓN Y CONFIRMACIÓN VISUAL") {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(color = VerdeMentaSuave, shape = RoundedCornerShape(8.dp)) {
                        Text(
                            text = diagnosis.cropName,
                            style = MaterialTheme.typography.labelMedium,
                            color = VerdeBosqueAndino,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                    CategoryBadge(category = diagnosis.category)
                }

                Text(
                    text = diagnosis.commonName,
                    style = MaterialTheme.typography.headlineMedium,
                    color = VerdeBosqueAndino
                )
                Text(
                    text = "Nombre local: ${diagnosis.localName}",
                    style = MaterialTheme.typography.titleMedium,
                    color = TerracotaQuinua
                )
                Text(
                    text = diagnosis.scientificName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                when {
                    !diagnosis.photoUri.isNullOrBlank() -> {
                        AsyncImage(
                            model = diagnosis.photoUri,
                            contentDescription = "Foto analizada de ${diagnosis.commonName}",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(14.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                    referenceDrawableRes != null -> {
                        Image(
                            painter = painterResource(id = referenceDrawableRes),
                            contentDescription = "Ilustración comparativa de ${diagnosis.commonName}",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(14.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                Text(
                    text = "Síntomas clave para confirmar en parcela:",
                    style = MaterialTheme.typography.titleSmall,
                    color = VerdeBosqueAndino
                )
                diagnosis.observedSymptoms.take(3).forEach { symptom ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "•",
                            style = MaterialTheme.typography.titleMedium,
                            color = TerracotaQuinua,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = symptom,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // BLOQUE 2 - Nivel de Urgencia y Riesgo
        item {
            val (bg, borderCol) = when (diagnosis.urgency) {
                UrgencyLevel.BAJO -> UrgenciaVerdeFondo to UrgenciaVerde
                UrgencyLevel.MEDIO -> UrgenciaAmbarFondo to UrgenciaAmbar
                UrgencyLevel.ALTO -> UrgenciaRojoFondo to UrgenciaRojo
            }
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = bg),
                border = BorderStroke(1.5.dp, borderCol)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "BLOQUE 2 · NIVEL DE URGENCIA Y RIESGO",
                        style = MaterialTheme.typography.labelSmall,
                        color = borderCol,
                        fontWeight = FontWeight.Bold
                    )
                    UrgencyPill(urgency = diagnosis.urgency)
                    Text(
                        text = diagnosis.urgency.actionHeadline,
                        style = MaterialTheme.typography.titleMedium,
                        color = borderCol,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Impacto en la cosecha: ${diagnosis.harvestImpactAlert}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // BLOQUE 3 - Tratamiento Paso a Paso (Pestañas Orgánico vs Químico)
        item {
            BlockCard(blockNumber = "BLOQUE 3 · TRATAMIENTO PASO A PASO") {
                TabRow(
                    selectedTabIndex = treatmentTab,
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = VerdeBosqueAndino,
                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                ) {
                    Tab(
                        selected = treatmentTab == 0,
                        onClick = { treatmentTab = 0 },
                        modifier = Modifier
                            .heightIn(min = 50.dp)
                            .testTag("tab_organic_treatment"),
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Filled.Eco, contentDescription = null, modifier = Modifier.size(16.dp))
                                Text("Orgánico / Bio", style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    )
                    Tab(
                        selected = treatmentTab == 1,
                        onClick = { treatmentTab = 1 },
                        modifier = Modifier
                            .heightIn(min = 50.dp)
                            .testTag("tab_chemical_treatment"),
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Filled.Science, contentDescription = null, modifier = Modifier.size(16.dp))
                                Text("Químico (Severo)", style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    )
                }

                if (treatmentTab == 0) {
                    Text(
                        text = diagnosis.recommendedOrganic,
                        style = MaterialTheme.typography.titleMedium,
                        color = VerdeHojaCultivo,
                        fontWeight = FontWeight.Bold
                    )
                    if (diagnosis.organicIngredients.isNotEmpty()) {
                        Text(
                            text = "Ingredientes (mochila 20 L):",
                            style = MaterialTheme.typography.titleSmall
                        )
                        diagnosis.organicIngredients.forEach { ing ->
                            Text("• $ing", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    if (diagnosis.organicSteps.isNotEmpty()) {
                        Text(
                            text = "Preparación paso a paso:",
                            style = MaterialTheme.typography.titleSmall
                        )
                        diagnosis.organicSteps.forEachIndexed { idx, step ->
                            Text("${idx + 1}. $step", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    if (diagnosis.organicApplication.isNotBlank()) {
                        Surface(
                            color = VerdeMentaSuave,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Aplicación: ${diagnosis.organicApplication}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = VerdeBosqueAndino,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                } else {
                    Surface(
                        color = DoradoSuave,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, DoradoAltiplano),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Principio activo: ${diagnosis.recommendedChemical}",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Dosis exacta (20 L): ${diagnosis.chemicalDosage}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = TerracotaQuinua
                            )
                            Text(
                                text = "Frecuencia: ${diagnosis.chemicalFrequency}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "Tiempo de carencia: ${diagnosis.preHarvestDays} días antes de cosechar",
                                style = MaterialTheme.typography.titleSmall,
                                color = UrgenciaRojo
                            )
                        }
                    }
                    Text(
                        text = "Seguridad: ${diagnosis.chemicalSafety}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // BLOQUE 4 - Prevención Cultural
        item {
            BlockCard(blockNumber = "BLOQUE 4 · PREVENCIÓN CULTURAL (SIN COSTO)") {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Filled.Spa, contentDescription = null, tint = VerdeHojaCultivo)
                    Text(
                        text = "Prácticas de campo recomendadas",
                        style = MaterialTheme.typography.titleMedium,
                        color = VerdeBosqueAndino
                    )
                }
                val preventionList = diagnosis.culturalPrevention.ifEmpty {
                    listOf("Rotar cultivos andinos, realizar aporque oportuno y eliminar rastrojos infectados.")
                }
                preventionList.forEach { practice ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text("✓", color = UrgenciaVerde, fontWeight = FontWeight.Bold)
                        Text(practice, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        // BLOQUE 5 - Conexión Directa con el Ecosistema Jark'a Yapu
        item {
            BlockCard(blockNumber = "BLOQUE 5 · CONEXIÓN ECOSISTEMA JARK'A YAPU") {
                Surface(
                    color = AzulCieloSuave,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, AzulCieloNasa.copy(alpha = 0.45f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(Icons.Filled.WbSunny, contentDescription = null, tint = AzulCieloNasa)
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "Alerta Climática Cruzada (${diagnosis.municipality.displayName})",
                                style = MaterialTheme.typography.titleSmall,
                                color = AzulCieloNasa
                            )
                            Text(
                                text = crossAlert,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                Button(
                    onClick = { showExpenseDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = TerracotaQuinua),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 54.dp)
                        .testTag("register_treatment_expense_button")
                ) {
                    Icon(Icons.Filled.AttachMoney, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Registrar gasto de tratamiento")
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { showParcelaDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = VerdeBosqueAndino),
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 54.dp)
                            .testTag("save_to_parcela_button")
                    ) {
                        Icon(Icons.Filled.BookmarkAdded, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("Guardar en Parcela")
                    }

                    OutlinedButton(
                        onClick = {
                            shareDiagnosisViaWhatsApp(
                                context = context,
                                diagnosis = diagnosis,
                                crossClimateAlert = crossAlert
                            )
                        },
                        border = BorderStroke(1.5.dp, UrgenciaVerde),
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 54.dp)
                            .testTag("share_whatsapp_button")
                    ) {
                        Icon(Icons.Filled.Share, contentDescription = null, tint = UrgenciaVerde)
                        Spacer(Modifier.width(6.dp))
                        Text("WhatsApp", color = UrgenciaVerde)
                    }
                }
            }
        }
    }

    if (showExpenseDialog) {
        ExpenseEntryDialog(
            initialParcela = parcelaInput,
            initialCrop = diagnosis.cropName,
            initialDiagnosisTitle = diagnosis.commonName,
            onDismiss = { showExpenseDialog = false },
            onSave = { parcela, crop, title, inputName, inputCost, days, rate, notes ->
                onSaveExpense(parcela, crop, title, inputName, inputCost, days, rate, notes)
                showExpenseDialog = false
            }
        )
    }

    if (showParcelaDialog) {
        AlertDialog(
            onDismissRequest = { showParcelaDialog = false },
            title = { Text("Guardar diagnóstico en Parcela", color = VerdeBosqueAndino) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Este diagnóstico quedará disponible sin internet en el Cuaderno de Parcelas.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    OutlinedTextField(
                        value = parcelaInput,
                        onValueChange = { parcelaInput = it },
                        label = { Text("Nombre o sector de la parcela") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSaveToParcela(parcelaInput)
                        showParcelaDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VerdeBosqueAndino),
                    modifier = Modifier.heightIn(min = 48.dp)
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showParcelaDialog = false },
                    modifier = Modifier.heightIn(min = 48.dp)
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun BlockCard(
    blockNumber: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.2.dp, BordeSutil),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = blockNumber,
                style = MaterialTheme.typography.labelSmall,
                color = VerdeHojaCultivo,
                fontWeight = FontWeight.Bold
            )
            content()
        }
    }
}
