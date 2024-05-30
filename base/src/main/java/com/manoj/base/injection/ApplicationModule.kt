package com.manoj.base.injection


import android.app.Application
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.manoj.base.BuildConfig
import com.manoj.base.network.api.BaseApi
import com.manoj.base.network.helper.Constants.Companion.MQTT_HOST
import com.manoj.base.network.helper.MqttClientHelper
import com.manoj.base.network.helper.NetworkMonitor
import com.manoj.base.presentation.service.MqttAndroidClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.eclipse.paho.client.mqttv3.MqttClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {

    @Provides
    @Singleton
    fun providesOkHttp(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.apply { loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY }
        return OkHttpClient.Builder().addInterceptor(loggingInterceptor)
            .callTimeout(60, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS).build()
    }

    @Provides
    @Singleton
    fun providesRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder().baseUrl(BuildConfig.BASE_URL).client(okHttpClient).addConverterFactory(
            GsonConverterFactory.create()
        ).addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .build()

    @Provides
    @Singleton
    fun providesApiService(retrofit: Retrofit): BaseApi = retrofit.create(BaseApi::class.java)

    @Provides
    @Singleton
    fun provideSharedPref(application: Application): SharedPreferences {
        return application.getSharedPreferences(application.packageName, MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideNetworkMonitor(
        @ApplicationContext context: Context
    ): NetworkMonitor = NetworkMonitor(context)

    @Provides
    @Singleton
    fun provideMqttClientHelper(
        @ApplicationContext context: Context, mqttAndroidClient: MqttAndroidClient
    ): MqttClientHelper = MqttClientHelper(context, mqttAndroidClient)

    @Provides
    @Singleton
    fun provideMqttClient(
        @ApplicationContext context: Context
    ): MqttAndroidClient {
         val clientId: String = MqttClient.generateClientId()
        return MqttAndroidClient(context, MQTT_HOST, clientId)
    }
}