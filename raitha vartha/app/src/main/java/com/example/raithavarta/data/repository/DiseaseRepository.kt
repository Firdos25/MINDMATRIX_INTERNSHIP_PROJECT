package com.example.raithavarta.data.repository

import com.example.raithavarta.domain.model.DiseaseInsight
import kotlin.math.abs

/**
 * Mock "AI" disease detection with hard-coded EN / KN / HI strings.
 */
class DiseaseRepository {

    fun analyze(plantName: String): DiseaseInsight {
        val key = plantName.trim().lowercase()
        val variant = abs(key.hashCode()) % 3

        val templates = listOf(
            Triple(
                "Leaf Spot / Fungal Infection",
                "ಎಲೆ ಚುಕ್ಕೆ / ಬೂಷ್ಟು ಸೋಂಕು",
                "पत्ती धब्बा / कवक संक्रमण"
            ),
            Triple(
                "Early Blight",
                "ಆರಂಭಿಕ ಬೂಷ್ಟು ರೋಗ",
                "प्रारंभिक झुलसा रोग"
            ),
            Triple(
                "Powdery Mildew",
                "ಬೂದು ರೋಗ",
                "पाउडरी मिल्ड्यू"
            )
        )

        val (en, kn, hi) = templates[variant]

        val plantLabel = plantName.trim().ifBlank { "Crop" }

        return DiseaseInsight(
            diseaseEn = en,
            diseaseKn = kn,
            diseaseHi = hi,
            descriptionEn = "Mock analysis for $plantLabel: leaf surface shows stress patterns typical of fungal activity. Confidence is simulated for demo.",
            descriptionKn = "$plantLabel ಗೆ ಪ್ರದರ್ಶನಾತ್ಮಕ ವಿಶ್ಲೇಷಣೆ: ಎಲೆಯ ಮೇಲ್ಮೈಯಲ್ಲಿ ಬೂಷ್ಟು ಚಟುವಟಿಕೆಗೆ ಸಾಮಾನ್ಯವಾದ ಒತ್ತಡದ ಮಾದರಿಗಳು ಕಾಣುತ್ತವೆ.",
            descriptionHi = "$plantLabel के लिए नमूना विश्लेषण: पत्ती की सतह पर कवकीय गतिविधि के लक्षण दिख सकते हैं (डेमो)।",
            solutionEn = "Remove infected leaves, improve spacing/airflow, avoid evening wetting, and consult your local agriculture officer before spraying fungicide.",
            solutionKn = "ಸೋಂಕಿತ ಎಲೆಗಳನ್ನು ತೆಗೆದುಹಾಕಿ, ಗಾಳಿ ಸಂಚಾರ ಸುಧಾರಿಸಿ, ಸಂಜೆ ನೀರನ್ನು ತಪ್ಪಿಸಿ, ಸ್ಪ್ರೇ ಮಾಡುವ ಮೊದಲು ಸ್ಥಳೀಯ ಕೃಷಿ ಅಧಿಕಾರಿಯನ್ನು ಸಂಪರ್ಕಿಸಿ.",
            solutionHi = "संक्रमित पत्तियाँ हटाएँ, हवा का प्रवाह बेहतर करें, शाम को पानी देने से बचें, और फंगनाशक से पहले कृषि अधिकारी से परामर्श करें।"
        )
    }
}
