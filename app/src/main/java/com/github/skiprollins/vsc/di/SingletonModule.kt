package com.github.skiprollins.vsc.di

import android.content.Context
import com.github.skiprollins.vsc.BuildConfig
import com.github.skiprollins.vsc.network.InventoryApi
import com.github.skiprollins.vsc.network.InventoryService
import com.github.skiprollins.vsc.network.MockApiInterceptor
import com.github.skiprollins.vsc.ui.CartContract
import com.github.skiprollins.vsc.ui.CartViewModel
import com.github.skiprollins.vsc.util.SchedulerProvider
import com.github.skiprollins.vsc.util.SchedulerProviderImpl
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
class ApplicationModule(
    private val context: Context
) {
    @Provides
    @Singleton
    fun providesContext(): Context =
        context
}

@Module
class GsonModule {

    @Provides
    @Singleton
    fun providesGson(): Gson =
        GsonBuilder().create()

    @Provides
    @Singleton
    fun providesGsonConverter(gson: Gson): GsonConverterFactory =
        GsonConverterFactory.create(gson)
}

@Module
class NetworkModule {
    companion object {
        private const val MOCK_API = "MOCK_API"
    }

    @Provides
    @Singleton
    fun providesMockApiInterceptor(
        context: Context,
        gson: Gson
    ): MockApiInterceptor =
        MockApiInterceptor(context, gson)

    @Provides
    @Singleton
    @Named(MOCK_API)
    fun providesMockHttpClient(
        mockApiInterceptor: MockApiInterceptor
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(mockApiInterceptor)
            .build()

    @Provides
    @Singleton
    fun providesRetrofit(
        gson: GsonConverterFactory,
        @Named(MOCK_API) mockClient: OkHttpClient
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(if (BuildConfig.USE_MOCK_API) BuildConfig.MOCK_API else BuildConfig.MOCK_API) // Only mock api is available right now
            .client(if (BuildConfig.USE_MOCK_API) mockClient else mockClient) // Only mock api is available right now
            .addConverterFactory(gson)
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()

    @Provides
    @Singleton
    fun providesInventoryApi(
        retrofit: Retrofit
    ): InventoryApi =
        retrofit.create(InventoryApi::class.java)
}

@Module
class ServiceModule {
    
    @Provides
    @Singleton
    fun providesSchedulerProvider(): SchedulerProvider =
        SchedulerProviderImpl()

    @Provides
    @Singleton
    fun providesInventoryService(
        api: InventoryApi,
        schedulers: SchedulerProvider
    ): InventoryService =
        InventoryService(api, schedulers)
}

@Module
class ContractModule {

    @Provides
    @Singleton
    fun providesCartContract(
        service: InventoryService,
        schedulers: SchedulerProvider
    ): CartContract =
        CartViewModel(service, schedulers)
}