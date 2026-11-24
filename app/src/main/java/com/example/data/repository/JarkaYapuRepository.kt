package com.example.data.repository

import android.content.Context
import android.net.Uri
import com.example.data.LocalAgronomyDataset
import com.example.data.local.CostEntryDao
import com.example.data.local.SavedDiagnosisDao
import com.example.model.ChemicalTreatment
import com.example.model.ConnectivityMode
import com.example.model.CostEntryEntity
import com.example.model.CropType
import com.example.model.DiagnosisResult
import com.example.model.Municipality
import com.example.model.OrganicTreatment
import com.example.model.ProblemCategory
import com.example.model.SavedDiagnosisEntity
import com.example.model.UrgencyLevel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

private const val PREFS_NAME = "jarka_yapu_prefs"
private const val KEY_CONNECTIVITY_MODE = "connectivity_mode"
private const val KEY_MUNICIPALITY = "selected_municipality"
private const val KEY_CREATOR_CREDIT = "creator_credit"
private const val KEY_CUSTOM_LOGO_PATH = "custom_logo_path"
private const val SEP = "||"

class JarkaYapuRepository(
    private val context: Context,
    private val savedDiagnosisDao: SavedDiagnosisDao,
    private val costEntryDao: CostEntryDao
) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    val savedDiagnoses: Flow<List<SavedDiagnosisEntity>> = savedDiagnosisDao.observeAll()
    val costEntries: Flow<List<CostEntryEntity>> = costEntryDao.observeAll()
    val totalCostBs: Flow<Double> = costEntryDao.observeTotalCostBs()

    fun getInitialConnectivityMode(): ConnectivityMode {
        val raw = prefs.getString(KEY_CONNECTIVITY_MODE, ConnectivityMode.OFFLINE_CAMPO.name)
        return runCatching { ConnectivityMode.valueOf(raw ?: ConnectivityMode.OFFLINE_CAMPO.name) }
            .getOrDefault(ConnectivityMode.OFFLINE_CAMPO)
    }

    fun saveConnectivityMode(mode: ConnectivityMode) {
        prefs.edit().putString(KEY_CONNECTIVITY_MODE, mode.name).apply()
    }

    fun getInitialMunicipality(): Municipality {
        val raw = prefs.getString(KEY_MUNICIPALITY, Municipality.SICA_SICA.name)
        return runCatching { Municipality.valueOf(raw ?: Municipality.SICA_SICA.name) }
            .getOrDefault(Municipality.SICA_SICA)
    }

    fun saveMunicipality(municipality: Municipality) {
        prefs.edit().putString(KEY_MUNICIPALITY, municipality.name).apply()
    }

    fun getCreatorCredit(): String {
        return prefs.getString(KEY_CREATOR_CREDIT, "Jark'a Yapu · Provincia Aroma")
            ?.ifBlank { "Jark'a Yapu · Provincia Aroma" }
            ?: "Jark'a Yapu · Provincia Aroma"
    }

    fun saveCreatorCredit(credit: String) {
        prefs.edit().putString(KEY_CREATOR_CREDIT, credit.trim()).apply()
    }

    fun getCustomLogoPath(): String? {
        val path = prefs.getString(KEY_CUSTOM_LOGO_PATH, null) ?: return null
        return if (File(path).exists()) path else null
    }

    suspend fun saveCustomLogoFromUri(uri: Uri): String? = withContext(Dispatchers.IO) {
        runCatching {
            val logosDir = File(context.filesDir, "logos").apply { mkdirs() }
            val destFile = File(logosDir, "custom_brand_logo.jpg")
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(destFile).use { output ->
                    input.copyTo(output)
                }
            }
            if (destFile.exists() && destFile.length() > 0L) {
                prefs.edit().putString(KEY_CUSTOM_LOGO_PATH, destFile.absolutePath).apply()
                destFile.absolutePath
            } else {
                null
            }
        }.getOrNull()
    }

    fun clearCustomLogo() {
        val current = prefs.getString(KEY_CUSTOM_LOGO_PATH, null)
        if (!current.isNullOrBlank()) {
            runCatching { File(current).delete() }
        }
        prefs.edit().remove(KEY_CUSTOM_LOGO_PATH).apply()
    }

    suspend fun saveDiagnosisToParcela(
        result: DiagnosisResult,
        parcelaName: String,
        municipality: Municipality,
        crossClimateAlert: String
    ): Long {
        val entity = SavedDiagnosisEntity(
            diagnosisCode = result.id,
            parcelaName = parcelaName.ifBlank { "Parcela Principal" },
            municipalityName = municipality.displayName,
            cropDisplayName = result.cropDisplayName,
            commonName = result.commonName,
            localName = result.localName,
            scientificName = result.scientificName,
            categoryName = result.category.name,
            urgencyName = result.urgency.name,
            harvestImpactAlert = result.harvestImpactAlert,
            symptomsJoined = result.keySymptoms.joinToString(SEP),
            organicName = result.organicTreatment.name,
            organicIngredientsJoined = result.organicTreatment.ingredients.joinToString(SEP),
            organicStepsJoined = result.organicTreatment.preparationSteps.joinToString(SEP),
            organicApplication = result.organicTreatment.applicationGuide,
            chemicalActive = result.chemicalTreatment.activeIngredient,
            chemicalDosage = result.chemicalTreatment.dosagePer20L,
            chemicalFrequency = result.chemicalTreatment.frequency,
            chemicalCarenciaDays = result.chemicalTreatment.preHarvestDays,
            chemicalSafety = result.chemicalTreatment.safetyNote,
            culturalJoined = result.culturalPrevention.joinToString(SEP),
            crossClimateAlert = crossClimateAlert,
            photoUri = result.capturedPhotoUri,
            referenceResKey = LocalAgronomyDataset.getReferenceResKey(result.cropType),
            isFromCloudAi = result.isFromCloudAi
        )
        return savedDiagnosisDao.insert(entity)
    }

    suspend fun deleteSavedDiagnosis(id: Long) = savedDiagnosisDao.deleteById(id)

    suspend fun addCostEntry(
        parcelaName: String,
        cropName: String,
        diagnosisTitle: String,
        inputName: String,
        inputCostBs: Double,
        laborDays: Double,
        laborDailyRateBs: Double,
        notes: String
    ): Long {
        val total = inputCostBs + (laborDays * laborDailyRateBs)
        val entry = CostEntryEntity(
            parcelaName = parcelaName.ifBlank { "Parcela Principal" },
            cropName = cropName.ifBlank { "Cultivo General" },
            diagnosisTitle = diagnosisTitle,
            inputName = inputName.ifBlank { "Insumo de tratamiento" },
            inputCostBs = inputCostBs,
            laborDays = laborDays,
            laborDailyRateBs = laborDailyRateBs,
            totalCostBs = total,
            notes = notes.trim()
        )
        return costEntryDao.insert(entry)
    }

    suspend fun deleteCostEntry(id: Long) = costEntryDao.deleteById(id)

    fun mapEntityToDiagnosisResult(entity: SavedDiagnosisEntity): DiagnosisResult {
        val cropType = when {
            entity.cropDisplayName.contains("Quinua", ignoreCase = true) -> CropType.QUINUA
            entity.cropDisplayName.contains("Papa", ignoreCase = true) -> CropType.PAPA
            entity.cropDisplayName.contains("Zanahoria", ignoreCase = true) -> CropType.ZANAHORIA
            else -> CropType.OTRO
        }
        val category = runCatching { ProblemCategory.valueOf(entity.categoryName) }
            .getOrDefault(ProblemCategory.HONGO_ENFERMEDAD)
        val urgency = runCatching { UrgencyLevel.valueOf(entity.urgencyName) }
            .getOrDefault(UrgencyLevel.MEDIO)

        return DiagnosisResult(
            id = entity.diagnosisCode,
            cropType = cropType,
            cropDisplayName = entity.cropDisplayName,
            commonName = entity.commonName,
            localName = entity.localName,
            scientificName = entity.scientificName,
            category = category,
            referenceImageRes = LocalAgronomyDataset.findReferenceResByKey(entity.referenceResKey),
            capturedPhotoUri = entity.photoUri,
            keySymptoms = entity.symptomsJoined.split(SEP).filter { it.isNotBlank() },
            urgency = urgency,
            harvestImpactAlert = entity.harvestImpactAlert,
            organicTreatment = OrganicTreatment(
                name = entity.organicName,
                ingredients = entity.organicIngredientsJoined.split(SEP).filter { it.isNotBlank() },
                preparationSteps = entity.organicStepsJoined.split(SEP).filter { it.isNotBlank() },
                applicationGuide = entity.organicApplication
            ),
            chemicalTreatment = ChemicalTreatment(
                activeIngredient = entity.chemicalActive,
                dosagePer20L = entity.chemicalDosage,
                frequency = entity.chemicalFrequency,
                preHarvestDays = entity.chemicalCarenciaDays,
                safetyNote = entity.chemicalSafety
            ),
            culturalPrevention = entity.culturalJoined.split(SEP).filter { it.isNotBlank() },
            defaultCrossClimateAlert = entity.crossClimateAlert,
            isFromCloudAi = entity.isFromCloudAi
        )
    }
}
