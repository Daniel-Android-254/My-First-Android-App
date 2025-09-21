package com.example.breathwatch.ui.screens.extras

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.breathwatch.data.remote.model.extras.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun ExtrasScreen(
    viewModel: ExtrasViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val pullRefreshState = rememberPullRefreshState(
        refreshing = uiState.isLoading,
        onRefresh = { viewModel.refreshAllContent() }
    )

    LaunchedEffect(Unit) {
        viewModel.loadInitialContent()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Extras",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            // Error handling
            uiState.error?.let { error ->
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Error loading content",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = error,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }

            // Available APIs Section
            item {
                Text(
                    text = "Available APIs",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            items(extraApiItemsFixed) { apiItem ->
                ApiItemCard(
                    apiItem = apiItem,
                    isLoading = uiState.isLoading,
                    onRefresh = { viewModel.refreshApiContent(apiItem.id) }
                )
            }

            // Cat Facts
            uiState.catFact?.let { catFact ->
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "🐱 Cat Fact",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                                IconButton(
                                    onClick = { viewModel.refreshApiContent("catfacts") }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Refresh cat fact",
                                        tint = MaterialTheme.colorScheme.onTertiaryContainer
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = catFact.fact,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                    }
                }
            }

            // Dog Image
            uiState.dogImage?.let { dogImage ->
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("🐶 Random Dog", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            AsyncImage(
                                model = dogImage.message,
                                contentDescription = "Random Dog Image",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }

            // Trivia Question
            uiState.triviaQuestion?.let { trivia ->
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("❓ Trivia Question", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Category: ${trivia.category}", style = MaterialTheme.typography.bodySmall)
                            Text(trivia.question, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }

            // Public Holidays
            uiState.publicHolidays?.let { holidays ->
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("🎉 Public Holidays (US, 2024)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            holidays.take(5).forEach { holiday ->
                                Text("${holiday.date}: ${holiday.name}")
                            }
                        }
                    }
                }
            }

            // Universities
            uiState.universities?.let { universities ->
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("🎓 Universities (USA)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            universities.take(5).forEach { uni ->
                                Text(uni.name)
                            }
                        }
                    }
                }
            }

            // Book
            uiState.book?.let { book ->
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("📚 Book Recommendation", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(book.volumeInfo.title, fontWeight = FontWeight.Bold)
                            Text("by ${book.volumeInfo.authors.joinToString()}", style = MaterialTheme.typography.bodySmall)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(book.volumeInfo.description, maxLines = 4)
                        }
                    }
                }
            }

            // Bitcoin Price
            uiState.bitcoinPrice?.let { price ->
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("💰 Bitcoin Price", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("USD: ${price.bpi.uSD.rate}")
                            Text("GBP: ${price.bpi.gBP.rate}")
                            Text("EUR: ${price.bpi.eUR.rate}")
                            Text("Last updated: ${price.time.updated}", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            // Space Body
            uiState.spaceBody?.let { body ->
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("🪐 Space Body: ${body.englishName}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Type: ${body.bodyType}")
                            Text("Gravity: ${body.gravity} m/s²")
                            Text("Discovered by: ${body.discoveredBy} on ${body.discoveryDate}")
                        }
                    }
                }
            }

            // Placeholder for future API integrations
            if (uiState.catFact == null && uiState.dogImage == null && uiState.triviaQuestion == null && uiState.publicHolidays == null && uiState.universities == null && uiState.book == null && uiState.bitcoinPrice == null && uiState.spaceBody == null && !uiState.isLoading && uiState.error == null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "More content coming soon!",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Pull down to refresh",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        PullRefreshIndicator(
            refreshing = uiState.isLoading,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiItemCard(
    apiItem: ExtraApiItem,
    isLoading: Boolean,
    onRefresh: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onRefresh
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = apiItem.icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = apiItem.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = apiItem.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Tap to load",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

data class ExtraApiItem(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector
)

// Fixed version with available icons only
val extraApiItemsFixed = listOf(
    ExtraApiItem(
        id = "catfacts",
        title = "Cat Facts",
        description = "Random interesting cat facts",
        icon = Icons.Default.Pets
    ),
    ExtraApiItem(
        id = "dogfacts",
        title = "Dog Images",
        description = "Random dog breed images",
        icon = Icons.Default.Pets
    ),
    ExtraApiItem(
        id = "trivia",
        title = "Trivia Questions",
        description = "Test your knowledge",
        icon = Icons.Default.Help
    ),
    ExtraApiItem(
        id = "holidays",
        title = "Public Holidays",
        description = "Upcoming holidays",
        icon = Icons.Default.Event
    ),
    ExtraApiItem(
        id = "universities",
        title = "Universities",
        description = "Educational institutions",
        icon = Icons.Default.School
    ),
    ExtraApiItem(
        id = "books",
        title = "Books",
        description = "Science book recommendations",
        icon = Icons.Default.Book
    ),
    ExtraApiItem(
        id = "bitcoin",
        title = "Bitcoin Price",
        description = "Current cryptocurrency rates",
        icon = Icons.Default.AttachMoney
    ),
    ExtraApiItem(
        id = "space",
        title = "Space Bodies",
        description = "Solar system information",
        icon = Icons.Default.Public
    )
)
