package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.model.CostEntryEntity
import com.example.model.Crop
import com.example.model.Diagnosis
import com.example.model.ExpenseRecord
import com.example.model.Municipality
import com.example.model.Pest
import com.example.model.SavedDiagnosisEntity
import com.example.model.Treatment
import com.example.model.WeatherAlert
import kotlinx.coroutines.flow.Flow

/**
 * DAO principal de Jark'a Yapu para operaciones CRUD completas (Room)
 * sobre cultivos, plagas, tratamientos, diagnósticos, gastos y alertas climáticas offline.
 */
@Dao
interface JarkaYapuDao {

    // --- Crop CRUD ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrop(crop: Crop)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrops(crops: List<Crop>)

    @Query("SELECT * FROM crops ORDER BY name ASC")
    fun observeAllCrops(): Flow<List<Crop>>

    @Query("SELECT * FROM crops WHERE id = :cropId LIMIT 1")
    suspend fun getCropById(cropId: String): Crop?

    @Update
    suspend fun updateCrop(crop: Crop)

    @Delete
    suspend fun deleteCrop(crop: Crop)

    @Query("DELETE FROM crops WHERE id = :cropId")
    suspend fun deleteCropById(cropId: String)

    // --- Pest CRUD ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPest(pest: Pest)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPests(pests: List<Pest>)

    @Query("SELECT * FROM pests ORDER BY common_name ASC")
    fun observeAllPests(): Flow<List<Pest>>

    @Query("SELECT * FROM pests WHERE crop_id = :cropId ORDER BY common_name ASC")
    fun observePestsByCrop(cropId: String): Flow<List<Pest>>

    @Query("SELECT * FROM pests WHERE id = :pestId LIMIT 1")
    suspend fun getPestById(pestId: String): Pest?

    @Update
    suspend fun updatePest(pest: Pest)

    @Delete
    suspend fun deletePest(pest: Pest)

    // --- Treatment CRUD ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTreatment(treatment: Treatment)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTreatments(treatments: List<Treatment>)

    @Query("SELECT * FROM treatments WHERE pest_id = :pestId ORDER BY is_organic DESC")
    fun observeTreatmentsForPest(pestId: String): Flow<List<Treatment>>

    @Update
    suspend fun updateTreatment(treatment: Treatment)

    @Delete
    suspend fun deleteTreatment(treatment: Treatment)

    // --- Diagnosis CRUD (Almacenamiento local de diagnósticos en parcela) ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiagnosis(diagnosis: Diagnosis): Long

    @Query("SELECT * FROM diagnoses ORDER BY created_at_millis DESC")
    fun observeAllDiagnoses(): Flow<List<Diagnosis>>

    @Query("SELECT * FROM diagnoses WHERE municipality = :municipality ORDER BY created_at_millis DESC")
    fun observeDiagnosesByMunicipality(municipality: Municipality): Flow<List<Diagnosis>>

    @Query("SELECT * FROM diagnoses WHERE id = :diagnosisId LIMIT 1")
    suspend fun getDiagnosisById(diagnosisId: Long): Diagnosis?

    @Update
    suspend fun updateDiagnosis(diagnosis: Diagnosis)

    @Delete
    suspend fun deleteDiagnosis(diagnosis: Diagnosis)

    @Query("DELETE FROM diagnoses WHERE id = :diagnosisId")
    suspend fun deleteDiagnosisById(diagnosisId: Long)

    // --- ExpenseRecord CRUD (Cuaderno de gastos en Bs.) ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpenseRecord(record: ExpenseRecord): Long

    @Query("SELECT * FROM expense_records ORDER BY recorded_at_millis DESC")
    fun observeAllExpenseRecords(): Flow<List<ExpenseRecord>>

    @Query("SELECT * FROM expense_records WHERE diagnosis_id = :diagnosisId ORDER BY recorded_at_millis DESC")
    fun observeExpensesForDiagnosis(diagnosisId: Long): Flow<List<ExpenseRecord>>

    @Query("SELECT COALESCE(SUM(total_cost_bs), 0.0) FROM expense_records")
    fun observeTotalExpensesBs(): Flow<Double>

    @Update
    suspend fun updateExpenseRecord(record: ExpenseRecord)

    @Delete
    suspend fun deleteExpenseRecord(record: ExpenseRecord)

    @Query("DELETE FROM expense_records WHERE id = :recordId")
    suspend fun deleteExpenseRecordById(recordId: Long)

    // --- WeatherAlert CRUD (Caché offline de clima de Provincia Aroma) ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherAlert(alert: WeatherAlert)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherAlerts(alerts: List<WeatherAlert>)

    @Query("SELECT * FROM weather_alerts ORDER BY cached_at_millis DESC")
    fun observeAllWeatherAlerts(): Flow<List<WeatherAlert>>

    @Query("SELECT * FROM weather_alerts WHERE municipality = :municipality ORDER BY cached_at_millis DESC")
    fun observeWeatherAlertsByMunicipality(municipality: Municipality): Flow<List<WeatherAlert>>

    @Update
    suspend fun updateWeatherAlert(alert: WeatherAlert)

    @Delete
    suspend fun deleteWeatherAlert(alert: WeatherAlert)

    @Query("DELETE FROM weather_alerts WHERE municipality = :municipality")
    suspend fun clearWeatherAlertsForMunicipality(municipality: Municipality)
}

@Dao
interface SavedDiagnosisDao {
    @Query("SELECT * FROM saved_diagnoses ORDER BY timestampMillis DESC")
    fun observeAll(): Flow<List<SavedDiagnosisEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: SavedDiagnosisEntity): Long

    @Update
    suspend fun update(entity: SavedDiagnosisEntity)

    @Query("DELETE FROM saved_diagnoses WHERE id = :id")
    suspend fun deleteById(id: Long)
}

@Dao
interface CostEntryDao {
    @Query("SELECT * FROM cost_entries ORDER BY timestampMillis DESC")
    fun observeAll(): Flow<List<CostEntryEntity>>

    @Query("SELECT COALESCE(SUM(totalCostBs), 0.0) FROM cost_entries")
    fun observeTotalCostBs(): Flow<Double>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: CostEntryEntity): Long

    @Update
    suspend fun update(entity: CostEntryEntity)

    @Query("DELETE FROM cost_entries WHERE id = :id")
    suspend fun deleteById(id: Long)
}
