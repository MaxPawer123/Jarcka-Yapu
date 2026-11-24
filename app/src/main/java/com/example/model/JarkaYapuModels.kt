package com.example.model

import androidx.annotation.DrawableRes
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

enum class ConnectivityMode(val label: String, val shortLabel: String) {
    OFFLINE_CAMPO("Modo Campo Offline", "Campo Offline"),
    ONLINE_NUBE("Modo Nube Online", "Nube Online")
}

enum class AppModule(val route: String, val title: String) {
    DIAGNOSTICO("diagnostico", "Diagnóstico"),
    CLIMA("clima", "Clima"),
    SUELOS("suelos", "Suelos"),
    CUADERNO("cuaderno", "Cuaderno")
}

enum class CropType(val displayName: String) {
    QUINUA("Quinua"),
    PAPA("Papa"),
    ZANAHORIA("Zanahoria"),
    OTRO("Cultivo Universal")
}

enum class ProblemCategory(val displayName: String) {
    INSECTO_PLAGA("Insecto / Plaga"),
    HONGO_ENFERMEDAD("Hongo / Enfermedad"),
    DEFICIENCIA_NUTRICIONAL("Deficiencia nutricional")
}

enum class UrgencyLevel(val label: String, val actionHeadline: String) {
    BAJO("Bajo - Monitoreo", "Monitorear parcela cada 5 días"),
    MEDIO("Medio - Acción pronto", "Intervenir en los próximos 2 a 3 días"),
    ALTO("Alto / Crítico - Actuar hoy", "Acción inmediata hoy mismo en parcela")
}

enum class Municipality(
    val displayName: String,
    val altitudeMeters: Int,
    val zoneNote: String
) {
    SICA_SICA("Sica Sica", 3925, "Capital provincial · Pampa y serranía alta"),
    PATACAMAYA("Patacamaya", 3789, "Valle altiplánico · Zona quinua y hortalizas"),
    AYO_AYO("Ayo Ayo", 3860, "Altiplano central · Suelos franco-arenosos"),
    UMALA("Umala", 3815, "Cabecera de microcuenca · Alta helada nocturna")
}

// ============================================================================
// ENTIDADES ROOM PARA PERSISTENCIA OFFLINE-FIRST (Jark'a Yapu · Provincia Aroma)
// ============================================================================

@Entity(tableName = "crops")
data class Crop(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "scientific_name")
    val scientificName: String,
    @ColumnInfo(name = "local_name")
    val localName: String,
    @ColumnInfo(name = "altitude_range")
    val altitudeRange: String,
    @ColumnInfo(name = "soil_preference")
    val soilPreference: String
)

