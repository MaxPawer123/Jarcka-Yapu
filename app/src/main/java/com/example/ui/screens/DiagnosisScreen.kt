package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FilterAltOff
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.example.R
import com.example.data.LocalAgronomyDataset
import com.example.model.ConnectivityMode
import com.example.model.CropType
import com.example.model.DiagnosisResult
import com.example.model.Municipality
import com.example.model.ScanUiState
import com.example.ui.components.CategoryBadge
import com.example.ui.components.UrgencyPill
import com.example.ui.theme.AzulCieloNasa
import com.example.ui.theme.AzulCieloSuave
import com.example.ui.theme.BlancoPuro
import com.example.ui.theme.BordeSutil
import com.example.ui.theme.DoradoAltiplano
import com.example.ui.theme.DoradoSuave
import com.example.ui.theme.TerracotaQuinua
import com.example.ui.theme.UrgenciaRojo
import com.example.ui.theme.UrgenciaRojoFondo
import com.example.ui.theme.VerdeBosqueAndino
import com.example.ui.theme.VerdeHojaCultivo
import com.example.ui.theme.VerdeMentaSuave

@Composable
fun DiagnosisScreen(
    connectivityMode: ConnectivityMode,
    selectedMunicipality: Municipality,
    selectedInputTab: Int,
    selectedCropFilter: CropType?,
    selectedSymptomTag: String?,
    filteredDiagnoses: List<DiagnosisResult>,
    selectedPhotoUri: Uri?,
    farmerNote: String,
    scanUiState: ScanUiState,
    onSelectInputTab: (Int) -> Unit,
    onSelectCropFilter: (CropType?) -> Unit,
    onSelectSymptomTag: (String?) -> Unit,
    onClearFilters: () -> Unit,
    onOpenDiagnosis: (DiagnosisResult) -> Unit,
    onEnableOnlineMode: () -> Unit,
    onOpenCamera: () -> Unit,
    onPhotoSelected: (Uri?) -> Unit,
    onFarmerNoteChange: (String) -> Unit,
    onAnalyzePhoto: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TabRow(
            selectedTabIndex = selectedInputTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = VerdeBosqueAndino
        ) {
            Tab(
                selected = selectedInputTab == 0,
                onClick = { onSelectInputTab(0) },
                modifier = Modifier
                    .heightIn(min = 52.dp)
                    .testTag("tab_local_offline"),
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Filled.Eco, contentDescription = null, modifier = Modifier.size(18.dp))
                        Text(
                            text = "Cultivos (Offline)",
                            style = MaterialTheme.typography.labelMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            )
            Tab(
                selected = selectedInputTab == 1,
                onClick = { onSelectInputTab(1) },
                modifier = Modifier
                    .heightIn(min = 52.dp)
                    .testTag("tab_ai_online"),
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                        Text(
                            text = "Escáner IA (Online)",
                            style = MaterialTheme.typography.labelMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            )
        }

        if (selectedInputTab == 0) {
            OfflineCropDiagnosisTab(
                selectedMunicipality = selectedMunicipality,
                selectedCropFilter = selectedCropFilter,
                selectedSymptomTag = selectedSymptomTag,
                filteredDiagnoses = filteredDiagnoses,
                onSelectCropFilter = onSelectCropFilter,
                onSelectSymptomTag = onSelectSymptomTag,
                onClearFilters = onClearFilters,
                onOpenDiagnosis = onOpenDiagnosis
            )
        } else {
            OnlineAiScannerTab(
                connectivityMode = connectivityMode,
                selectedMunicipality = selectedMunicipality,
                selectedPhotoUri = selectedPhotoUri,
                farmerNote = farmerNote,
                scanUiState = scanUiState,
                onEnableOnlineMode = onEnableOnlineMode,
                onOpenCamera = onOpenCamera,
                onPhotoSelected = onPhotoSelected,
                onFarmerNoteChange = onFarmerNoteChange,
                onAnalyzePhoto = onAnalyzePhoto,
                onSwitchToOfflineTab = { onSelectInputTab(0) }
            )
        }
    }
}

