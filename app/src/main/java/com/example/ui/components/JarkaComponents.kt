package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.R
import com.example.model.ConnectivityMode
import com.example.model.DiagnosisResult
import com.example.model.ProblemCategory
import com.example.model.UrgencyLevel
import com.example.ui.theme.AzulCieloNasa
import com.example.ui.theme.AzulCieloSuave
import com.example.ui.theme.BordeSutil
import com.example.ui.theme.DoradoAltiplano
import com.example.ui.theme.DoradoSuave
import com.example.ui.theme.TerracotaQuinua
import com.example.ui.theme.TerracotaSuave
import com.example.ui.theme.UrgenciaAmbar
import com.example.ui.theme.UrgenciaAmbarFondo
import com.example.ui.theme.UrgenciaRojo
import com.example.ui.theme.UrgenciaRojoFondo
import com.example.ui.theme.UrgenciaVerde
import com.example.ui.theme.UrgenciaVerdeFondo
import com.example.ui.theme.VerdeBosqueAndino
import com.example.ui.theme.VerdeHojaCultivo
import com.example.ui.theme.VerdeMentaSuave
import java.io.File

@Composable
fun ConnectivityModeSwitchPill(
    mode: ConnectivityMode,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isOffline = mode == ConnectivityMode.OFFLINE_CAMPO
    val bgColor = if (isOffline) DoradoSuave else AzulCieloSuave
    val borderColor = if (isOffline) DoradoAltiplano else AzulCieloNasa
    val contentColor = if (isOffline) Color(0xFF7A4D00) else AzulCieloNasa

    Surface(
        modifier = modifier
            .heightIn(min = 48.dp)
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onToggle)
            .testTag("connectivity_mode_switch"),
        shape = RoundedCornerShape(24.dp),
        color = bgColor,
        border = BorderStroke(1.5.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = if (isOffline) Icons.Filled.CloudOff else Icons.Filled.CloudDone,
                contentDescription = mode.label,
                tint = contentColor,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = mode.shortLabel,
                style = MaterialTheme.typography.labelMedium,
                color = contentColor,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun BrandAvatarBadge(
    customLogoPath: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(VerdeMentaSuave)
            .border(1.5.dp, DoradoAltiplano, CircleShape)
            .clickable(onClick = onClick)
            .testTag("brand_logo_button"),
        contentAlignment = Alignment.Center
    ) {
        if (!customLogoPath.isNullOrBlank() && File(customLogoPath).exists()) {
            AsyncImage(
                model = File(customLogoPath),
                contentDescription = "Logo personalizado de Jark'a Yapu",
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.jarka_launcher_icon_fg),
                contentDescription = "Emblema oficial Jark'a Yapu",
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun UrgencyPill(urgency: UrgencyLevel, modifier: Modifier = Modifier) {
    val (bg, fg, icon) = when (urgency) {
        UrgencyLevel.BAJO -> Triple(UrgenciaVerdeFondo, UrgenciaVerde, Icons.Filled.VerifiedUser)
        UrgencyLevel.MEDIO -> Triple(UrgenciaAmbarFondo, UrgenciaAmbar, Icons.Filled.WarningAmber)
        UrgencyLevel.ALTO -> Triple(UrgenciaRojoFondo, UrgenciaRojo, Icons.Filled.ErrorOutline)
    }
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = bg,
        border = BorderStroke(1.dp, fg.copy(alpha = 0.45f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = urgency.label,
                tint = fg,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = urgency.label,
                style = MaterialTheme.typography.labelMedium,
                color = fg,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun CategoryBadge(category: ProblemCategory, modifier: Modifier = Modifier) {
    val (bg, fg) = when (category) {
        ProblemCategory.INSECTO_PLAGA -> TerracotaSuave to TerracotaQuinua
        ProblemCategory.HONGO_ENFERMEDAD -> AzulCieloSuave to AzulCieloNasa
        ProblemCategory.DEFICIENCIA_NUTRICIONAL -> DoradoSuave to Color(0xFF8A5A00)
    }
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = bg,
        border = BorderStroke(1.dp, fg.copy(alpha = 0.35f))
    ) {
        Text(
            text = category.displayName,
            style = MaterialTheme.typography.labelMedium,
            color = fg,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun ExpenseEntryDialog(
    initialParcela: String,
    initialCrop: String,
    initialDiagnosisTitle: String,
    onDismiss: () -> Unit,
    onSave: (
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
    var parcelaName by remember { mutableStateOf(initialParcela) }
    var cropName by remember { mutableStateOf(initialCrop) }
    var inputName by remember { mutableStateOf("") }
    var inputCostText by remember { mutableStateOf("") }
    var laborDaysText by remember { mutableStateOf("1") }
    var laborRateText by remember { mutableStateOf("100") }
    var notes by remember { mutableStateOf("") }

    val inputCost = inputCostText.toDoubleOrNull() ?: 0.0
    val laborDays = laborDaysText.toDoubleOrNull() ?: 0.0
    val laborRate = laborRateText.toDoubleOrNull() ?: 0.0
    val calculatedTotal = inputCost + (laborDays * laborRate)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Registrar gasto de tratamiento",
                style = MaterialTheme.typography.titleLarge,
                color = VerdeBosqueAndino
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Tratamiento: $initialDiagnosisTitle",
                    style = MaterialTheme.typography.bodyMedium,
                    color = VerdeHojaCultivo,
                    fontWeight = FontWeight.SemiBold
                )
                OutlinedTextField(
                    value = parcelaName,
                    onValueChange = { parcelaName = it },
                    label = { Text("Nombre de la parcela") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = cropName,
                    onValueChange = { cropName = it },
                    label = { Text("Cultivo (Quinua, Papa, etc.)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = inputName,
                    onValueChange = { inputName = it },
                    label = { Text("Insumo o bioinsumo usado") },
                    placeholder = { Text("Ej. Caldo sulfocálcico / Spinosad") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = inputCostText,
                        onValueChange = { inputCostText = it },
                        label = { Text("Insumo (Bs)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = laborDaysText,
                        onValueChange = { laborDaysText = it },
                        label = { Text("Jornales") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.weight(0.8f)
                    )
                    OutlinedTextField(
                        value = laborRateText,
                        onValueChange = { laborRateText = it },
                        label = { Text("Bs/jornal") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.weight(0.9f)
                    )
                }
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Observación opcional") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Surface(
                    color = VerdeMentaSuave,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Total calculado: Bs ${"%.2f".format(calculatedTotal)}",
                        style = MaterialTheme.typography.titleMedium,
                        color = VerdeBosqueAndino,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        parcelaName,
                        cropName,
                        initialDiagnosisTitle,
                        inputName,
                        inputCost,
                        laborDays,
                        laborRate,
                        notes
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = VerdeBosqueAndino),
                modifier = Modifier
                    .heightIn(min = 48.dp)
                    .testTag("confirm_save_expense_button")
            ) {
                Text("Guardar gasto")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.heightIn(min = 48.dp)
            ) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun BrandCustomizationDialog(
    currentCredit: String,
    customLogoPath: String?,
    onUpdateCredit: (String) -> Unit,
    onPickCustomLogo: (Uri) -> Unit,
    onResetLogo: () -> Unit,
    onDismiss: () -> Unit
) {
    var creditInput by remember(currentCredit) { mutableStateOf(currentCredit) }

    val logoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            onPickCustomLogo(uri)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Identidad y Créditos",
                style = MaterialTheme.typography.titleLarge,
                color = VerdeBosqueAndino
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Personaliza el logo de tu comunidad, asociación agrícola o institución en la cabecera de Jark'a Yapu.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(VerdeMentaSuave)
                            .border(2.dp, VerdeHojaCultivo, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!customLogoPath.isNullOrBlank() && File(customLogoPath).exists()) {
                            AsyncImage(
                                model = File(customLogoPath),
                                contentDescription = "Logo actual",
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.jarka_launcher_icon_fg),
                                contentDescription = "Logo oficial Jark'a Yapu",
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedButton(
                            onClick = {
                                logoPicker.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            modifier = Modifier.heightIn(min = 48.dp)
                        ) {
                            Icon(Icons.Filled.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Cargar logo")
                        }
                        if (customLogoPath != null) {
                            TextButton(
                                onClick = onResetLogo,
                                modifier = Modifier.heightIn(min = 48.dp)
                            ) {
                                Icon(Icons.Filled.Restore, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Usar emblema oficial")
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = creditInput,
                    onValueChange = { creditInput = it },
                    label = { Text("Crédito / Institución / Creador") },
                    leadingIcon = { Icon(Icons.Filled.Edit, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Surface(
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, BordeSutil)
                ) {
                    Text(
                        text = "Diseñado para agricultores de Sica Sica, Patacamaya, Ayo Ayo y Umala (Provincia Aroma, La Paz).",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onUpdateCredit(creditInput)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = VerdeBosqueAndino),
                modifier = Modifier.heightIn(min = 48.dp)
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.heightIn(min = 48.dp)
            ) {
                Text("Cerrar")
            }
        }
    )
}

fun shareDiagnosisViaWhatsApp(
    context: Context,
    diagnosis: com.example.model.Diagnosis,
    crossClimateAlert: String = diagnosis.crossClimateAlert
) {
    val organicSummary = diagnosis.organicApplication.ifBlank { diagnosis.recommendedOrganic }
    val shareText = buildString {
        appendLine("*JARK'A YAPU - Diagnóstico de Parcela (${diagnosis.municipality.displayName})*")
        appendLine("Parcela: ${diagnosis.parcelaName}")
        appendLine("Cultivo: ${diagnosis.cropName}")
        appendLine("Problema: ${diagnosis.commonName} (${diagnosis.localName})")
        appendLine("Científico: ${diagnosis.scientificName}")
        appendLine("Urgencia: ${diagnosis.urgency.label}")
        appendLine()
        appendLine("Control Orgánico: ${diagnosis.recommendedOrganic}")
        if (organicSummary != diagnosis.recommendedOrganic) {
            appendLine("Aplicación: $organicSummary")
        }
        appendLine()
        appendLine("Control Químico (severo): ${diagnosis.recommendedChemical}")
        appendLine("Dosis: ${diagnosis.chemicalDosage} (Carencia: ${diagnosis.preHarvestDays} días)")
        appendLine()
        appendLine("Clima Aroma: $crossClimateAlert")
    }

    val whatsappIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        setPackage("com.whatsapp")
        putExtra(Intent.EXTRA_TEXT, shareText)
    }

    val chooserFallback = Intent.createChooser(
        Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        },
        context.getString(R.string.share_chooser_title)
    )

    try {
        context.startActivity(whatsappIntent)
    } catch (_: Exception) {
        context.startActivity(chooserFallback)
    }
}

fun shareDiagnosisViaWhatsApp(
    context: Context,
    diagnosis: DiagnosisResult,
    municipalityName: String,
    crossClimateAlert: String
) {
    val shareText = buildString {
        appendLine("*JARK'A YAPU - Diagnóstico de Parcela ($municipalityName)*")
        appendLine("Cultivo: ${diagnosis.cropDisplayName}")
        appendLine("Problema: ${diagnosis.commonName} (${diagnosis.localName})")
        appendLine("Científico: ${diagnosis.scientificName}")
        appendLine("Urgencia: ${diagnosis.urgency.label}")
        appendLine()
        appendLine("Control Orgánico: ${diagnosis.organicTreatment.name}")
        appendLine("Aplicación: ${diagnosis.organicTreatment.applicationGuide}")
        appendLine()
        appendLine("Control Químico (severo): ${diagnosis.chemicalTreatment.activeIngredient}")
        appendLine("Dosis: ${diagnosis.chemicalTreatment.dosagePer20L} (Carencia: ${diagnosis.chemicalTreatment.preHarvestDays} días)")
        appendLine()
        appendLine("Clima Aroma: $crossClimateAlert")
    }

    val whatsappIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        setPackage("com.whatsapp")
        putExtra(Intent.EXTRA_TEXT, shareText)
    }

    val chooserFallback = Intent.createChooser(
        Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        },
        context.getString(R.string.share_chooser_title)
    )

    try {
        context.startActivity(whatsappIntent)
    } catch (_: Exception) {
        context.startActivity(chooserFallback)
    }
}
