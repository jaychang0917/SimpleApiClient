package com.jaychang.sac

import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jaychang.sac.calladapter.ObserveOnCallAdapterFactory
import com.jaychang.sac.converter.ImageConverterFactory
import com.jaychang.sac.converter.WrappedResponseConverterFactory
import com.jaychang.sac.interceptor.HeaderInterceptor
import com.jaychang.sac.interceptor.ParameterInterceptor
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.exceptions.CompositeException
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiManager {

  lateinit var gson: Gson
  lateinit var apiErrorClass: Class<*>

  fun init(config: ApiClientConfig,
           apiErrorClass: Class<*>): Retrofit {
    this.gson = createGson()
    this.apiErrorClass = apiErrorClass
    RxJavaPlugins.setErrorHandler {
      when (it){
        is CompositeException -> {
          config.errorHandler?.invoke(it.exceptions[1])
        }
      }
    }
    return createRetrofit(config, createOkHttpClient(config))
  }

  private fun createOkHttpClient(config: ApiClientConfig): OkHttpClient {
    val builder = OkHttpClient.Builder()

    if (BuildConfig.DEBUG) {
      builder.addNetworkInterceptor(StethoInterceptor())
    }

    config.defaultParameters?.let {
      builder.addInterceptor(ParameterInterceptor(it))
    }

    config.defaultHeaders?.let {
      builder.addInterceptor(HeaderInterceptor(it))
    }

    builder
      .connectTimeout(config.connectTimeout, TimeUnit.MILLISECONDS)
      .readTimeout(config.readTimeout, TimeUnit.MILLISECONDS)
      .writeTimeout(config.writeTimeout, TimeUnit.MILLISECONDS)

    return builder.build()
  }

  private fun createGson(): Gson {
    return GsonBuilder().setPrettyPrinting().create()
  }

  private fun createRetrofit(config: ApiClientConfig, client: OkHttpClient): Retrofit {
    return Retrofit.Builder()
      .baseUrl(config.baseUrl).client(client)
      .addConverterFactory(WrappedResponseConverterFactory.create())
      .addConverterFactory(ImageConverterFactory.create())
      .addConverterFactory(GsonConverterFactory.create(gson))
      .addCallAdapterFactory(ObserveOnCallAdapterFactory.create(AndroidSchedulers.mainThread()))
      .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
      .build()
  }

}