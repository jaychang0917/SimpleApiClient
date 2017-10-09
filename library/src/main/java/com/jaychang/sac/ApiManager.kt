package com.jaychang.sac

import android.annotation.SuppressLint
import android.content.Context
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.jaychang.sac.calladapter.MockResponseAdapterFactory
import com.jaychang.sac.calladapter.ObserveOnCallAdapterFactory
import com.jaychang.sac.converter.ImageConverterFactory
import com.jaychang.sac.converter.WrappedResponseConverterFactory
import com.jaychang.sac.interceptor.HeaderInterceptor
import com.jaychang.sac.interceptor.ParameterInterceptor
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.exceptions.CompositeException
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

@SuppressLint("StaticFieldLeak")
object ApiManager {

  internal var errorClass: KClass<out SimpleApiError>? = null
  internal lateinit var context: Context
  internal lateinit var jsonParser: JsonParser

  fun init(config: SimpleApiClient.Config): Retrofit {
    this.jsonParser = config.jsonParser
    this.errorClass = config.errorClass

    RxJavaPlugins.setErrorHandler {
      when (it){
        is CompositeException -> {
          if (it.exceptions.size >= 2) {
            config.errorHandler?.invoke(it.exceptions[1])
          } else {
            config.errorHandler?.invoke(it.exceptions[0])
          }
        }
      }
    }

    return createRetrofit(config, createOkHttpClient(config))
  }

  private fun createOkHttpClient(config: SimpleApiClient.Config): OkHttpClient {
    val builder = OkHttpClient.Builder()

    if (config.isStethoEnabled) {
      builder.addNetworkInterceptor(StethoInterceptor())
    }

    val httpLoggingInterceptor = HttpLoggingInterceptor()
    httpLoggingInterceptor.level = config.logLevel
    builder.addInterceptor(httpLoggingInterceptor)

    config.defaultParameters?.let {
      builder.addInterceptor(ParameterInterceptor(it))
    }

    config.defaultHeaders?.let {
      builder.addInterceptor(HeaderInterceptor(it))
    }

    config.certificatePins?.let {
      if (it.isEmpty()) {
        return@let
      }
      val pinBuilder = CertificatePinner.Builder()
      for (pin in it) {
        pin.sha1PublicKeyHash?.let {
          pinBuilder.add(pin.hostname, pin.toString())
        }
        pin.sha256PublicKeyHash?.let {
          pinBuilder.add(pin.hostname, pin.toString())
        }
      }
      builder.certificatePinner(pinBuilder.build())
    }

    builder
      .connectTimeout(config.connectTimeout, TimeUnit.MILLISECONDS)
      .readTimeout(config.readTimeout, TimeUnit.MILLISECONDS)
      .writeTimeout(config.writeTimeout, TimeUnit.MILLISECONDS)

    return builder.build()
  }

  private fun createRetrofit(config: SimpleApiClient.Config, client: OkHttpClient): Retrofit {
    return Retrofit.Builder()
      .baseUrl(config.baseUrl).client(client)
      .addConverterFactory(WrappedResponseConverterFactory.create())
      .addConverterFactory(ImageConverterFactory.create())
      .addConverterFactory(jsonParser.converterFactory())
      .addCallAdapterFactory(MockResponseAdapterFactory.create(config.isMockDataEnabled, context, jsonParser))
      .addCallAdapterFactory(ObserveOnCallAdapterFactory.create(AndroidSchedulers.mainThread()))
      .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
      .build()
  }

}