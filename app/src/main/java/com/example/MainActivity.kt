package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.SatelliteAlt
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.SatelliteAlt
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.app.configureAppCheck
import com.example.model.AppModule
import com.example.model.ConnectivityMode
import com.example.ui.WeatherAlertsScreen
import com.example.ui.components.BrandAvatarBadge
import com.example.ui.components.BrandCustomizationDialog
import com.example.ui.components.ConnectivityModeSwitchPill
import com.example.ui.screens.ClimateScreen
import com.example.ui.screens.DiagnosisResultScreen
import com.example.ui.screens.DiagnosisScreen
import com.example.ui.screens.NotebookScreen
import com.example.ui.screens.SoilConservationScreen
import com.example.ui.theme.BlancoPuro
import com.example.ui.theme.DoradoAltiplano
import com.example.ui.theme.JarkaYapuTheme
import com.example.ui.theme.VerdeBosqueAndino
import com.example.ui.theme.VerdeHojaCultivo
import com.example.ui.theme.VerdeMentaSuave
import com.example.viewmodel.JarkaYapuViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // STEP 1: Configurar Firebase App Check antes de setContent o ViewModels
        configureAppCheck(this, intent)

        enableEdgeToEdge()
        setContent {
            JarkaYapuTheme(darkTheme = false) {
                JarkaYapuApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JarkaYapuApp(
    viewModel: JarkaYapuViewModel = viewModel()
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: AppModule.DIAGNOSTICO.route

    val connectivityMode by viewModel.connectivityMode.collectAsStateWithLifecycle()
    val selectedMunicipality by viewModel.selectedMunicipality.collectAsStateWithLifecycle()
    val diagnosisInputTab by viewModel.diagnosisInputTab.collectAsStateWithLifecycle()
    val selectedCropFilter by viewModel.selectedCropFilter.collectAsStateWithLifecycle()
    val selectedSymptomTag by viewModel.selectedSymptomTag.collectAsStateWithLifecycle()
    val filteredDiagnoses by viewModel.filteredLocalDiagnoses.collectAsStateWithLifecycle()
    val activeDiagnosis by viewModel.activeDiagnosis.collectAsStateWithLifecycle()
    val selectedPhotoUri by viewModel.selectedPhotoUri.collectAsStateWithLifecycle()
    val farmerNote by viewModel.farmerNote.collectAsStateWithLifecycle()
    val scanUiState by viewModel.scanUiState.collectAsStateWithLifecycle()
    val savedDiagnoses by viewModel.savedDiagnoses.collectAsStateWithLifecycle()
    val costEntries by viewModel.costEntries.collectAsStateWithLifecycle()
    val totalCostBs by viewModel.totalCostBs.collectAsStateWithLifecycle()
    val creatorCredit by viewModel.creatorCredit.collectAsStateWithLifecycle()
    val customLogoPath by viewModel.customLogoPath.collectAsStateWithLifecycle()
    val snackbarMessage by viewModel.snackbarMessage.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    var showBrandDialog by remember { mutableStateOf(false) }

    LaunchedEffect(snackbarMessage) {
        val msg = snackbarMessage
        if (!msg.isNullOrBlank()) {
            snackbarHostState.showSnackbar(msg)
            viewModel.consumeSnackbar()
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val useNavigationRail = maxWidth >= 680.dp

        Scaffold(
            contentWindowInsets = WindowInsets.safeDrawing,
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = VerdeBosqueAndino,
                        titleContentColor = BlancoPuro
                    ),
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.clickable { showBrandDialog = true }
                        ) {
                            BrandAvatarBadge(
                                customLogoPath = customLogoPath,
                                onClick = { showBrandDialog = true }
                            )
                            Column(modifier = Modifier.weight(1f, fill = false)) {
                                Text(
                                    text = "Jark'a Yapu",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = BlancoPuro,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = creatorCredit,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = VerdeMentaSuave,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    },
                    actions = {
                        ConnectivityModeSwitchPill(
                            mode = connectivityMode,
                            onToggle = { viewModel.toggleConnectivityMode() },
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                )
            },
            bottomBar = {
                if (!useNavigationRail && activeDiagnosis == null) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.navigationBarsPadding()
                    ) {
                        AppModule.entries.forEach { module ->
                            val selected = currentRoute == module.route
                            NavigationBarItem(
                                selected = selected,
                                onClick = {
                                    navController.navigate(module.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = moduleIcon(module, selected),
                                        contentDescription = module.title
                                    )
                                },
                                label = {
                                    Text(
                                        text = module.title,
                                        style = MaterialTheme.typography.labelSmall,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = VerdeBosqueAndino,
                                    selectedTextColor = VerdeBosqueAndino,
                                    indicatorColor = VerdeMentaSuave,
                                    unselectedIconColor = VerdeHojaCultivo,
                                    unselectedTextColor = VerdeHojaCultivo
                                ),
                                modifier = Modifier.testTag("nav_${module.route}")
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (useNavigationRail && activeDiagnosis == null) {
                    NavigationRail(
                        containerColor = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        AppModule.entries.forEach { module ->
                            val selected = currentRoute == module.route
                            NavigationRailItem(
                                selected = selected,
                                onClick = {
                                    navController.navigate(module.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = moduleIcon(module, selected),
                                        contentDescription = module.title
                                    )
                                },
                                label = { Text(module.title) },
                                colors = NavigationRailItemDefaults.colors(
                                    selectedIconColor = VerdeBosqueAndino,
                                    selectedTextColor = VerdeBosqueAndino,
                                    indicatorColor = VerdeMentaSuave
                                )
                            )
                        }
                    }
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    val diagnosisDetail = activeDiagnosis
                    if (diagnosisDetail != null) {
                        DiagnosisResultScreen(
                            diagnosis = diagnosisDetail,
                            municipality = selectedMunicipality,
                            climate = viewModel.getClimateFor(selectedMunicipality),
                            onBack = { viewModel.closeDiagnosisResult() },
                            onSaveToParcela = { parcelaName ->
                                viewModel.saveDiagnosisToParcela(diagnosisDetail, parcelaName)
                            },
                            onSaveExpense = { parcela, crop, title, inputName, inputCost, days, rate, notes ->
                                viewModel.addCostEntry(
                                    parcelaName = parcela,
                                    cropName = crop,
                                    diagnosisTitle = title,
                                    inputName = inputName,
                                    inputCostBs = inputCost,
                                    laborDays = days,
                                    laborDailyRateBs = rate,
                                    notes = notes
                                )
                            }
                        )
                    } else {
                        NavHost(
                            navController = navController,
                            startDestination = AppModule.DIAGNOSTICO.route
                        ) {
                            composable(AppModule.DIAGNOSTICO.route) {
                                DiagnosisScreen(
                                    connectivityMode = connectivityMode,
                                    selectedMunicipality = selectedMunicipality,
                                    selectedInputTab = diagnosisInputTab,
                                    selectedCropFilter = selectedCropFilter,
                                    selectedSymptomTag = selectedSymptomTag,
                                    filteredDiagnoses = filteredDiagnoses,
                                    selectedPhotoUri = selectedPhotoUri,
                                    farmerNote = farmerNote,
                                    scanUiState = scanUiState,
                                    onSelectInputTab = { viewModel.setDiagnosisInputTab(it) },
                                    onSelectCropFilter = { viewModel.selectCropFilter(it) },
                                    onSelectSymptomTag = { viewModel.selectSymptomTag(it) },
                                    onClearFilters = { viewModel.clearLocalFilters() },
                                    onOpenDiagnosis = { viewModel.openDiagnosis(it) },
                                    onEnableOnlineMode = {
                                        viewModel.setConnectivityMode(ConnectivityMode.ONLINE_NUBE)
                                    },
                                    onPhotoSelected = { viewModel.setSelectedPhoto(it) },
                                    onFarmerNoteChange = { viewModel.updateFarmerNote(it) },
                                    onAnalyzePhoto = { viewModel.analyzeSelectedPhotoWithAi() }
                                )
                            }

                            composable(AppModule.CLIMA.route) {
                                WeatherAlertsScreen(
                                    selectedMunicipality = selectedMunicipality,
                                    connectivityMode = connectivityMode,
                                    onSelectMunicipality = { viewModel.selectMunicipality(it) }
                                )
                            }

                            composable(AppModule.SUELOS.route) {
                                SoilConservationScreen(
                                    selectedMunicipality = selectedMunicipality,
                                    soilProfile = viewModel.getSoilProfileFor(selectedMunicipality),
                                    onSelectMunicipality = { viewModel.selectMunicipality(it) }
                                )
                            }

                            composable(AppModule.CUADERNO.route) {
                                NotebookScreen(
                                    selectedMunicipality = selectedMunicipality,
                                    savedDiagnoses = savedDiagnoses,
                                    costEntries = costEntries,
                                    totalCostBs = totalCostBs,
                                    onOpenSavedDiagnosis = { viewModel.openSavedDiagnosis(it) },
                                    onDeleteSavedDiagnosis = { viewModel.deleteSavedDiagnosis(it) },
                                    onAddCostEntry = { parcela, crop, title, inputName, inputCost, days, rate, notes ->
                                        viewModel.addCostEntry(
                                            parcelaName = parcela,
                                            cropName = crop,
                                            diagnosisTitle = title,
                                            inputName = inputName,
                                            inputCostBs = inputCost,
                                            laborDays = days,
                                            laborDailyRateBs = rate,
                                            notes = notes
                                        )
                                    },
                                    onDeleteCostEntry = { viewModel.deleteCostEntry(it) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showBrandDialog) {
        BrandCustomizationDialog(
            currentCredit = creatorCredit,
            customLogoPath = customLogoPath,
            onUpdateCredit = { viewModel.updateCreatorCredit(it) },
            onPickCustomLogo = { viewModel.updateCustomLogo(it) },
            onResetLogo = { viewModel.resetCustomLogo() },
            onDismiss = { showBrandDialog = false }
        )
    }
}

private fun moduleIcon(module: AppModule, selected: Boolean) = when (module) {
    AppModule.DIAGNOSTICO -> if (selected) Icons.Filled.BugReport else Icons.Outlined.BugReport
    AppModule.CLIMA -> if (selected) Icons.Filled.WbSunny else Icons.Outlined.WbSunny
    AppModule.SUELOS -> if (selected) Icons.Filled.SatelliteAlt else Icons.Outlined.SatelliteAlt
    AppModule.CUADERNO -> if (selected) Icons.Filled.MenuBook else Icons.Outlined.MenuBook
}
