package com.example.data

import com.example.R
import com.example.model.ConservationPractice
import com.example.model.CropClimateAdvice
import com.example.model.CropType
import com.example.model.DiagnosisResult
import com.example.model.Municipality
import com.example.model.MunicipalityClimate
import com.example.model.NasaSoilProfile
import com.example.model.OrganicTreatment
import com.example.model.ChemicalTreatment
import com.example.model.ProblemCategory
import com.example.model.UrgencyLevel
import com.example.model.WeatherHazardAlert

object LocalAgronomyDataset {

    val symptomQuickFilters: List<String> = listOf(
        "Panoja comida o telarañas",
        "Polvo violáceo en envés",
        "Túneles en tubérculo",
        "Manchas negras acuosas",
        "Galerías oxidadas en raíz",
        "Puntas secas quemadas"
    )

    val localPestsAndDiseases: List<DiagnosisResult> = listOf(
        // 1. QUINUA - Kcona kcona
        DiagnosisResult(
            id = "quinua_kcona_kcona",
            cropType = CropType.QUINUA,
            cropDisplayName = "Quinua",
            commonName = "Polilla de la quinua",
            localName = "Kcona kcona / Ticona de panoja",
            scientificName = "Eurysacca melanocampta",
            category = ProblemCategory.INSECTO_PLAGA,
            referenceImageRes = R.drawable.img_quinua_ref,
            keySymptoms = listOf(
                "Larvas pequeñas verdosas o pardas dentro de la panoja formando hilos finos tipo telaraña.",
                "Granos lechosos o maduros mordidos y vaciados, dejando polvillo y excremento oscuro.",
                "Hojas tiernas enrolladas o pegadas entre sí durante la etapa vegetativa."
            ),
            urgency = UrgencyLevel.ALTO,
            harvestImpactAlert = "Puede destruir entre el 30% y 60% del grano en formación en pocos días si coincide con veranillos secos.",
            organicTreatment = OrganicTreatment(
                name = "Bioinsecticida Bacillus thuringiensis + Extracto de Ají y Ajo",
                ingredients = listOf(
                    "40 g de Bacillus thuringiensis (Bt kurstaki) o 250 g de ají amarillo + 100 g de ajo molido",
                    "100 g de jabón potásico o jabón neutro rallado (adherente)",
                    "20 litros de agua limpia sin cloro"
                ),
                preparationSteps = listOf(
                    "Si usa extracto local: licuar o machacar ajo y ají, macerar 24 horas en 2 litros de agua y colar con tela fina.",
                    "Disolver el jabón rallado en agua tibia y mezclar en la mochila de 20 L junto con Bt o el macerado.",
                    "Agitar bien antes de cargar para evitar que se tapen las boquillas."
                ),
                applicationGuide = "Aplicar al atardecer (después de las 17:00) dirigiendo la boquilla directo al interior de las panojas. Repetir cada 7 días si persisten larvas jóvenes."
            ),
            chemicalTreatment = ChemicalTreatment(
                activeIngredient = "Spinosad 48% SC (baja toxicidad) o Cipermetrina 20% EC",
                dosagePer20L = "15 ml de Spinosad ó 25 ml de Cipermetrina por mochila de 20 L",
                frequency = "1 aplicación focalizada en focos activos; repetir solo tras 12 días si supera 4 larvas/panoja",
                preHarvestDays = 15,
                safetyNote = "Usar mascarilla, guantes y gafas. No aplicar con viento fuerte ni cerca de colmenas o ganado."
            ),
            culturalPrevention = listOf(
                "Colocar trampas de agua con feromonas o luz solar nocturna desde inicio de panojamiento.",
                "Realizar roturación temprana del suelo tras la cosecha para exponer pupas a las heladas y aves.",
                "Mantener los bordes de parcela libres de malezas hospederas como la quinua silvestre (ajhara).",
                "Asociar hileras borde con tarwi o haba para atraer avispas controladoras naturales."
            ),
            defaultCrossClimateAlert = "Precaución Aroma: noches secas y días soleados aceleran la eclosión de larvas; revisa 10 panojas al azar hoy por la tarde.",
            symptomFilterTags = listOf("Panoja comida o telarañas")
        ),

        // 2. QUINUA - Mildiu
        DiagnosisResult(
            id = "quinua_mildiu",
            cropType = CropType.QUINUA,
            cropDisplayName = "Quinua",
            commonName = "Mildiu de la quinua",
            localName = "Q'ara / Ceniza de hoja",
            scientificName = "Peronospora variabilis",
            category = ProblemCategory.HONGO_ENFERMEDAD,
            referenceImageRes = R.drawable.img_quinua_ref,
            keySymptoms = listOf(
                "Manchas cloróticas amarillentas o rojizas en la cara superior de hojas bajeras.",
                "Polvillo grisáceo o violáceo característico en el envés (debajo) de la hoja húmeda.",
                "Caída prematura de hojas (defoliación) y retraso severo en el llenado de grano."
            ),
            urgency = UrgencyLevel.ALTO,
            harvestImpactAlert = "Con lluvias continuas y neblina matinal puede defoliar toda la planta y reducir el rendimiento hasta en un 70%.",
            organicTreatment = OrganicTreatment(
                name = "Caldo Sulfocálcico o Fosfito de Potasio + Extracto de Cola de Caballo",
                ingredients = listOf(
                    "250 ml de Caldo Sulfocálcico artesanal (o 300 g de cola de caballo hervida)",
                    "80 ml de adherente agrícola o jabón potásico neutro",
                    "20 litros de agua de lluvia o reposada"
                ),
                preparationSteps = listOf(
                    "Llenar la mochila de 20 L hasta la mitad con agua limpia.",
                    "Añadir lentamente los 250 ml de caldo sulfocálcico o decocción fría de cola de caballo.",
                    "Completar con agua, agregar el adherente y mezclar suavemente."
                ),
                applicationGuide = "Asperjar cubriendo el envés de las hojas entre las 7:00 y 10:00 de la mañana (nunca en horas de sol fuerte para evitar quemaduras). Repetir cada 7 a 10 días."
            ),
            chemicalTreatment = ChemicalTreatment(
                activeIngredient = "Metalaxil-M + Mancozeb 68% WP o Oxicloruro de Cobre 50% WP",
                dosagePer20L = "50 g de Metalaxil+Mancozeb ó 60 g de Oxicloruro de Cobre por mochila de 20 L",
                frequency = "Cada 10 a 14 días alternando ingredientes activos (máximo 3 aplicaciones por campaña)",
                preHarvestDays = 21,
                safetyNote = "Respetar estrictamente los 21 días de carencia antes de la siega y usar equipo de protección."
            ),
            culturalPrevention = listOf(
                "Evitar siembras muy densas; dejar surcos aireados de 50 a 60 cm para bajar humedad.",
                "Abrir drenajes en zonas bajas donde se empoza el agua de lluvia.",
                "Rotar con papa o cebada forrajera y no repetir quinua en la misma parcela dos años seguidos.",
                "Desinfectar la semilla con ceniza cernida o tricoderma antes de la siembra."
            ),
            defaultCrossClimateAlert = "Alerta humedad Aroma: tras lluvias recientes con nubosidad, el mildiu avanza rápido; aplica hoy antes de nueva llovizna.",
            symptomFilterTags = listOf("Polvo violáceo en envés")
        ),

        // 3. PAPA - Gorgojo de los Andes
        DiagnosisResult(
            id = "papa_gorgojo_andes",
            cropType = CropType.PAPA,
            cropDisplayName = "Papa",
            commonName = "Gorgojo de los Andes",
            localName = "Gusano blanco / Khuru de la papa",
            scientificName = "Premnotrypes spp.",
            category = ProblemCategory.INSECTO_PLAGA,
            referenceImageRes = R.drawable.img_papa_ref,
            keySymptoms = listOf(
                "Adultos nocturnos que dejan mordeduras en media luna en el borde de hojas bajas.",
                "Larvas blancas arqueadas sin patas perforando galerías oscuras dentro del tubérculo.",
                "Orificios de salida limpios al momento de cosechar o almacenar en pirwa/k'ayru."
            ),
            urgency = UrgencyLevel.ALTO,
            harvestImpactAlert = "Daña la calidad comercial y alimentaria del tubérculo; puede inutilizar más del 50% de la cosecha y contaminar la semilla.",
            organicTreatment = OrganicTreatment(
                name = "Hongo Entomopatógeno Beauveria bassiana + Barrera de Muña",
                ingredients = listOf(
                    "100 g de Beauveria bassiana en polvo mojable (o sustrato de arroz colonizado)",
                    "500 g de hojas machacadas de muña (Minthostachys) para borde de surco",
                    "20 litros de agua sin cloro"
                ),
                preparationSteps = listOf(
                    "Disolver la Beauveria bassiana en 1 litro de agua tibia con 1 cucharada de azúcar negra 30 minutos antes.",
                    "Verter en la mochila de 20 L filtrando con tela para no obstruir la boquilla.",
                    "Usar la mochila limpia exclusivamente sin restos previos de fungicidas químicos."
                ),
                applicationGuide = "Aplicar al cuello de la planta al anochecer (18:00–20:00) cuando salen los adultos a caminar y alimentarse. Complementar con zanja trampa perimetral."
            ),
            chemicalTreatment = ChemicalTreatment(
                activeIngredient = "Clorpirifos granulado (solo al aporque) o Fipronil 20% SC",
                dosagePer20L = "25 ml de Fipronil 20% SC dirigidos al cuello de planta (mochila 20 L)",
                frequency = "Aplicación única al momento de emergencia o primer aporque en parcelas con historial crítico",
                preHarvestDays = 45,
                safetyNote = "Alta toxicidad para abejas y fauna; no aplicar en floración ni en días con pronóstico de lluvia intensa."
            ),
            culturalPrevention = listOf(
                "Realizar aporque alto y oportuno tapando grietas del suelo para impedir que las larvas bajen al tubérculo.",
                "Cosechar sobre mantas plásticas o lonas (nunca directo sobre tierra abierta) para evitar que las larvas queden enterradas.",
                "Instalar zanjas trampa perimetrales con plástico y restos de follaje al inicio de lluvias.",
                "Espolvorear cal agrícola o ceniza con muña seca en el almacén de semilla."
            ),
            defaultCrossClimateAlert = "Aviso Aroma: las primeras lluvias nocturnas activan la salida de adultos del suelo; revisa bordes de hojas al anochecer.",
            symptomFilterTags = listOf("Túneles en tubérculo")
        ),

        // 4. PAPA - Tizón tardío / Rancha
        DiagnosisResult(
            id = "papa_tizon_rancha",
            cropType = CropType.PAPA,
            cropDisplayName = "Papa",
            commonName = "Tizón tardío de la papa",
            localName = "Rancha / Q'asawi",
            scientificName = "Phytophthora infestans",
            category = ProblemCategory.HONGO_ENFERMEDAD,
            referenceImageRes = R.drawable.img_papa_ref,
            keySymptoms = listOf(
                "Manchas irregulares de color verde oscuro a negro húmedo en puntas y bordes de hojas.",
                "Halo blanquecino algodonoso en el envés de las lesiones durante mañanas húmedas.",
                "Tallos quebradizos negruzcos con olor fétido característico y pudrición seca en tubérculos."
            ),
            urgency = UrgencyLevel.ALTO,
            harvestImpactAlert = "Es la enfermedad más destructiva de la papa: con neblina y lluvia puede secar toda la chacra en menos de 7 días.",
            organicTreatment = OrganicTreatment(
                name = "Caldo Bordelés (Sulfato de Cobre + Cal Viva) preventivo/curativo inicial",
                ingredients = listOf(
                    "200 g de Sulfato de Cobre pentahidratado",
                    "200 g de Cal viva o cal hidratada de buena calidad",
                    "20 litros de agua en recipientes plásticos (no metálicos)"
                ),
                preparationSteps = listOf(
                    "Disolver los 200 g de sulfato de cobre en 10 L de agua en un balde plástico.",
                    "Aparte, disolver los 200 g de cal en otros 10 L de agua.",
                    "Verter siempre la solución de cobre sobre la cal (nunca al revés), comprobar neutralidad con un machete limpio (no debe oxidarse) y colar."
                ),
                applicationGuide = "Aplicar inmediatamente el mismo día de preparación cubriendo follaje superior e inferior. No aplicar si la planta está en floración plena o bajo sol fuerte."
            ),
            chemicalTreatment = ChemicalTreatment(
                activeIngredient = "Cymoxanil + Mancozeb 72% WP o Dimetomorf + Mancozeb",
                dosagePer20L = "60 g de Cymoxanil+Mancozeb por mochila de 20 L",
                frequency = "Cada 7 días en época de lluvias persistentes; cada 12 días en tiempo seco",
                preHarvestDays = 14,
                safetyNote = "Cortar el follaje 10 días antes de cosechar si hubo rancha para no contagiar los tubérculos durante el escarbe."
            ),
            culturalPrevention = listOf(
                "Utilizar semilla certificada sana y descartar tubérculos con manchas marrones al partir.",
                "Aporcar alto para proteger los tubérculos del lavado de esporas por la lluvia.",
                "Orientar surcos según la pendiente suave y evitar encharcamientos.",
                "Quemar o enterrar profundo los restos de follaje enfermo después de la cosecha."
            ),
            defaultCrossClimateAlert = "Alerta crítica Aroma: humedad nocturna > 80% dispara la Rancha; aplica hoy por la tarde antes de que caiga sereno.",
            symptomFilterTags = listOf("Manchas negras acuosas")
        ),

        // 5. ZANAHORIA - Mosca de la zanahoria
        DiagnosisResult(
            id = "zanahoria_mosca",
            cropType = CropType.ZANAHORIA,
            cropDisplayName = "Zanahoria",
            commonName = "Mosca de la zanahoria",
            localName = "Gusano de la raíz",
            scientificName = "Psila rosae",
            category = ProblemCategory.INSECTO_PLAGA,
            referenceImageRes = R.drawable.img_zanahoria_ref,
            keySymptoms = listOf(
                "Follaje exterior toma coloración rojiza, bronceada o amarillenta sin causa de sequía.",
                "Galerías y surcos sinuosos de color herrumbre/oxidado en la parte externa de la raíz.",
                "Raíces bifurcadas o deformadas cuando el ataque ocurre en plántulas jóvenes."
            ),
            urgency = UrgencyLevel.MEDIO,
            harvestImpactAlert = "Reduce el valor comercial de la zanahoria en feria y favorece el ingreso de pudriciones blandas en el suelo.",
            organicTreatment = OrganicTreatment(
                name = "Repelente de Extracto de Cebolla/Ajo + Trampas Cromáticas Amarillas",
                ingredients = listOf(
                    "250 g de cebolla roja picada + 100 g de ajo machacado",
                    "100 ml de aceite de neem o jabón potásico",
                    "20 litros de agua y 6 láminas amarillas pegajosas por cada 500 m²"
                ),
                preparationSteps = listOf(
                    "Hervir los restos de cebolla y ajo durante 10 minutos en 2 L de agua, dejar enfriar toda la noche.",
                    "Colar el líquido y mezclarlo en la mochila de 20 L con el jabón potásico.",
                    "Colocar las trampas amarillas con aceite vegetal a la altura del follaje en los bordes del cultivo."
                ),
                applicationGuide = "Aplicar al cuello de las plantas durante el raleo o deshierbe (momento de mayor olor que atrae a la mosca). Repetir cada 10 días."
            ),
            chemicalTreatment = ChemicalTreatment(
                activeIngredient = "Deltametrina 2.5% EC o Lambda-cihalotrina 5% EC",
                dosagePer20L = "20 ml por mochila de 20 L dirigido a la base del follaje",
                frequency = "Cada 14 días solo si las trampas capturan más de 3 moscas por lámina/semana",
                preHarvestDays = 14,
                safetyNote = "Aplicar después del riego para que el olor del deshierbe no atraiga adultos y respetar 14 días antes de cosechar."
            ),
            culturalPrevention = listOf(
                "Realizar el raleo y deshierbe al atardecer y retirar inmediatamente las plantas arrancadas de la parcela.",
                "Asociar surcos intercalados con cebolla o puerro cuyo aroma confunde a la mosca.",
                "Cubrir ligeramente los hombros de la raíz con tierra suelta mediante un aporque ligero.",
                "Rotar con leguminosas (haba, arveja) o cereales durante al menos 2 campañas."
            ),
            defaultCrossClimateAlert = "Aviso Aroma: vientos calmos al atardecer favorecen el vuelo de la mosca; realiza el raleo en horas frescas.",
            symptomFilterTags = listOf("Galerías oxidadas en raíz")
        ),

        // 6. ZANAHORIA - Tizón foliar / Alternaria
        DiagnosisResult(
            id = "zanahoria_tizon_alternaria",
            cropType = CropType.ZANAHORIA,
            cropDisplayName = "Zanahoria",
            commonName = "Tizón foliar / Alternaria",
            localName = "Mancha negra / Quemado de hoja",
            scientificName = "Alternaria dauci",
            category = ProblemCategory.HONGO_ENFERMEDAD,
            referenceImageRes = R.drawable.img_zanahoria_ref,
            keySymptoms = listOf(
                "Manchas marrón oscuro a negro con borde amarillento en puntas y márgenes de hojas viejas.",
                "Las hojas afectadas se enrollan, se secan y parecen quemadas por helada o fertilizante.",
                "Debilitamiento del cuello de la hoja que impide arrancar la raíz intacta al cosechar."
            ),
            urgency = UrgencyLevel.MEDIO,
            harvestImpactAlert = "Reduce el tamaño y peso de la raíz al perder área verde fotosintética y dificulta el atado para venta.",
            organicTreatment = OrganicTreatment(
                name = "Biofungicida Trichoderma harzianum + Bicarbonato Potásico",
                ingredients = listOf(
                    "80 g de Trichoderma harzianum (o 60 g de bicarbonato + 60 ml de aceite agrícola)",
                    "80 ml de jabón potásico como dispersante",
                    "20 litros de agua limpia reposada"
                ),
                preparationSteps = listOf(
                    "Disolver el Trichoderma en 1 L de agua limpia sin cloro y dejar activar 20 minutos.",
                    "Agregar a la mochila de 20 L con agua y jabón potásico.",
                    "Agitar suavemente antes de asperjar."
                ),
                applicationGuide = "Aplicar cubriendo uniformemente todo el follaje en horas de la mañana sin viento. Repetir cada 8 a 10 días hasta controlar avance en hojas nuevas."
            ),
            chemicalTreatment = ChemicalTreatment(
                activeIngredient = "Azoxistrobina 25% SC o Mancozeb 80% WP",
                dosagePer20L = "20 ml de Azoxistrobina ó 50 g de Mancozeb por mochila de 20 L",
                frequency = "Cada 10 a 14 días alternando modo de acción (máximo 2 aplicaciones seguidas)",
                preHarvestDays = 14,
                safetyNote = "No mezclar con productos fuertemente alcalinos y respetar el tiempo de carencia de 14 días."
            ),
            culturalPrevention = listOf(
                "Regar por gravedad o goteo en la mañana para que el follaje seque antes de la noche.",
                "Usar semilla tratada con agua caliente (50 °C por 20 min) o semilla certificada libre del hongo.",
                "Evitar exceso de abono nitrogenado que produce follaje muy tierno y susceptible.",
                "Incorporar o compostar profundamente los rastrojos tras la cosecha."
            ),
            defaultCrossClimateAlert = "Precaución Aroma: heladas leves combinadas con rocío debilitan las puntas y facilitan el ingreso de Alternaria.",
            symptomFilterTags = listOf("Puntas secas quemadas")
        )
    )