@Entity(
    tableName = "pests",
    foreignKeys = [
        ForeignKey(
            entity = Crop::class,
            parentColumns = ["id"],
            childColumns = ["crop_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["crop_id"])]
)
data class Pest(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "crop_id")
    val cropId: String,
    @ColumnInfo(name = "common_name")
    val commonName: String,
    @ColumnInfo(name = "local_name")
    val localName: String,
    @ColumnInfo(name = "scientific_name")
    val scientificName: String,
    @ColumnInfo(name = "category")
    val category: ProblemCategory,
    @ColumnInfo(name = "urgency")
    val urgency: UrgencyLevel,
    @ColumnInfo(name = "key_symptoms")
    val keySymptoms: List<String>,
    @ColumnInfo(name = "harvest_impact_alert")
    val harvestImpactAlert: String,
    @ColumnInfo(name = "cultural_prevention")
    val culturalPrevention: List<String>
)

@Entity(
    tableName = "treatments",
    foreignKeys = [
        ForeignKey(
            entity = Pest::class,
            parentColumns = ["id"],
            childColumns = ["pest_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["pest_id"])]
)
data class Treatment(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "pest_id")
    val pestId: String,
    @ColumnInfo(name = "is_organic")
    val isOrganic: Boolean,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "active_ingredient_or_inputs")
    val activeIngredientOrInputs: String,
    @ColumnInfo(name = "dosage_per_20l")
    val dosagePer20L: String,
    @ColumnInfo(name = "preparation_steps")
    val preparationSteps: List<String>,
    @ColumnInfo(name = "application_guide")
    val applicationGuide: String,
    @ColumnInfo(name = "frequency")
    val frequency: String,
    @ColumnInfo(name = "pre_harvest_days")
    val preHarvestDays: Int,
    @ColumnInfo(name = "safety_note")
    val safetyNote: String
)

@Entity(
    tableName = "diagnoses",
    indices = [Index(value = ["crop_id"]), Index(value = ["pest_id"])]
)
data class Diagnosis(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "parcela_name")
    val parcelaName: String,
    @ColumnInfo(name = "municipality")
    val municipality: Municipality,
    @ColumnInfo(name = "crop_id")
    val cropId: String?,
    @ColumnInfo(name = "crop_name")
    val cropName: String,
    @ColumnInfo(name = "pest_id")
    val pestId: String?,
    @ColumnInfo(name = "common_name")
    val commonName: String,
    @ColumnInfo(name = "local_name")
    val localName: String,
    @ColumnInfo(name = "scientific_name")
    val scientificName: String,
    @ColumnInfo(name = "category")
    val category: ProblemCategory,
    @ColumnInfo(name = "urgency")
    val urgency: UrgencyLevel,
    @ColumnInfo(name = "observed_symptoms")
    val observedSymptoms: List<String>,
    @ColumnInfo(name = "recommended_organic")
    val recommendedOrganic: String,
    @ColumnInfo(name = "recommended_chemical")
    val recommendedChemical: String,
    @ColumnInfo(name = "cross_climate_alert")
    val crossClimateAlert: String,
    @ColumnInfo(name = "harvest_impact_alert")
    val harvestImpactAlert: String = "Monitorear la parcela para evitar pérdida de rendimiento.",
    @ColumnInfo(name = "organic_ingredients")
    val organicIngredients: List<String> = emptyList(),
    @ColumnInfo(name = "organic_steps")
    val organicSteps: List<String> = emptyList(),
    @ColumnInfo(name = "organic_application")
    val organicApplication: String = "",
    @ColumnInfo(name = "chemical_dosage")
    val chemicalDosage: String = "Según etiqueta para mochila de 20 L",
    @ColumnInfo(name = "chemical_frequency")
    val chemicalFrequency: String = "Cada 10 a 14 días según severidad",
    @ColumnInfo(name = "pre_harvest_days")
    val preHarvestDays: Int = 15,
    @ColumnInfo(name = "chemical_safety")
    val chemicalSafety: String = "Usar guantes, mascarilla y respetar el tiempo de carencia.",
    @ColumnInfo(name = "cultural_prevention")
    val culturalPrevention: List<String> = emptyList(),
    @ColumnInfo(name = "reference_res_key")
    val referenceResKey: String? = null,
    @ColumnInfo(name = "photo_uri")
    val photoUri: String? = null,
    @ColumnInfo(name = "is_from_cloud_ai")
    val isFromCloudAi: Boolean = false,
    @ColumnInfo(name = "created_at_millis")
    val createdAtMillis: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "expense_records",
    indices = [Index(value = ["diagnosis_id"])]
)
data class ExpenseRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "diagnosis_id")
    val diagnosisId: Long? = null,
    @ColumnInfo(name = "parcela_name")
    val parcelaName: String,
    @ColumnInfo(name = "crop_name")
    val cropName: String,
    @ColumnInfo(name = "treatment_name")
    val treatmentName: String,
    @ColumnInfo(name = "input_name")
    val inputName: String,
    @ColumnInfo(name = "input_cost_bs")
    val inputCostBs: Double,
    @ColumnInfo(name = "labor_days")
    val laborDays: Double,
    @ColumnInfo(name = "labor_daily_rate_bs")
    val laborDailyRateBs: Double,
    @ColumnInfo(name = "total_cost_bs")
    val totalCostBs: Double,
    @ColumnInfo(name = "notes")
    val notes: String = "",
    @ColumnInfo(name = "recorded_at_millis")
    val recordedAtMillis: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "weather_alerts",
    indices = [Index(value = ["municipality"])]
)
data class WeatherAlert(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "municipality")
    val municipality: Municipality,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "hazard_type")
    val hazardType: String,
    @ColumnInfo(name = "risk_level")
    val riskLevel: UrgencyLevel,
    @ColumnInfo(name = "window_label")
    val windowLabel: String,
    @ColumnInfo(name = "min_temp_c")
    val minTempC: Int,
    @ColumnInfo(name = "wind_kmh")
    val windKmh: Int,
    @ColumnInfo(name = "rain_probability_pct")
    val rainProbabilityPct: Int,
    @ColumnInfo(name = "detail")
    val detail: String,
    @ColumnInfo(name = "crop_recommendation")
    val cropRecommendation: String,
    @ColumnInfo(name = "cached_at_millis")
    val cachedAtMillis: Long = System.currentTimeMillis()
)

class JarkaRoomConverters {
    private val separator = "||"

    @TypeConverter
    fun fromStringList(value: List<String>?): String =
        value?.joinToString(separator).orEmpty()

    @TypeConverter
    fun toStringList(raw: String?): List<String> =
        raw?.split(separator)?.filter { it.isNotBlank() } ?: emptyList()

    @TypeConverter
    fun fromMunicipality(value: Municipality): String = value.name

