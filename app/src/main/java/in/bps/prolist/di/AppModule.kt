package `in`.bps.prolist.di

import `in`.bps.prolist.api.ApiService
import `in`.bps.prolist.helper.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideBaseUrl() = Constants.baseUrl;

    @Provides
    @Singleton
    fun providerRetrofitInstance(baseUrl: String): ApiService {
        val logger = HttpLoggingInterceptor()
        logger.level = (HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
        client.connectTimeout(100, TimeUnit.SECONDS).readTimeout(100, TimeUnit.SECONDS)
        if(Constants.env == "dev") client.addInterceptor(logger)
        return Retrofit.Builder().baseUrl(baseUrl).client(client.build()).addConverterFactory(GsonConverterFactory.create()).build().create(ApiService::class.java)
    }
}