package com.example.network

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.example.model.ChemicalTreatment
import com.example.model.CropType
import com.example.model.Diagnosis
import com.example.model.DiagnosisResult
import com.example.model.Municipality
import com.example.model.OrganicTreatment
import com.example.model.ProblemCategory
import com.example.model.UrgencyLevel
import com.google.firebase.Firebase
import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import kotlin.math.max

private const val TAG = "GeminiVisionService"
private const val PRIMARY_MODEL = "gemini-3.8-flash"
private const val FALLBACK_MODEL = "gemini-3.5-flash-lite"

/**
 * Servicio de visión artificial basado en Firebase AI Logic (`Firebase.ai`)
 * para diagnosticar plagas, enfermedades y deficiencias en cultivos a partir de una imagen [Bitmap].
 */
class GeminiVisionService(private val context: Context) {

    private val diagnosisSchema = Schema.obj(
        properties = mapOf(
            "cropName" to Schema.string(
                description = "Nombre del cultivo detectado en español (ej. Quinua, Haba, Papa, Zanahoria, Cebada, Alfalfa)"
            ),
            "commonName" to Schema.string(
                description = "Nombre común de la plaga, enfermedad o deficiencia"
            ),
            "localName" to Schema.string(
                description = "Nombre local o andino equivalente en Bolivia"
            ),
            "scientificName" to Schema.string(
                description = "Nombre científico si aplica o 'No determinado'"
            ),
            "category" to Schema.enumeration(
                values = listOf("INSECTO_PLAGA", "HONGO_ENFERMEDAD", "DEFICIENCIA_NUTRICIONAL"),
                description = "Categoría del problema agrícola"
            ),
            "keySymptoms" to Schema.array(
                items = Schema.string(),
                description = "Exactamente 3 viñetas cortas con síntomas visuales observados en la fotografía"
            ),
            "urgency" to Schema.enumeration(
                values = listOf("BAJO", "MEDIO", "ALTO"),
                description = "Nivel de urgencia"
            ),
            "harvestImpactAlert" to Schema.string(
                description = "Alerta breve del impacto en la cosecha"
            ),
            "organicName" to Schema.string(
                description = "Nombre del tratamiento orgánico o bioinsumo local"
            ),
            "organicIngredients" to Schema.array(
                items = Schema.string(),
                description = "Lista de ingredientes locales para mochila de 20 litros"
            ),
            "organicSteps" to Schema.array(
                items = Schema.string(),
                description = "Pasos cortos de preparación del bioinsumo"
            ),
            "organicApplication" to Schema.string(
                description = "Cómo y a qué hora aplicar el preparado orgánico"
            ),
            "chemicalActive" to Schema.string(
                description = "Principio activo químico recomendado solo para casos severos"
            ),
            "chemicalDosage20L" to Schema.string(
                description = "Dosis exacta en ml o g por mochila de 20 litros"
            ),
            "chemicalFrequency" to Schema.string(
                description = "Frecuencia de aplicación"
            ),
            "preHarvestDays" to Schema.integer(
                description = "Días de carencia antes de cosechar"
            ),
            "chemicalSafety" to Schema.string(
                description = "Advertencia práctica de seguridad para el agricultor"
            ),
            "culturalPrevention" to Schema.array(
                items = Schema.string(),
                description = "Lista de 3 a 4 prácticas culturales sin costo en insumos"
            ),
            "crossClimateAlert" to Schema.string(
                description = "Recomendación cruzada considerando heladas, lluvias o vientos del Altiplano"
            )
        )
    )

    private val sharedGenerationConfig = generationConfig {
        responseMimeType = "application/json"
        responseSchema = diagnosisSchema
        temperature = 0.2f
    }

    private val primaryModel by lazy {
        Firebase.ai.generativeModel(
            modelName = PRIMARY_MODEL,
            generationConfig = sharedGenerationConfig
        )
    }