    val municipalityClimates: Map<Municipality, MunicipalityClimate> = mapOf(
        Municipality.SICA_SICA to MunicipalityClimate(
            municipality = Municipality.SICA_SICA,
            currentTempC = 12,
            minNightTempC = -2,
            maxDayTempC = 15,
            windKmh = 24,
            rainProbabilityPct = 35,
            humidityPct = 58,
            cacheTimestamp = "Caché local · Actualizado 06:00 AM",
            activeCrossAlertForSpray = "Alerta Sica Sica (-2 °C esta madrugada): Se pronostican heladas nocturnas y viento moderado. Aplica bioinsumos antes de las 16:30 para evitar congelamiento foliar.",
            hazardAlerts = listOf(
                WeatherHazardAlert(
                    title = "Helada nocturna moderada (-2 °C)",
                    riskLevel = UrgencyLevel.ALTO,
                    windowLabel = "Madrugada 02:00 – 06:00",
                    detail = "Cielo despejado provocará descenso térmico brusco en pampas abiertas de Sica Sica."
                ),
                WeatherHazardAlert(
                    title = "Vientos fuertes de tarde (24 km/h)",
                    riskLevel = UrgencyLevel.MEDIO,
                    windowLabel = "13:00 – 17:30",
                    detail = "Evitar fumigación al mediodía; la deriva del viento desperdicia el bioinsumo."
                ),
                WeatherHazardAlert(
                    title = "Granizada aislada en serranía",
                    riskLevel = UrgencyLevel.MEDIO,
                    windowLabel = "Próximas 48 horas",
                    detail = "Formación convectiva sobre zonas altas; tener listos surcos de drenaje."
                )
            ),
            cropRecommendations = listOf(
                CropClimateAdvice(
                    cropName = "Quinua",
                    actionNow = "Realizar quema controlada de thola/paja húmeda en bordes a las 03:30 AM si la panoja está en grano lechoso."
                ),
                CropClimateAdvice(
                    cropName = "Papa",
                    actionNow = "Aplicar bioestimulante de algas o riego ligero al surco en la tarde para amortiguar la helada nocturna."
                ),
                CropClimateAdvice(
                    cropName = "Zanahoria",
                    actionNow = "Mantener humedad uniforme en cama de cultivo para proteger hombros de raíz del frío."
                )
            )
        ),
        Municipality.PATACAMAYA to MunicipalityClimate(
            municipality = Municipality.PATACAMAYA,
            currentTempC = 14,
            minNightTempC = 1,
            maxDayTempC = 17,
            windKmh = 18,
            rainProbabilityPct = 65,
            humidityPct = 76,
            cacheTimestamp = "Caché local · Actualizado 06:00 AM",
            activeCrossAlertForSpray = "Alerta Patacamaya (65% prob. lluvia y 76% humedad): Riesgo alto de Rancha y Mildiu en 48h. Aplica hoy con adherente y limpia zanjas de drenaje.",
            hazardAlerts = listOf(
                WeatherHazardAlert(
                    title = "Lluvias vespertinas y alta humedad (76%)",
                    riskLevel = UrgencyLevel.ALTO,
                    windowLabel = "Hoy 16:00 – 21:00",
                    detail = "Condiciones ideales para esporulación de Phytophthora (Rancha) y Mildiu en quinua."
                ),
                WeatherHazardAlert(
                    title = "Riesgo leve de granizo en planicie",
                    riskLevel = UrgencyLevel.MEDIO,
                    windowLabel = "Mañana tarde",
                    detail = "Nubosidad densa ingresando desde el noreste hacia Patacamaya."
                ),
                WeatherHazardAlert(
                    title = "Helada baja (+1 °C)",
                    riskLevel = UrgencyLevel.BAJO,
                    windowLabel = "Madrugada",
                    detail = "La cobertura nubosa protege parcialmente de heladas blancas severas."
                )
            ),
            cropRecommendations = listOf(
                CropClimateAdvice(
                    cropName = "Quinua",
                    actionNow = "Revisar envés de hojas bajeras por Mildiu y asegurar desagües para evitar encharcamiento."
                ),
                CropClimateAdvice(
                    cropName = "Papa",
                    actionNow = "Reforzar aporque en surcos con pendiente y aplicar Caldo Bordelés preventivo antes de la lluvia."
                ),
                CropClimateAdvice(
                    cropName = "Zanahoria",
                    actionNow = "Suspender riego hoy y vigilar aparición de manchas por Alternaria tras la lluvia."
                )
            )
        ),
        Municipality.AYO_AYO to MunicipalityClimate(
            municipality = Municipality.AYO_AYO,
            currentTempC = 13,
            minNightTempC = 0,
            maxDayTempC = 16,
            windKmh = 31,
            rainProbabilityPct = 20,
            humidityPct = 45,
            cacheTimestamp = "Caché local · Actualizado 06:00 AM",
            activeCrossAlertForSpray = "Alerta Ayo Ayo (Vientos de 31 km/h y baja humedad): Riesgo de sequía superficial y deriva de aspersión. Fumiga solo al amanecer (06:00–08:30).",
            hazardAlerts = listOf(
                WeatherHazardAlert(
                    title = "Vientos fuertes en pampa (31 km/h)",
                    riskLevel = UrgencyLevel.ALTO,
                    windowLabel = "11:00 – 18:00",
                    detail = "Ráfagas erosionan suelos sueltos y tumban plantas altas de quinua sin aporque."
                ),
                WeatherHazardAlert(
                    title = "Déficit hídrico / veranillo (20% lluvia)",
                    riskLevel = UrgencyLevel.MEDIO,
                    windowLabel = "Próximos 5 días",
                    detail = "Estrés hídrico favorece reproducción rápida de polilla Kcona kcona."
                ),
                WeatherHazardAlert(
                    title = "Helada en borde de río (0 °C)",
                    riskLevel = UrgencyLevel.MEDIO,
                    windowLabel = "04:00 – 06:00",
                    detail = "Microcuencas bajas pueden presentar escarcha ligera al amanecer."
                )
            ),
            cropRecommendations = listOf(
                CropClimateAdvice(
                    cropName = "Quinua",
                    actionNow = "Aporcar ligeramente la base para evitar acame por viento y monitorear panojas por Kcona kcona."
                ),
                CropClimateAdvice(
                    cropName = "Papa",
                    actionNow = "Programar riego de auxilio nocturno si el suelo franco-arenoso está agrietado."
                ),
                CropClimateAdvice(
                    cropName = "Zanahoria",
                    actionNow = "Regar temprano en la mañana para evitar deshidratación de plántulas por viento."
                )
            )
        ),
        Municipality.UMALA to MunicipalityClimate(
            municipality = Municipality.UMALA,
            currentTempC = 11,
            minNightTempC = -4,
            maxDayTempC = 14,
            windKmh = 20,
            rainProbabilityPct = 25,
            humidityPct = 52,
            cacheTimestamp = "Caché local · Actualizado 06:00 AM",
            activeCrossAlertForSpray = "Alerta Umala (-4 °C helada severa esta noche): No aplicar químicos foliares hoy. Prioriza protección térmica con humo, riego o cobertura.",
            hazardAlerts = listOf(
                WeatherHazardAlert(
                    title = "Helada negra severa (-4 °C)",
                    riskLevel = UrgencyLevel.ALTO,
                    windowLabel = "01:00 – 06:30",
                    detail = "Descenso térmico crítico en cabeceras de Umala; alto riesgo de daño celular en brotes."
                ),
                WeatherHazardAlert(
                    title = "Viento frío descendente (20 km/h)",
                    riskLevel = UrgencyLevel.MEDIO,
                    windowLabel = "15:00 – 19:00",
                    detail = "Aumenta la sensación de enfriamiento en parcelas sin barreras de thola."
                ),
                WeatherHazardAlert(
                    title = "Baja probabilidad de granizo",
                    riskLevel = UrgencyLevel.BAJO,
                    windowLabel = "Próximas 24h",
                    detail = "Atmósfera seca y despejada reduce riesgo de tormenta convectiva."
                )
            ),
            cropRecommendations = listOf(
                CropClimateAdvice(
                    cropName = "Quinua",
                    actionNow = "Activar fogatas con bosta seca y paja húmeda en dirección del viento a partir de las 02:30 AM."
                ),
                CropClimateAdvice(
                    cropName = "Papa",
                    actionNow = "Si existen parcelas en emergencia temprana, cubrir brotes con tierra suelta o paja (chala)."
                ),
                CropClimateAdvice(
                    cropName = "Zanahoria",
                    actionNow = "Aplicar riego por surco antes de las 17:00 para liberar calor latente durante la madrugada."
                )
            )
        )
    )