@Composable
private fun OfflineCropDiagnosisTab(
    selectedMunicipality: Municipality,
    selectedCropFilter: CropType?,
    selectedSymptomTag: String?,
    filteredDiagnoses: List<DiagnosisResult>,
    onSelectCropFilter: (CropType?) -> Unit,
    onSelectSymptomTag: (String?) -> Unit,
    onClearFilters: () -> Unit,
    onOpenDiagnosis: (DiagnosisResult) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            AltiplanoHeroBanner(municipality = selectedMunicipality)
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "1. Elige tu cultivo (sin internet)",
                    style = MaterialTheme.typography.titleMedium,
                    color = VerdeBosqueAndino
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CropFilterButton(
                        label = "Todos (6)",
                        selected = selectedCropFilter == null,
                        accentColor = VerdeBosqueAndino,
                        onClick = { onSelectCropFilter(null) },
                        testTag = "filter_crop_all"
                    )
                    CropFilterButton(
                        label = "Quinua",
                        selected = selectedCropFilter == CropType.QUINUA,
                        accentColor = TerracotaQuinua,
                        onClick = { onSelectCropFilter(CropType.QUINUA) },
                        testTag = "filter_crop_quinua"
                    )
                    CropFilterButton(
                        label = "Papa",
                        selected = selectedCropFilter == CropType.PAPA,
                        accentColor = VerdeHojaCultivo,
                        onClick = { onSelectCropFilter(CropType.PAPA) },
                        testTag = "filter_crop_papa"
                    )
                    CropFilterButton(
                        label = "Zanahoria",
                        selected = selectedCropFilter == CropType.ZANAHORIA,
                        accentColor = DoradoAltiplano,
                        onClick = { onSelectCropFilter(CropType.ZANAHORIA) },
                        testTag = "filter_crop_zanahoria"
                    )
                }
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "2. ¿Qué ves en tu planta?",
                        style = MaterialTheme.typography.titleMedium,
                        color = VerdeBosqueAndino
                    )
                    if (selectedCropFilter != null || selectedSymptomTag != null) {
                        TextButton(
                            onClick = onClearFilters,
                            modifier = Modifier.heightIn(min = 48.dp)
                        ) {
                            Icon(Icons.Filled.FilterAltOff, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Limpiar")
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LocalAgronomyDataset.symptomQuickFilters.forEachIndexed { idx, symptom ->
                        val isSelected = selectedSymptomTag == symptom
                        FilterChip(
                            selected = isSelected,
                            onClick = { onSelectSymptomTag(symptom) },
                            label = { Text(symptom, style = MaterialTheme.typography.labelMedium) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = VerdeMentaSuave,
                                selectedLabelColor = VerdeBosqueAndino
                            ),
                            modifier = Modifier
                                .heightIn(min = 48.dp)
                                .testTag("symptom_chip_$idx")
                        )
                    }
                }
            }
        }

        if (filteredDiagnoses.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, BordeSutil)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "No hay coincidencia exacta con ese filtro.",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Button(
                            onClick = onClearFilters,
                            colors = ButtonDefaults.buttonColors(containerColor = VerdeBosqueAndino),
                            modifier = Modifier.heightIn(min = 52.dp)
                        ) {
                            Text("Mostrar los 6 registros locales")
                        }
                    }
                }
            }
        } else {
            items(filteredDiagnoses, key = { it.id }) { diagnosis ->
                LocalDiagnosisCard(
                    diagnosis = diagnosis,
                    onClick = { onOpenDiagnosis(diagnosis) }
                )
            }
        }
    }
}

@Composable
private fun AltiplanoHeroBanner(municipality: Municipality) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, BordeSutil)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(152.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_altiplano_hero),
                contentDescription = "Cultivos en el Altiplano de la Provincia Aroma",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.15f),
                                VerdeBosqueAndino.copy(alpha = 0.88f)
                            )
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Surface(
                    color = DoradoAltiplano,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "${municipality.displayName} · ${municipality.altitudeMeters} m s.n.m.",
                        style = MaterialTheme.typography.labelSmall,
                        color = VerdeBosqueAndino,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
                Text(
                    text = "Guía de Campo Sin Internet",
                    style = MaterialTheme.typography.headlineSmall,
                    color = BlancoPuro
                )
                Text(
                    text = "Diagnóstico visual rápido para Quinua, Papa y Zanahoria",
                    style = MaterialTheme.typography.bodySmall,
                    color = VerdeMentaSuave
                )
            }
        }
    }
}

