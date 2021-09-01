package io.stipop.refactor.present.di.modules

import dagger.Module
import dagger.Provides
import io.stipop.refactor.data.services.*
import io.stipop.refactor.domain.services.MyStickersServiceProtocol
import io.stipop.refactor.domain.services.SearchServiceProtocol
import io.stipop.refactor.domain.services.StickerStoreServiceProtocol
import okhttp3.OkHttp
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class ServiceModule {

    private val _httpClient = with(OkHttpClient.Builder()) {
        addInterceptor(HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        })
        build()
    }

    private val _apiClient = Retrofit.Builder()
        .baseUrl("https://messenger.stipop.io/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(_httpClient)
        .build()

    @Singleton
    @Provides
    fun provideMyStickersService(): MyStickersServiceProtocol {
        return _apiClient
            .create(MyStickersRestService::class.java)
    }

    @Singleton
    @Provides
    fun provideSearchService(): SearchServiceProtocol {
        return _apiClient
            .create(SearchRestService::class.java)
    }

    @Singleton
    @Provides
    fun provideStickerStoreService(): StickerStoreServiceProtocol {
        return _apiClient
            .create(StickerStoreRestService::class.java)
    }
}
