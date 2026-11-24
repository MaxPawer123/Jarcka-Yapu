package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.CostEntryEntity
import com.example.model.Municipality
import com.example.model.SavedDiagnosisEntity
import com.example.model.UrgencyLevel
import com.example.ui.components.ExpenseEntryDialog
import com.example.ui.components.UrgencyPill
import com.example.ui.theme.BlancoPuro
import com.example.ui.theme.BordeSutil
import com.example.ui.theme.DoradoAltiplano
import com.example.ui.theme.TerracotaQuinua
import com.example.ui.theme.VerdeBosqueAndino
import com.example.ui.theme.VerdeHojaCultivo
import com.example.ui.theme.VerdeMentaSuave
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NotebookScreen(
    selectedMunicipality: Municipality,
    savedDiagnoses: List<SavedDiagnosisEntity>,
    costEntries: List<CostEntryEntity>,
    totalCostBs: Double,
    onOpenSavedDiagnosis: (SavedDiagnosisEntity) -> Unit,
    onDeleteSavedDiagnosis: (Long) -> Unit,
    onAddCostEntry: (
        parcelaName: String,
        cropName: String,
        diagnosisTitle: String,
        inputName: String,
        inputCostBs: Double,
        laborDays: Double,
        laborDailyRateBs: Double,
        notes: String
    ) -> Unit,
    onDeleteCostEntry: (Long) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var showAddExpenseDialog by remember { mutableStateOf(false) }

    val dateFormatter = remember {
        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "BO"))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Resumen financiero y de parcelas
        Surface(
            color = VerdeBosqueAndino,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "Total Invertido en Parcelas",
                        style = MaterialTheme.typography.labelMedium,
                        color = VerdeMentaSuave
                    )
                    Text(
                        text = "Bs ${"%.2f".format(totalCostBs)}",
                        style = MaterialTheme.typography.displaySmall,
                        color = BlancoPuro
                    )
                    Text(
                        text = "${costEntries.size} gastos · ${savedDiagnoses.size} diagnósticos guardados",
                        style = MaterialTheme.typography.bodySmall,
                        color = DoradoAltiplano
                    )
                }

                Button(
                    onClick = { showAddExpenseDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DoradoAltiplano,
                        contentColor = VerdeBosqueAndino
                    ),
                    modifier = Modifier
                        .heightIn(min = 52.dp)
                        .testTag("add_new_expense_button")
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("Gasto", fontWeight = FontWeight.Bold)
                }
            }
        }

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = VerdeBosqueAndino
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                modifier = Modifier
                    .heightIn(min = 52.dp)
                    .testTag("tab_notebook_costs"),
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Filled.ReceiptLong, contentDescription = null, modifier = Modifier.size(18.dp))
                        Text("Costos (Bs.)", style = MaterialTheme.typography.labelLarge)
                    }
                }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                modifier = Modifier
                    .heightIn(min = 52.dp)
                    .testTag("tab_notebook_parcels"),
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Filled.Agriculture, contentDescription = null, modifier = Modifier.size(18.dp))
                        Text("Parcelas (${savedDiagnoses.size})", style = MaterialTheme.typography.labelLarge)
                    }
                }
            )
        }

        if (selectedTab == 0) {
            if (costEntries.isEmpty()) {
                EmptyNotebookState(
                    icon = Icons.Filled.ReceiptLong,
                    title = "Aún no tienes gastos registrados",
                    subtitle = "Presiona '+ Gasto' o registra insumos y jornales desde cualquier diagnóstico.",
                    actionLabel = "Registrar primer gasto",
                    onAction = { showAddExpenseDialog = true }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(costEntries, key = { it.id }) { entry ->
                        CostEntryCard(
                            entry = entry,
                            formattedDate = dateFormatter.format(Date(entry.timestampMillis)),
                            onDelete = { onDeleteCostEntry(entry.id) }
                        )
                    }
                }
            }
        } else {
            if (savedDiagnoses.isEmpty()) {
                EmptyNotebookState(
                    icon = Icons.Filled.Agriculture,
                    title = "Sin diagnósticos guardados todavía",
                    subtitle = "Cuando consultes una plaga o enfermedad, toca 'Guardar en Parcela' para llevar tu historial.",
                    actionLabel = null,
                    onAction = {}
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(savedDiagnoses, key = { it.id }) { saved ->
                        SavedParcelCard(
                            saved = saved,
                            formattedDate = dateFormatter.format(Date(saved.timestampMillis)),
                            onOpen = { onOpenSavedDiagnosis(saved) },
                            onDelete = { onDeleteSavedDiagnosis(saved.id) }
                        )
                    }
                }
            }
        }
    }

    if (showAddExpenseDialog) {
        ExpenseEntryDialog(
            initialParcela = "Parcela ${selectedMunicipality.displayName}",
            initialCrop = "Quinua",
            initialDiagnosisTitle = "Manejo general de parcela",
            onDismiss = { showAddExpenseDialog = false },
            onSave = { parcela, crop, title, inputName, inputCost, days, rate, notes ->
                onAddCostEntry(parcela, crop, title, inputName, inputCost, days, rate, notes)
                showAddExpenseDialog = false
            }
        )
    }
}

