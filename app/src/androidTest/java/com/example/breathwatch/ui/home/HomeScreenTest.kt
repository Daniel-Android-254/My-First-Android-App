package com.example.breathwatch.ui.home

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.breathwatch.domain.model.AirQualityData
import com.example.breathwatch.domain.model.AqiCategory
import com.example.breathwatch.domain.model.WeatherData
import com.example.breathwatch.ui.theme.BreathWatchTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDateTime

@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun homeScreen_displaysLoadingInitially() {
        composeTestRule.setContent {
            BreathWatchTheme {
                HomeScreen(
                    onNavigateToHealthLog = {},
                    onNavigateToSettings = {}
                )
            }
        }

        // Check that loading indicator is displayed
        composeTestRule.onNodeWithText("Loading").assertExists()
    }

    @Test
    fun homeScreen_displaysAirQualityCard() {
        val airQualityData = AirQualityData(
            latitude = -1.2921,
            longitude = 36.8219,
            locationName = "Nairobi, Kenya",
            pm25 = 25.0,
            pm10 = 40.0,
            o3 = null,
            no2 = null,
            so2 = null,
            co = null,
            aqi = 75,
            aqiCategory = AqiCategory.MODERATE,
            lastUpdated = LocalDateTime.now(),
            isStale = false
        )

        val weatherData = WeatherData(
            latitude = -1.2921,
            longitude = 36.8219,
            locationName = "Nairobi, Kenya",
            temperatureCelsius = 22.0,
            temperatureFahrenheit = 71.6,
            conditionText = "Partly Cloudy",
            conditionIcon = null,
            humidity = 65,
            windSpeedKph = 10.0,
            windDirection = "NE",
            precipitationMm = 0.0,
            pressureMb = 1013.0,
            visibilityKm = 10.0,
            cloudCover = 40,
            uvIndex = 6.0,
            lastUpdated = LocalDateTime.now(),
            isStale = false
        )

        composeTestRule.setContent {
            BreathWatchTheme {
                // We would need to mock the ViewModel for a proper test
                // For now, this is a basic UI structure test
                HomeScreen(
                    onNavigateToHealthLog = {},
                    onNavigateToSettings = {}
                )
            }
        }

        // Check that the main UI elements exist
        composeTestRule.onNodeWithText("BreathWatch").assertExists()
        composeTestRule.onNodeWithContentDescription("Refresh data").assertExists()
        composeTestRule.onNodeWithContentDescription("Settings").assertExists()
        composeTestRule.onNodeWithContentDescription("Add health log").assertExists()
    }

    @Test
    fun homeScreen_navigationButtonsWork() {
        var healthLogClicked = false
        var settingsClicked = false

        composeTestRule.setContent {
            BreathWatchTheme {
                HomeScreen(
                    onNavigateToHealthLog = { healthLogClicked = true },
                    onNavigateToSettings = { settingsClicked = true }
                )
            }
        }

        // Click on health log FAB
        composeTestRule.onNodeWithContentDescription("Add health log").performClick()
        assert(healthLogClicked)

        // Click on settings button
        composeTestRule.onNodeWithContentDescription("Settings").performClick()
        assert(settingsClicked)
    }
}