    val nasaSoilProfiles: Map<Municipality, NasaSoilProfile> = mapOf(
        Municipality.SICA_SICA to NasaSoilProfile(
            municipality = Municipality.SICA_SICA,
            ndviValue = 0.42f,
            ndviStatus = "Vigor moderado (estrés frío)",
            soilMoisturePct = 28,
            surfaceTempC = 17,
            organicMatterPct = 1.6f,
            salinityStatus = "Salinidad ligera (CE 1.4 dS/m)",
            erosionRisk = UrgencyLevel.MEDIO,
            satelliteSourceNote = "Caché satelital NASA POWER · MODIS NDVI 250m · SMAP Soil Moisture",
            soilDiagnosisSummary = "Suelos franco-arcillosos de ladera con contenido medio-bajo de materia orgánica (1.6%) y pérdida de humedad por pendiente y viento. Requiere retención de agua y cobertura viva.",
            conservationPlan = buildAndeanConservationPlan("laderas y pampas altas de Sica Sica")
        ),
        Municipality.PATACAMAYA to NasaSoilProfile(
            municipality = Municipality.PATACAMAYA,
            ndviValue = 0.56f,
            ndviStatus = "Vigor saludable en crecimiento",
            soilMoisturePct = 44,
            surfaceTempC = 19,
            organicMatterPct = 2.1f,
            salinityStatus = "Moderada en planicies bajas (CE 2.2 dS/m)",
            erosionRisk = UrgencyLevel.BAJO,
            satelliteSourceNote = "Caché satelital NASA POWER · MODIS NDVI 250m · SMAP Soil Moisture",
            soilDiagnosisSummary = "Buen vigor vegetativo y humedad adecuada (44%), pero con acumulación puntual de sales en suelos planos tras riegos sucesivos. Priorizar lavado suave y abonos orgánicos.",
            conservationPlan = buildAndeanConservationPlan("planicies agrícolas de Patacamaya")
        ),
        Municipality.AYO_AYO to NasaSoilProfile(
            municipality = Municipality.AYO_AYO,
            ndviValue = 0.31f,
            ndviStatus = "Vigor bajo (estrés hídrico)",
            soilMoisturePct = 19,
            surfaceTempC = 22,
            organicMatterPct = 1.2f,
            salinityStatus = "Baja a moderada (CE 1.6 dS/m)",
            erosionRisk = UrgencyLevel.ALTO,
            satelliteSourceNote = "Caché satelital NASA POWER · MODIS NDVI 250m · SMAP Soil Moisture",
            soilDiagnosisSummary = "Suelo franco-arenoso expuesto a erosión eólica intensa, baja humedad superficial (19%) y déficit crítico de materia orgánica (1.2%). Urgente instalar barreras vivas e incorporar guano.",
            conservationPlan = buildAndeanConservationPlan("pampas ventosas de Ayo Ayo")
        ),
        Municipality.UMALA to NasaSoilProfile(
            municipality = Municipality.UMALA,
            ndviValue = 0.37f,
            ndviStatus = "Vigor bajo-medio (heladas)",
            soilMoisturePct = 25,
            surfaceTempC = 15,
            organicMatterPct = 1.8f,
            salinityStatus = "Normal (CE 1.1 dS/m)",
            erosionRisk = UrgencyLevel.MEDIO,
            satelliteSourceNote = "Caché satelital NASA POWER · MODIS NDVI 250m · SMAP Soil Moisture",
            soilDiagnosisSummary = "Suelos de cabecera con buena estructura inicial pero enfriamiento rápido nocturno y escorrentía en lluvias intensas. Necesita zanjas de infiltración y biol enriquecido.",
            conservationPlan = buildAndeanConservationPlan("cabeceras de microcuenca en Umala")
        )
    )

