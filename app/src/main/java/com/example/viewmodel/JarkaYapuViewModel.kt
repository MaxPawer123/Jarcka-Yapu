package com.example.viewmodel

import android.app.Application
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.LocalAgronomyDataset
import com.example.data.local.JarkaYapuDatabase
import com.example.data.repository.JarkaYapuRepository
import com.example.model.ConnectivityMode
import com.example.model.CostEntryEntity
import com.example.model.CropType
import com.example.model.DiagnosisResult
import com.example.model.Municipality
import com.example.model.MunicipalityClimate
import com.example.model.NasaSoilProfile
import com.example.model.SavedDiagnosisEntity
import com.example.model.ScanUiState
import com.example.network.GeminiVisionService
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class JarkaYapuViewModel(application: Application) : AndroidViewModel(application) {

    private val database = JarkaYapuDatabase.getInstance(application)
    private val repository = JarkaYapuRepository(
        context = application,
        savedDiagnosisDao = database.savedDiagnosisDao(),
        costEntryDao = database.costEntryDao()
    )
    private val geminiVisionService = GeminiVisionService(application)

    private val _connectivityMode = MutableStateFlow(repository.getInitialConnectivityMode())
    val connectivityMode: StateFlow<ConnectivityMode> = _connectivityMode.asStateFlow()

    private val _selectedMunicipality = MutableStateFlow(repository.getInitialMunicipality())
    val selectedMunicipality: StateFlow<Municipality> = _selectedMunicipality.asStateFlow()

    private val _diagnosisInputTab = MutableStateFlow(0)
    val diagnosisInputTab: StateFlow<Int> = _diagnosisInputTab.asStateFlow()

    private val _selectedCropFilter = MutableStateFlow<CropType?>(null)
    val selectedCropFilter: StateFlow<CropType?> = _selectedCropFilter.asStateFlow()

    private val _selectedSymptomTag = MutableStateFlow<String?>(null)
    val selectedSymptomTag: StateFlow<String?> = _selectedSymptomTag.asStateFlow()

    val filteredLocalDiagnoses: StateFlow<List<DiagnosisResult>> = combine(
        _selectedCropFilter,
        _selectedSymptomTag
    ) { cropFilter, symptomTag ->
        LocalAgronomyDataset.localPestsAndDiseases.filter { item ->
            val matchesCrop = cropFilter == null || item.cropType == cropFilter
            val matchesSymptom = symptomTag == null || item.symptomFilterTags.contains(symptomTag)
            matchesCrop && matchesSymptom
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LocalAgronomyDataset.localPestsAndDiseases
    )

    private val _activeDiagnosis = MutableStateFlow<DiagnosisResult?>(null)
    val activeDiagnosis: StateFlow<DiagnosisResult?> = _activeDiagnosis.asStateFlow()

    private val _selectedPhotoUri = MutableStateFlow<Uri?>(null)
    val selectedPhotoUri: StateFlow<Uri?> = _selectedPhotoUri.asStateFlow()

    private val _farmerNote = MutableStateFlow("")
    val farmerNote: StateFlow<String> = _farmerNote.asStateFlow()

    private val _scanUiState = MutableStateFlow<ScanUiState>(ScanUiState.Idle)
    val scanUiState: StateFlow<ScanUiState> = _scanUiState.asStateFlow()

    val savedDiagnoses: StateFlow<List<SavedDiagnosisEntity>> = repository.savedDiagnoses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val costEntries: StateFlow<List<CostEntryEntity>> = repository.costEntries
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalCostBs: StateFlow<Double> = repository.totalCostBs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    private val _creatorCredit = MutableStateFlow(repository.getCreatorCredit())
    val creatorCredit: StateFlow<String> = _creatorCredit.asStateFlow()

    private val _customLogoPath = MutableStateFlow(repository.getCustomLogoPath())
    val customLogoPath: StateFlow<String?> = _customLogoPath.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    fun toggleConnectivityMode() {
        val next = if (_connectivityMode.value == ConnectivityMode.OFFLINE_CAMPO) {
            ConnectivityMode.ONLINE_NUBE
        } else {
            ConnectivityMode.OFFLINE_CAMPO
        }
        setConnectivityMode(next)
    }

    fun setConnectivityMode(mode: ConnectivityMode) {
        _connectivityMode.value = mode
        repository.saveConnectivityMode(mode)
        if (mode == ConnectivityMode.OFFLINE_CAMPO) {
            _snackbarMessage.value = "Modo Campo Offline activo: diagnóstico y clima sin internet."
        } else {
            _snackbarMessage.value = "Modo Nube Online activo: Escáner Universal IA habilitado."
        }
    }

    fun selectMunicipality(municipality: Municipality) {
        _selectedMunicipality.value = municipality
        repository.saveMunicipality(municipality)
    }

    fun getCurrentClimate(): MunicipalityClimate {
        val muni = _selectedMunicipality.value
        return LocalAgronomyDataset.municipalityClimates[municipalityKeyOrDefault(muni)]!!
    }

    fun getClimateFor(municipality: Municipality): MunicipalityClimate {
        return LocalAgronomyDataset.municipalityClimates[municipality]!!
    }

    fun getSoilProfileFor(municipality: Municipality): NasaSoilProfile {
        return LocalAgronomyDataset.nasaSoilProfiles[municipality]!!
    }

    private fun municipalityKeyOrDefault(municipality: Municipality): Municipality {
        return if (LocalAgronomyDataset.municipalityClimates.containsKey(municipality)) {
            municipality
        } else {
            Municipality.SICA_SICA
        }
    }

    fun setDiagnosisInputTab(tabIndex: Int) {
        _diagnosisInputTab.value = tabIndex
    }

    fun selectCropFilter(cropType: CropType?) {
        _selectedCropFilter.value = if (_selectedCropFilter.value == cropType) null else cropType
    }

    fun selectSymptomTag(tag: String?) {
        _selectedSymptomTag.value = if (_selectedSymptomTag.value == tag) null else tag
    }

    fun clearLocalFilters() {
        _selectedCropFilter.value = null
        _selectedSymptomTag.value = null
    }

    fun openDiagnosis(diagnosisResult: DiagnosisResult) {
        _activeDiagnosis.value = diagnosisResult
    }

    fun openSavedDiagnosis(entity: SavedDiagnosisEntity) {
        _activeDiagnosis.value = repository.mapEntityToDiagnosisResult(entity)
    }

    fun closeDiagnosisResult() {
        _activeDiagnosis.value = null
    }

    fun setSelectedPhoto(uri: Uri?) {
        _selectedPhotoUri.value = uri
        _scanUiState.value = ScanUiState.Idle
    }

    fun updateFarmerNote(note: String) {
        _farmerNote.value = note
    }

    fun analyzeSelectedPhotoWithAi() {
        val photoUri = _selectedPhotoUri.value
        if (photoUri == null) {
            _scanUiState.value = ScanUiState.Error("Primero toma una foto o elige una imagen de tu galería.", canRetry = false)
            return
        }
        if (_connectivityMode.value == ConnectivityMode.OFFLINE_CAMPO) {
            _scanUiState.value = ScanUiState.Error(
                "Estás en Modo Campo Offline. Cambia al Modo Nube Online en la barra superior para usar el Escáner IA.",
                canRetry = false
            )
            return
        }
        if (!hasActiveNetworkConnection()) {
            _scanUiState.value = ScanUiState.Error(
                "Sin señal de datos o Wi-Fi en el teléfono. Puedes usar la pestaña Cultivos Locales (Offline) sin internet.",
                canRetry = true
            )
            return
        }

        viewModelScope.launch {
            _scanUiState.value = ScanUiState.Loading
            try {
                val result = geminiVisionService.analyzeCropPhoto(
                    imageUri = photoUri,
                    municipality = _selectedMunicipality.value,
                    farmerNote = _farmerNote.value
                )
                _scanUiState.value = ScanUiState.Success(result)
                _activeDiagnosis.value = result
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _scanUiState.value = ScanUiState.Error(
                    e.localizedMessage ?: "No se pudo completar el análisis visual IA.",
                    canRetry = true
                )
            }
        }
    }

    fun saveDiagnosisToParcela(result: DiagnosisResult, parcelaName: String) {
        viewModelScope.launch {
            val muni = _selectedMunicipality.value
            val climateAlert = getClimateFor(muni).activeCrossAlertForSpray
            repository.saveDiagnosisToParcela(
                result = result,
                parcelaName = parcelaName,
                municipality = muni,
                crossClimateAlert = climateAlert
            )
            _snackbarMessage.value = "Guardado en Mi Parcela (${muni.displayName})."
        }
    }

    fun deleteSavedDiagnosis(id: Long) {
        viewModelScope.launch {
            repository.deleteSavedDiagnosis(id)
            _snackbarMessage.value = "Registro eliminado del historial de parcela."
        }
    }

    fun addCostEntry(
        parcelaName: String,
        cropName: String,
        diagnosisTitle: String,
        inputName: String,
        inputCostBs: Double,
        laborDays: Double,
        laborDailyRateBs: Double,
        notes: String
    ) {
        viewModelScope.launch {
            repository.addCostEntry(
                parcelaName = parcelaName,
                cropName = cropName,
                diagnosisTitle = diagnosisTitle,
                inputName = inputName,
                inputCostBs = inputCostBs,
                laborDays = laborDays,
                laborDailyRateBs = laborDailyRateBs,
                notes = notes
            )
            val total = inputCostBs + (laborDays * laborDailyRateBs)
            _snackbarMessage.value = "Gasto registrado: Bs ${"%.2f".format(total)} en Cuaderno de Costos."
        }
    }

    fun deleteCostEntry(id: Long) {
        viewModelScope.launch {
            repository.deleteCostEntry(id)
            _snackbarMessage.value = "Gasto eliminado del cuaderno."
        }
    }

    fun updateCreatorCredit(credit: String) {
        val clean = credit.trim().ifBlank { "Jark'a Yapu · Provincia Aroma" }
        _creatorCredit.value = clean
        repository.saveCreatorCredit(clean)
        _snackbarMessage.value = "Identidad y créditos actualizados."
    }

    fun updateCustomLogo(uri: Uri) {
        viewModelScope.launch {
            val savedPath = repository.saveCustomLogoFromUri(uri)
            if (savedPath != null) {
                _customLogoPath.value = savedPath
                _snackbarMessage.value = "Logo personalizado guardado correctamente."
            } else {
                _snackbarMessage.value = "No se pudo cargar la imagen del logo."
            }
        }
    }

    fun resetCustomLogo() {
        repository.clearCustomLogo()
        _customLogoPath.value = null
        _snackbarMessage.value = "Se restauró el emblema oficial de Jark'a Yapu."
    }

    fun consumeSnackbar() {
        _snackbarMessage.value = null
    }

    private fun hasActiveNetworkConnection(): Boolean {
        val cm = getApplication<Application>().getSystemService(ConnectivityManager::class.java)
            ?: return false
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
