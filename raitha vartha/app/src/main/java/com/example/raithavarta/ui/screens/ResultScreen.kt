package com.example.raithavarta.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.raithavarta.domain.model.DiseaseInsight

@Composable
fun ResultScreen(
    insight: DiseaseInsight,
    onBack: () -> Unit
) {
    var tab by remember { mutableIntStateOf(0) }
    val titles = listOf("English", "ಕನ್ನಡ", "हिन्दी")

    Column(
        Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text("Analysis result", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(12.dp))
        TabRow(selectedTabIndex = tab) {
            titles.forEachIndexed { index, title ->
                Tab(
                    selected = tab == index,
                    onClick = { tab = index },
                    text = { Text(title) }
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        when (tab) {
            0 -> ResultLanguageCard(
                title = "Disease",
                headline = insight.diseaseEn,
                description = insight.descriptionEn,
                solution = insight.solutionEn
            )
            1 -> ResultLanguageCard(
                title = "ರೋಗ",
                headline = insight.diseaseKn,
                description = insight.descriptionKn,
                solution = insight.solutionKn
            )
            else -> ResultLanguageCard(
                title = "रोग",
                headline = insight.diseaseHi,
                description = insight.descriptionHi,
                solution = insight.solutionHi
            )
        }
        Spacer(Modifier.height(16.dp))
        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Back")
        }
    }
}

@Composable
private fun ResultLanguageCard(title: String, headline: String, description: String, solution: String) {
    Card(
        Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            Text(headline, style = MaterialTheme.typography.titleLarge)
            Text(description, style = MaterialTheme.typography.bodyLarge)
            Text("Suggested care", style = MaterialTheme.typography.titleMedium)
            Text(solution, style = MaterialTheme.typography.bodyLarge)
        }
    }
}