    private fun buildAndeanConservationPlan(zoneContext: String): List<ConservationPractice> = listOf(
        ConservationPractice(
            title = "Abonos Orgánicos (Estiércol Fermentado y Biol)",
            categoryBadge = "Fertilidad del Suelo",
            description = "Recupera la vida microbiana y retiene hasta 3 veces más humedad en $zoneContext.",
            localMaterials = "Guano de ovino/llama descompuesto (3–5 t/ha), ceniza de thola y biol en bidón de 200 L.",
            implementationSteps = listOf(
                "Fermentar el estiércol en pila cubierta con plástico por 60 días antes de incorporar al surco.",
                "Aplicar biol diluido al 20% (4 L de biol en 16 L de agua) cada 15 días en etapas de crecimiento.",
                "Evitar usar guano fresco directo para no quemar raíces ni atraer gusano blanco."
            )
        ),
        ConservationPractice(
            title = "Barreras Vivas contra Viento y Helada (Thola e Ichu)",
            categoryBadge = "Protección Eólica",
            description = "Frena ráfagas de viento del Altiplano, evita erosión de suelo fértil y crea microclima más tibio.",
            localMaterials = "Matas trasplantadas de thola (Parastrephia), ichu (paja brava) y queñua/kishwara en linderos.",
            implementationSteps = listOf(
                "Plantar franjas perpendiculares al viento dominante cada 25 a 30 metros.",
                "Dejar franjas de paja brava de 60 cm de ancho sin roturar entre parcelas vecinas.",
                "Mantener la cobertura vegetal viva todo el año para albergar insectos benéficos."
            )
        ),
        ConservationPractice(
            title = "Rotación de Cultivos Andinos (Aynoka Planificada)",
            categoryBadge = "Corte de Plagas",
            description = "Corta el ciclo de vida de Gorgojo, Kcona kcona y hongos del suelo sin gastar en químicos.",
            localMaterials = "Secuencia sugerida: Papa -> Quinua -> Haba o Tarwi -> Cebada / Descanso.",
            implementationSteps = listOf(
                "Año 1: Papa con incorporación fuerte de estiércol fermentado.",
                "Año 2: Quinua aprovechando el nutriente residual de la papa.",
                "Año 3: Leguminosa andina (haba, arveja o tarwi) para fijar nitrógeno natural en el suelo."
            )
        ),
        ConservationPractice(
            title = "Zanjas de Infiltración y Surcos en Contorno",
            categoryBadge = "Cosecha de Agua",
            description = "Captura el agua de las lluvias cortas e intensas evitando cárcavas y lavado de nutrientes.",
            localMaterials = "Chontilla/pico, nivel en 'A' de madera y piedra local para taludes.",
            implementationSteps = listOf(
                "Trazar curvas a nivel con nivel en 'A' siguiendo el contorno de la ladera.",
                "Excavar zanjas de 40 cm de ancho x 40 cm de profundidad con tabiques cada 5 metros.",
                "Reforzar el borde inferior con champa o ichu para estabilizar la tierra."
            )
        )
    )

    fun findReferenceResByKey(key: String?): Int? = when (key) {
        "img_quinua_ref" -> R.drawable.img_quinua_ref
        "img_papa_ref" -> R.drawable.img_papa_ref
        "img_zanahoria_ref" -> R.drawable.img_zanahoria_ref
        else -> null
    }

    fun getReferenceResKey(cropType: CropType): String? = when (cropType) {
        CropType.QUINUA -> "img_quinua_ref"
        CropType.PAPA -> "img_papa_ref"
        CropType.ZANAHORIA -> "img_zanahoria_ref"
        CropType.OTRO -> null
    }
}
