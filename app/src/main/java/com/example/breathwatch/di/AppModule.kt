package com.example.breathwatch.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.breathwatch.BuildConfig
import com.example.breathwatch.data.local.AppDatabase
import com.example.breathwatch.data.remote.api.AirQualityApi
import com.example.breathwatch.data.remote.api.WeatherApi
import com.example.breathwatch.data.remote.api.extras.*
import com.example.breathwatch.data.repository.AirQualityRepositoryImpl
import com.example.breathwatch.data.repository.ExtrasRepositoryImpl
import com.example.breathwatch.data.repository.HealthLogRepositoryImpl
import com.example.breathwatch.domain.repository.AirQualityRepository
import com.example.breathwatch.domain.repository.ExtrasRepository
import com.example.breathwatch.domain.repository.HealthLogRepository
import com.example.breathwatch.domain.repository.WeatherRepository
import com.example.breathwatch.util.Constants
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }
    
    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }
    
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request()
                    .newBuilder()
                    .addHeader("Accept", "application/json")
                    .build()
                chain.proceed(request)
            }
            .also { client ->
                if (BuildConfig.DEBUG) {
                    val logging = HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    }
                    client.addInterceptor(logging)
                }
            }
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    @Provides
    @Singleton
    fun provideWeatherApi(okHttpClient: OkHttpClient, moshi: Moshi): WeatherApi {
        return Retrofit.Builder()
            .baseUrl(Constants.WEATHER_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(WeatherApi::class.java)
    }
    
    @Provides
    @Singleton
    fun provideAirQualityApi(okHttpClient: OkHttpClient, moshi: Moshi): AirQualityApi {
        return Retrofit.Builder()
            .baseUrl(Constants.AIR_QUALITY_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(AirQualityApi::class.java)
    }
    
    @Provides
    @Singleton
    fun provideCatFactApi(okHttpClient: OkHttpClient, moshi: Moshi): CatFactApi {
        return Retrofit.Builder()
            .baseUrl(Constants.CAT_FACT_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(CatFactApi::class.java)
    }

    @Provides
    @Singleton
    fun provideDogImageApi(okHttpClient: OkHttpClient, moshi: Moshi): DogImageApi {
        return Retrofit.Builder()
            .baseUrl(Constants.DOG_IMAGE_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(DogImageApi::class.java)
    }

    @Provides
    @Singleton
    fun provideTriviaApi(okHttpClient: OkHttpClient, moshi: Moshi): TriviaApi {
        return Retrofit.Builder()
            .baseUrl(Constants.TRIVIA_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(TriviaApi::class.java)
    }

    @Provides
    @Singleton
    fun providePublicHolidayApi(okHttpClient: OkHttpClient, moshi: Moshi): PublicHolidayApi {
        return Retrofit.Builder()
            .baseUrl(Constants.PUBLIC_HOLIDAY_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(PublicHolidayApi::class.java)
    }

    @Provides
    @Singleton
    fun provideUniversityApi(okHttpClient: OkHttpClient, moshi: Moshi): UniversityApi {
        return Retrofit.Builder()
            .baseUrl(Constants.UNIVERSITY_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(UniversityApi::class.java)
    }

    @Provides
    @Singleton
    fun provideBookApi(okHttpClient: OkHttpClient, moshi: Moshi): BookApi {
        return Retrofit.Builder()
            .baseUrl(Constants.BOOK_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(BookApi::class.java)
    }

    @Provides
    @Singleton
    fun provideBitcoinPriceApi(okHttpClient: OkHttpClient, moshi: Moshi): BitcoinPriceApi {
        return Retrofit.Builder()
            .baseUrl(Constants.BITCOIN_PRICE_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(BitcoinPriceApi::class.java)
    }

    @Provides
    @Singleton
    fun provideSpaceBodyApi(okHttpClient: OkHttpClient, moshi: Moshi): SpaceBodyApi {
        return Retrofit.Builder()
            .baseUrl(Constants.SPACE_BODY_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(SpaceBodyApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }
    
    @Provides
    @Singleton
    fun provideWeatherRepository(
        weatherApi: WeatherApi,
        appDatabase: AppDatabase
    ): WeatherRepository {
        return WeatherRepositoryImpl(weatherApi, appDatabase.weatherDataDao())
    }
    
    @Provides
    @Singleton
    fun provideAirQualityRepository(
        airQualityApi: AirQualityApi,
        appDatabase: AppDatabase
    ): AirQualityRepository {
        return AirQualityRepositoryImpl(airQualityApi, appDatabase.airQualityDataDao())
    }
    
    @Provides
    @Singleton
    fun provideExtrasRepository(
        catFactApi: CatFactApi,
        dogImageApi: DogImageApi,
        triviaApi: TriviaApi,
        publicHolidayApi: PublicHolidayApi,
        universityApi: UniversityApi,
        bookApi: BookApi,
        bitcoinPriceApi: BitcoinPriceApi,
        spaceBodyApi: SpaceBodyApi
    ): ExtrasRepository {
        return ExtrasRepositoryImpl(
            catFactApi,
            dogImageApi,
            triviaApi,
            publicHolidayApi,
            universityApi,
            bookApi,
            bitcoinPriceApi,
            spaceBodyApi
        )
    }
    
    @Provides
    @Singleton
    fun provideHealthLogRepository(
        appDatabase: AppDatabase
    ): HealthLogRepository {
        return HealthLogRepositoryImpl(appDatabase.healthLogDao())
    }
}