    private val fallbackModel by lazy {
        Firebase.ai.generativeModel(
            modelName = FALLBACK_MODEL,
            generationConfig = sharedGenerationConfig
        )
    }

    /**
     * Analiza una fotografía mediante [Uri], decodificando un [Bitmap] optimizado
     * y construyendo el prompt agronómico contextualizado al municipio seleccionado.
     */
    suspend fun analyzeCropPhoto(
        imageUri: Uri,
        municipality: Municipality,
        farmerNote: String
    ): DiagnosisResult {
        val bitmap = decodeAndResizeBitmap(imageUri)
            ?: throw IllegalStateException("No se pudo leer la fotografía seleccionada.")

        val promptText = buildAgronomyPrompt(municipality, farmerNote)
        return diagnoseCropFromBitmap(
            bitmap = bitmap,
            prompt = promptText,
            photoUri = imageUri.toString()
        )
    }

    /**
     * Procesa directamente un [Bitmap] y un [prompt] específico usando Firebase AI Logic (`Firebase.ai`)
     * y retorna un [DiagnosisResult] compatible con todos los bloques y entidades de `JarkaYapuModels.kt`.
     */
    suspend fun diagnoseCropFromBitmap(
        bitmap: Bitmap,
        prompt: String,
        photoUri: String? = null
    ): DiagnosisResult {
        val preparedBitmap = prepareBitmapForUpload(bitmap)

        val responseText = try {
            withTimeout(60_000L) {
                executeWithModelFallback(preparedBitmap, prompt)
            }
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Gemini Vision timed out", e)
            throw IllegalStateException(
                "La consulta a la IA superó los 60 segundos. Verifica tu señal móvil e intenta nuevamente."
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Gemini Vision error", e)
            val rawMessage = e.localizedMessage.orEmpty()
            val friendlyMessage = if (isTransientCapacityError(e)) {
                "Los servidores de IA están saturados temporalmente por alta demanda. Presiona Reintentar en unos segundos o consulta la Guía Offline."
            } else {
                "Error al analizar la imagen en la nube: ${rawMessage.ifBlank { "Verifica tu conexión a internet" }}."
            }
            throw IllegalStateException(friendlyMessage)
        }

        if (responseText.isNullOrBlank()) {
            throw IllegalStateException(
                "La IA devolvió una respuesta vacía. Intenta tomar la foto con mejor luz."
            )
        }

        return parseDiagnosisJson(responseText, photoUri)
    }

    /**
     * Convierte un [DiagnosisResult] obtenido desde Gemini en la entidad Room [Diagnosis]
     * definida en `JarkaYapuModels.kt` para persistencia offline.
     */
    fun toRoomDiagnosis(
        result: DiagnosisResult,
        parcelaName: String,
        municipality: Municipality
    ): Diagnosis = Diagnosis(
        parcelaName = parcelaName.ifBlank { "Parcela ${municipality.displayName}" },
        municipality = municipality,
        cropId = result.cropType.name.lowercase(),
        cropName = result.cropDisplayName,
        pestId = result.id,
        commonName = result.commonName,
        localName = result.localName,
        scientificName = result.scientificName,
        category = result.category,
        urgency = result.urgency,
        observedSymptoms = result.keySymptoms,
        recommendedOrganic = "${result.organicTreatment.name}: ${result.organicTreatment.applicationGuide}",
        recommendedChemical = "${result.chemicalTreatment.activeIngredient} (${result.chemicalTreatment.dosagePer20L})",
        crossClimateAlert = result.defaultCrossClimateAlert,
        photoUri = result.capturedPhotoUri,
        isFromCloudAi = result.isFromCloudAi
    )

    fun buildAgronomyPrompt(
        municipality: Municipality,
        farmerNote: String = ""
    ): String = buildString {
        appendLine(
            "Eres un ingeniero agrónomo especialista en sanidad vegetal del Altiplano de La Paz, Bolivia " +
                "(Provincia Aroma: ${municipality.displayName}, ${municipality.altitudeMeters} m s.n.m.)."
        )
        appendLine(
            "Analiza detalladamente la fotografía enviada por el agricultor " +
                "(puede ser quinua, haba, papa, zanahoria, cebolla, cebada, alfalfa u otro cultivo)."
        )
        appendLine(
            "Identifica con precisión cualquier insecto visible (chinches, polillas, pulgones, escarabajos, gorgojos, trips), " +
                "daño fúngico (mildiu, rancha, mancha chocolate en haba, roya, alternaria) o deficiencia nutricional."
        )
        if (farmerNote.isNotBlank()) {
            appendLine("Nota del agricultor sobre el cultivo o síntoma: \"$farmerNote\"")
        }
        appendLine("Responde exclusivamente en español claro y práctico para lectura en parcela.")
        appendLine("Si el daño es leve o la planta se observa sana, indícalo honestamente en urgencia BAJO.")
        appendLine(
            "Incluye medidas exactas por mochila de 20 litros tanto en control orgánico/ecológico " +
                "como en control químico y tiempo de carencia en días."
        )
    }

    private suspend fun executeWithModelFallback(
        bitmap: Bitmap,
        promptText: String
    ): String? {
        val requestContent = content {
            image(bitmap)
            text(promptText)
        }

        val attempts: List<Pair<String, GenerativeModel>> = listOf(
            PRIMARY_MODEL to primaryModel,
            FALLBACK_MODEL to fallbackModel,
            "$FALLBACK_MODEL (reintento)" to fallbackModel
        )

        var lastError: Exception? = null
        for ((index, attempt) in attempts.withIndex()) {
            val (label, model) = attempt
            try {
                if (index == 2) {
                    delay(1_500L)
                }
                Log.d(TAG, "Executing vision analysis with $label")
                val response = model.generateContent(requestContent)
                if (!response.text.isNullOrBlank()) {
                    return response.text
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                lastError = e
                Log.w(TAG, "Model attempt $label failed: ${e.message}")
                if (index == 0) {
                    val msg = e.message.orEmpty().lowercase()
                    if (msg.contains("403") || msg.contains("permission") || msg.contains("invalid_argument")) {
                        throw e
                    }
                }
            }
        }
        throw lastError ?: IllegalStateException("No se obtuvo respuesta de los modelos de visión.")
    }

    private fun isTransientCapacityError(error: Throwable): Boolean {
        val msg = (error.message ?: error.localizedMessage).orEmpty().lowercase()
        return msg.contains("high demand") ||
            msg.contains("temporarily") ||
            msg.contains("unavailable") ||
            msg.contains("overloaded") ||
            msg.contains("resource_exhausted") ||
            msg.contains("503") ||
            msg.contains("429") ||
            msg.contains("try again later")
    }

    private fun parseDiagnosisJson(jsonString: String, photoUri: String?): DiagnosisResult {
        val json = JSONObject(jsonString)

        val cropName = json.optString("cropName", "Cultivo agrícola")
        val cropType = when {
            cropName.contains("quinua", ignoreCase = true) -> CropType.QUINUA
            cropName.contains("papa", ignoreCase = true) -> CropType.PAPA
            cropName.contains("zanahoria", ignoreCase = true) -> CropType.ZANAHORIA
            else -> CropType.OTRO
        }

        val category = runCatching {
            ProblemCategory.valueOf(json.optString("category", "HONGO_ENFERMEDAD"))
        }.getOrDefault(ProblemCategory.HONGO_ENFERMEDAD)

        val urgency = runCatching {
            UrgencyLevel.valueOf(json.optString("urgency", "MEDIO"))
        }.getOrDefault(UrgencyLevel.MEDIO)

        val symptomsArr = json.optJSONArray("keySymptoms")
        val keySymptoms = if (symptomsArr != null) {
            (0 until symptomsArr.length()).map { symptomsArr.getString(it) }.take(3)
        } else {
            listOf("Alteración visual detectada en la fotografía enviada.")
        }

        val ingredientsArr = json.optJSONArray("organicIngredients")
        val organicIngredients = if (ingredientsArr != null) {
            (0 until ingredientsArr.length()).map { ingredientsArr.getString(it) }
        } else emptyList()

        val stepsArr = json.optJSONArray("organicSteps")
        val organicSteps = if (stepsArr != null) {
            (0 until stepsArr.length()).map { stepsArr.getString(it) }
        } else emptyList()

        val culturalArr = json.optJSONArray("culturalPrevention")
        val culturalList = if (culturalArr != null) {
            (0 until culturalArr.length()).map { culturalArr.getString(it) }
        } else emptyList()

        return DiagnosisResult(
            id = "ai_${System.currentTimeMillis()}",
            cropType = cropType,
            cropDisplayName = cropName,
            commonName = json.optString("commonName", "Diagnóstico visual IA"),
            localName = json.optString("localName", "Identificación asistida por IA"),
            scientificName = json.optString("scientificName", "No determinado"),
            category = category,
            referenceImageRes = null,
            capturedPhotoUri = photoUri,
            keySymptoms = keySymptoms.ifEmpty { listOf("Observación visual en parcela.") },
            urgency = urgency,
            harvestImpactAlert = json.optString(
                "harvestImpactAlert",
                "Monitorear evolución en los próximos días."
            ),
            organicTreatment = OrganicTreatment(
                name = json.optString("organicName", "Manejo agroecológico local"),
                ingredients = organicIngredients.ifEmpty {
                    listOf("Bioinsumo local diluido en 20 L de agua")
                },
                preparationSteps = organicSteps.ifEmpty {
                    listOf("Mezclar en agua limpia y colar antes de cargar la mochila.")
                },
                applicationGuide = json.optString(
                    "organicApplication",
                    "Aplicar en horas frescas de la mañana o tarde."
                )
            ),
            chemicalTreatment = ChemicalTreatment(
                activeIngredient = json.optString("chemicalActive", "Consultar agroservicio autorizado"),
                dosagePer20L = json.optString("chemicalDosage20L", "Según etiqueta para mochila de 20 L"),
                frequency = json.optString("chemicalFrequency", "Cada 10 a 14 días según severidad"),
                preHarvestDays = json.optInt("preHarvestDays", 15),
                safetyNote = json.optString(
                    "chemicalSafety",
                    "Usar guantes, mascarilla y respetar los días de carencia."
                )
            ),
            culturalPrevention = culturalList.ifEmpty {
                listOf("Rotar cultivos, eliminar rastrojos infectados y evitar exceso de humedad.")
            },
            defaultCrossClimateAlert = json.optString(
                "crossClimateAlert",
                "Verifica el pronóstico de heladas y viento de tu municipio antes de asperjar."
            ),
            isFromCloudAi = true
        )
    }

    private suspend fun decodeAndResizeBitmap(uri: Uri): Bitmap? = withContext(Dispatchers.IO) {
        val rawBitmap = context.contentResolver.openInputStream(uri)?.use { stream ->
            BitmapFactory.decodeStream(stream)
        } ?: return@withContext null
        prepareBitmapForUpload(rawBitmap)
    }

    private suspend fun prepareBitmapForUpload(rawBitmap: Bitmap): Bitmap =
        withContext(Dispatchers.Default) {
            val maxDim = max(rawBitmap.width, rawBitmap.height)
            val scaled = if (maxDim > 1024) {
                val scale = 1024f / maxDim.toFloat()
                val targetW = (rawBitmap.width * scale).toInt().coerceAtLeast(1)
                val targetH = (rawBitmap.height * scale).toInt().coerceAtLeast(1)
                Bitmap.createScaledBitmap(rawBitmap, targetW, targetH, true)
            } else {
                rawBitmap
            }

            val out = ByteArrayOutputStream()
            scaled.compress(Bitmap.CompressFormat.JPEG, 82, out)
            val bytes = out.toByteArray()
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size) ?: scaled
        }
}
