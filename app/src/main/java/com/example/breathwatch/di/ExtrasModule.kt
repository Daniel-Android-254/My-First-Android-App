package com.example.breathwatch.di

import com.example.breathwatch.data.remote.api.ExtrasApi
import com.example.breathwatch.data.repository.ExtrasRepositoryImpl
import com.example.breathwatch.domain.repository.ExtrasRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ExtrasModule {

    @Provides
    @Singleton
    @Named("extrasRetrofit")
    fun provideExtrasRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ExtrasApi.CAT_FACTS_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideExtrasApi(@Named("extrasRetrofit") retrofit: Retrofit): ExtrasApi {
        return retrofit.create(ExtrasApi::class.java)
    }

    @Provides
    @Singleton
    fun provideExtrasRepository(
        extrasApi: ExtrasApi,
        cacheStrategy: CacheStrategy,
        extrasDao: ExtrasDao
    ): ExtrasRepository {
        return ExtrasRepositoryImpl(extrasApi, cacheStrategy, extrasDao)
    }
}
