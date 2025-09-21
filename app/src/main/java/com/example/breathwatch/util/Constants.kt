package com.example.breathwatch.util

object Constants {
    // API Endpoints
    const val WEATHER_BASE_URL = "https://wttr.in/"
    const val AIR_QUALITY_BASE_URL = "https://api.openaq.org/v2/"
    const val CAT_FACT_BASE_URL = "https://catfact.ninja/"
    const val DOG_IMAGE_BASE_URL = "https://dog.ceo/api/"
    const val TRIVIA_BASE_URL = "https://the-trivia-api.com/api/"
    const val PUBLIC_HOLIDAY_BASE_URL = "https://date.nager.at/api/v3/"
    const val UNIVERSITY_BASE_URL = "http://universities.hipolabs.com/"
    const val BOOK_BASE_URL = "https://www.googleapis.com/books/v1/"
    const val BITCOIN_PRICE_BASE_URL = "https://api.coindesk.com/v1/bpi/"
    const val SPACE_BODY_BASE_URL = "https://api.le-systeme-solaire.net/rest/"
    
    // DataStore Keys
    object PreferencesKeys {
        const val USER_PREFERENCES = "user_preferences"
        const val IS_ONBOARDING_COMPLETE = "is_onboarding_complete"
        const val USER_LOCATION_LAT = "user_location_lat"
        const val USER_LOCATION_LON = "user_location_lon"
        const val USER_LOCATION_NAME = "user_location_name"
        const val AQI_THRESHOLD = "aqi_threshold"
        const val NOTIFICATIONS_ENABLED = "notifications_enabled"
        const val THEME_MODE = "theme_mode"
    }
    
    // Default Values
    const val DEFAULT_AQI_THRESHOLD = 35 // PM2.5 µg/m³
    const val DEFAULT_REFRESH_INTERVAL_HOURS = 3L
    const val CACHE_EXPIRY_HOURS = 6L
    
    // WorkManager
    const val SYNC_WORKER_TAG = "air_quality_sync_worker"
    
    // Room Database
    const val DATABASE_NAME = "breathwatch_db"
    
    // Notifications
    const val NOTIFICATION_CHANNEL_ID = "air_quality_alerts"
    const val NOTIFICATION_ID = 1
    
    // Location
    const val DEFAULT_LOCATION_UPDATE_INTERVAL = 30_000L // 30 seconds
    const val FASTEST_LOCATION_UPDATE_INTERVAL = 10_000L // 10 seconds
    const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    
    // Request Codes
    const val LOCATION_PERMISSION_REQUEST = 1
    
    // Intent Extras
    const val EXTRA_LOCATION_LAT = "extra_location_lat"
    const val EXTRA_LOCATION_LON = "extra_location_lon"
    const val EXTRA_LOCATION_NAME = "extra_location_name"
    
    // AQI Categories
    const val AQI_GOOD = 0
    const val AQI_MODERATE = 1
    const val AQI_UNHEALTHY_SENSITIVE = 2
    const val AQI_UNHEALTHY = 3
    const val AQI_VERY_UNHEALTHY = 4
    const val AQI_HAZARDOUS = 5
}