@Composable
private fun CropFilterButton(
    label: String,
    selected: Boolean,
    accentColor: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        modifier = Modifier
            .heightIn(min = 52.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .testTag(testTag),
        shape = RoundedCornerShape(14.dp),
        color = if (selected) accentColor else MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.5.dp, if (selected) accentColor else BordeSutil)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (selected) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = BlancoPuro,
                    modifier = Modifier.size(18.dp)
                )
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = if (selected) BlancoPuro else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun LocalDiagnosisCard(
    diagnosis: DiagnosisResult,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("diagnosis_card_${diagnosis.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.2.dp, BordeSutil),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                diagnosis.referenceImageRes?.let { resId ->
                    Image(
                        painter = painterResource(id = resId),
                        contentDescription = "Referencia visual de ${diagnosis.commonName}",
                        modifier = Modifier
                            .size(88.dp)
                            .clip(RoundedCornerShape(14.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = VerdeMentaSuave,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = diagnosis.cropDisplayName,
                                style = MaterialTheme.typography.labelSmall,
                                color = VerdeBosqueAndino,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        CategoryBadge(category = diagnosis.category)
                    }
                    Text(
                        text = diagnosis.commonName,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Local: ${diagnosis.localName}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TerracotaQuinua,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = diagnosis.scientificName,
                        style = MaterialTheme.typography.bodySmall,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                text = "• ${diagnosis.keySymptoms.firstOrNull().orEmpty()}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                UrgencyPill(urgency = diagnosis.urgency)
                Button(
                    onClick = onClick,
                    colors = ButtonDefaults.buttonColors(containerColor = VerdeBosqueAndino),
                    modifier = Modifier.heightIn(min = 52.dp)
                ) {
                    Text("Ver guía")
                    Spacer(Modifier.width(6.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
private fun OnlineAiScannerTab(
    connectivityMode: ConnectivityMode,
    selectedMunicipality: Municipality,
    selectedPhotoUri: Uri?,
    farmerNote: String,
    scanUiState: ScanUiState,
    onEnableOnlineMode: () -> Unit,
    onOpenCamera: () -> Unit,
    onPhotoSelected: (Uri?) -> Unit,
    onFarmerNoteChange: (String) -> Unit,
    onAnalyzePhoto: () -> Unit,
    onSwitchToOfflineTab: () -> Unit
) {
    val context = LocalContext.current
    var permissionMessage by remember { mutableStateOf<String?>(null) }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            onOpenCamera()
        } else {
            permissionMessage = "Se necesita permiso de cámara para abrir CameraX y fotografiar el cultivo. También puedes usar Galería."
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            onPhotoSelected(uri)
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = AzulCieloSuave),
                border = BorderStroke(1.2.dp, AzulCieloNasa.copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AutoAwesome,
                            contentDescription = null,
                            tint = AzulCieloNasa
                        )
                        Text(
                            text = "Escáner Universal IA · Cualquier Cultivo",
                            style = MaterialTheme.typography.titleLarge,
                            color = AzulCieloNasa
                        )
                    }
                    Text(
                        text = "Toma una foto nítida de hojas, panojas, raíces o frutos de haba, cebada, alfalfa, cebolla, papa o quinua. La IA responderá en los 5 bloques oficiales adaptados a ${selectedMunicipality.displayName}.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        if (connectivityMode == ConnectivityMode.OFFLINE_CAMPO) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DoradoSuave),
                    border = BorderStroke(1.5.dp, DoradoAltiplano)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Filled.CloudOff, contentDescription = null, tint = Color(0xFF7A4D00))
                            Text(
                                text = "Modo Campo Offline activo",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color(0xFF7A4D00)
                            )
                        }
                        Text(
                            text = "Para analizar fotos nuevas con visión artificial en la nube, activa el Modo Nube Online.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Button(
                            onClick = onEnableOnlineMode,
                            colors = ButtonDefaults.buttonColors(containerColor = AzulCieloNasa),
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 52.dp)
                                .testTag("enable_online_mode_button")
                        ) {
                            Icon(Icons.Filled.Wifi, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Cambiar a Modo Nube Online")
                        }
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        permissionMessage = null
                        val hasCam = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED
                        if (hasCam) {
                            onOpenCamera()
                        } else {
                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = VerdeBosqueAndino),
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 56.dp)
                        .testTag("take_photo_button")
                ) {
                    Icon(Icons.Filled.CameraAlt, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Tomar Foto")
                }

                OutlinedButton(
                    onClick = {
                        permissionMessage = null
                        galleryLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    border = BorderStroke(1.5.dp, VerdeHojaCultivo),
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 56.dp)
                        .testTag("pick_gallery_button")
                ) {
                    Icon(Icons.Filled.PhotoLibrary, contentDescription = null, tint = VerdeBosqueAndino)
                    Spacer(Modifier.width(8.dp))
                    Text("Galería", color = VerdeBosqueAndino)
                }
            }
        }

        if (!permissionMessage.isNullOrBlank()) {
            item {
                Surface(
                    color = UrgenciaRojoFondo,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, UrgenciaRojo)
                ) {
                    Text(
                        text = permissionMessage.orEmpty(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = UrgenciaRojo,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.2.dp, BordeSutil)
            ) {
                if (selectedPhotoUri != null) {
                    AsyncImage(
                        model = selectedPhotoUri,
                        contentDescription = "Fotografía del cultivo capturada para diagnóstico",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(230.dp)
                            .clip(RoundedCornerShape(18.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CameraAlt,
                            contentDescription = null,
                            tint = VerdeHojaCultivo,
                            modifier = Modifier.size(44.dp)
                        )
                        Text(
                            text = "Aún no seleccionaste una fotografía",
                            style = MaterialTheme.typography.titleMedium,
                            color = VerdeBosqueAndino
                        )
                        Text(
                            text = "Consejo de campo: acércate a 20 cm de la hoja o raíz con buena luz del sol.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        item {
            OutlinedTextField(
                value = farmerNote,
                onValueChange = onFarmerNoteChange,
                label = { Text("Nota de campo (opcional)") },
                placeholder = { Text("Ej. Haba con manchas chocolate tras la lluvia") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("farmer_note_input"),
                maxLines = 3
            )
        }

        item {
            when (scanUiState) {
                is ScanUiState.Loading -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = VerdeMentaSuave),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            CircularProgressIndicator(color = VerdeBosqueAndino)
                            Column {
                                Text(
                                    text = "Analizando imagen con Gemini IA...",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = VerdeBosqueAndino
                                )
                                Text(
                                    text = "Generando diagnóstico en los 5 bloques agronómicos.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = VerdeHojaCultivo
                                )
                            }
                        }
                    }
                }
                is ScanUiState.Error -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = UrgenciaRojoFondo),
                        border = BorderStroke(1.2.dp, UrgenciaRojo),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Filled.ErrorOutline, contentDescription = null, tint = UrgenciaRojo)
                                Text(
                                    text = "No se pudo completar el análisis",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = UrgenciaRojo
                                )
                            }
                            Text(
                                text = scanUiState.message,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                if (scanUiState.canRetry) {
                                    Button(
                                        onClick = onAnalyzePhoto,
                                        colors = ButtonDefaults.buttonColors(containerColor = UrgenciaRojo),
                                        modifier = Modifier.heightIn(min = 48.dp)
                                    ) {
                                        Icon(Icons.Filled.Refresh, contentDescription = null)
                                        Spacer(Modifier.width(6.dp))
                                        Text("Reintentar")
                                    }
                                }
                                OutlinedButton(
                                    onClick = onSwitchToOfflineTab,
                                    modifier = Modifier.heightIn(min = 48.dp)
                                ) {
                                    Text("Ir a Guía Offline")
                                }
                            }
                        }
                    }
                }
                else -> {
                    Button(
                        onClick = onAnalyzePhoto,
                        enabled = selectedPhotoUri != null,
                        colors = ButtonDefaults.buttonColors(containerColor = AzulCieloNasa),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 56.dp)
                            .testTag("analyze_ai_button")
                    ) {
                        Icon(Icons.Filled.AutoAwesome, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Analizar Cultivo con IA")
                    }
                }
            }
        }
    }
}