@Composable
private fun CostEntryCard(
    entry: CostEntryEntity,
    formattedDate: String,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.2.dp, BordeSutil)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = entry.inputName,
                        style = MaterialTheme.typography.titleMedium,
                        color = VerdeBosqueAndino,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${entry.parcelaName} · ${entry.cropName} · ${entry.diagnosisTitle}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "Bs ${"%.2f".format(entry.totalCostBs)}",
                    style = MaterialTheme.typography.titleLarge,
                    color = TerracotaQuinua,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Insumo: Bs ${"%.2f".format(entry.inputCostBs)} | Jornales: ${entry.laborDays} × Bs ${"%.0f".format(entry.laborDailyRateBs)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.DeleteOutline,
                        contentDescription = "Eliminar gasto",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            if (entry.notes.isNotBlank()) {
                Text(
                    text = "Nota: ${entry.notes}",
                    style = MaterialTheme.typography.bodySmall,
                    color = VerdeHojaCultivo
                )
            }
            Text(
                text = formattedDate,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SavedParcelCard(
    saved: SavedDiagnosisEntity,
    formattedDate: String,
    onOpen: () -> Unit,
    onDelete: () -> Unit
) {
    val urgency = runCatching { UrgencyLevel.valueOf(saved.urgencyName) }.getOrDefault(UrgencyLevel.MEDIO)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpen)
            .testTag("saved_parcel_card_${saved.id}"),
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
                Surface(color = VerdeMentaSuave, shape = RoundedCornerShape(8.dp)) {
                    Text(
                        text = "${saved.parcelaName} (${saved.municipalityName})",
                        style = MaterialTheme.typography.labelSmall,
                        color = VerdeBosqueAndino,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                UrgencyPill(urgency = urgency)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${saved.cropDisplayName}: ${saved.commonName}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Local: ${saved.localName} · $formattedDate",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.DeleteOutline,
                        contentDescription = "Eliminar registro de parcela",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyNotebookState(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    actionLabel: String?,
    onAction: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = VerdeHojaCultivo,
            modifier = Modifier.size(56.dp)
        )
        Spacer(Modifier.size(12.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = VerdeBosqueAndino
        )
        Spacer(Modifier.size(6.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (actionLabel != null) {
            Spacer(Modifier.size(16.dp))
            Button(
                onClick = onAction,
                colors = ButtonDefaults.buttonColors(containerColor = VerdeBosqueAndino),
                modifier = Modifier.heightIn(min = 52.dp)
            ) {
                Text(actionLabel)
            }
        }
    }
}