    @TypeConverter
    fun toMunicipality(raw: String): Municipality =
        runCatching { Municipality.valueOf(raw) }.getOrDefault(Municipality.SICA_SICA)

    @TypeConverter
    fun fromProblemCategory(value: ProblemCategory): String = value.name

    @TypeConverter
    fun toProblemCategory(raw: String): ProblemCategory =
        runCatching { ProblemCategory.valueOf(raw) }.getOrDefault(ProblemCategory.HONGO_ENFERMEDAD)

    @TypeConverter
    fun fromUrgencyLevel(value: UrgencyLevel): String = value.name

    @TypeConverter
    fun toUrgencyLevel(raw: String): UrgencyLevel =
        runCatching { UrgencyLevel.valueOf(raw) }.getOrDefault(UrgencyLevel.MEDIO)
}

// ============================================================================
// MODELOS DE DOMINIO Y PRESENTACIÓN DE LA APP
// ============================================================================

data class OrganicTreatment(
    val name: String,
    val ingredients: List<String>,
    val preparationSteps: List<String>,
    val applicationGuide: String
)

data class ChemicalTreatment(
    val activeIngredient: String,
    val dosagePer20L: String,
    val frequency: String,
    val preHarvestDays: Int,
    val safetyNote: String
)

data class DiagnosisResult(
    val id: String,
    val cropType: CropType,
    val cropDisplayName: String,
    val commonName: String,
    val localName: String,
    val scientificName: String,
    val category: ProblemCategory,
    @DrawableRes val referenceImageRes: Int? = null,
    val capturedPhotoUri: String? = null,
    val keySymptoms: List<String>,
    val urgency: UrgencyLevel,
    val harvestImpactAlert: String,
    val organicTreatment: OrganicTreatment,
    val chemicalTreatment: ChemicalTreatment,
    val culturalPrevention: List<String>,
    val defaultCrossClimateAlert: String,
    val isFromCloudAi: Boolean = false,
    val symptomFilterTags: List<String> = emptyList()
)

data class WeatherHazardAlert(
    val title: String,
    val riskLevel: UrgencyLevel,
    val windowLabel: String,
    val detail: String
)

data class CropClimateAdvice(
    val cropName: String,
    val actionNow: String
)

data class MunicipalityClimate(
    val municipality: Municipality,
    val currentTempC: Int,
    val minNightTempC: Int,
    val maxDayTempC: Int,
    val windKmh: Int,
    val rainProbabilityPct: Int,
    val humidityPct: Int,
    val cacheTimestamp: String,
    val activeCrossAlertForSpray: String,
    val hazardAlerts: List<WeatherHazardAlert>,
    val cropRecommendations: List<CropClimateAdvice>
)

data class ConservationPractice(
    val title: String,
    val categoryBadge: String,
    val description: String,
    val localMaterials: String,
    val implementationSteps: List<String>
)

data class NasaSoilProfile(
    val municipality: Municipality,
    val ndviValue: Float,
    val ndviStatus: String,
    val soilMoisturePct: Int,
    val surfaceTempC: Int,
    val organicMatterPct: Float,
    val salinityStatus: String,
    val erosionRisk: UrgencyLevel,
    val satelliteSourceNote: String,
    val soilDiagnosisSummary: String,
    val conservationPlan: List<ConservationPractice>
)

@Entity(tableName = "saved_diagnoses")
data class SavedDiagnosisEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val diagnosisCode: String,
    val parcelaName: String,
    val municipalityName: String,
    val cropDisplayName: String,
    val commonName: String,
    val localName: String,
    val scientificName: String,
    val categoryName: String,
    val urgencyName: String,
    val harvestImpactAlert: String,
    val symptomsJoined: String,
    val organicName: String,
    val organicIngredientsJoined: String,
    val organicStepsJoined: String,
    val organicApplication: String,
    val chemicalActive: String,
    val chemicalDosage: String,
    val chemicalFrequency: String,
    val chemicalCarenciaDays: Int,
    val chemicalSafety: String,
    val culturalJoined: String,
    val crossClimateAlert: String,
    val photoUri: String?,
    val referenceResKey: String?,
    val isFromCloudAi: Boolean,
    val timestampMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "cost_entries")
data class CostEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val parcelaName: String,
    val cropName: String,
    val diagnosisTitle: String,
    val inputName: String,
    val inputCostBs: Double,
    val laborDays: Double,
    val laborDailyRateBs: Double,
    val totalCostBs: Double,
    val notes: String,
    val timestampMillis: Long = System.currentTimeMillis()
)

sealed interface ScanUiState {
    data object Idle : ScanUiState
    data object Loading : ScanUiState
    data class Success(val result: DiagnosisResult) : ScanUiState
    data class Error(val message: String, val canRetry: Boolean = true) : ScanUiState
}
