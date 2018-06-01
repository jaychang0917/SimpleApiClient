package com.jaychang.sac.calladapter

import android.content.Context
import com.google.gson.reflect.TypeToken
import com.jaychang.sac.JsonParser
import com.jaychang.sac.SimpleApiResult
import com.jaychang.sac.annotation.MockResponse
import com.jaychang.sac.annotation.Status.*
import com.jaychang.sac.annotation.WrappedResponse
import com.jaychang.sac.util.Utils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.ResponseBody
import retrofit2.*
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.net.UnknownHostException
import javax.net.ssl.SSLPeerUnverifiedException

internal class MockResponseAdapterFactory(private val isEnabled: Boolean, private val context: Context, private val jsonParser: JsonParser) : CallAdapter.Factory() {
  companion object {
    fun create(isEnabled: Boolean, context: Context, jsonParser: JsonParser): MockResponseAdapterFactory {
      return MockResponseAdapterFactory(isEnabled, context, jsonParser)
    }
  }

  override fun get(type: Type, annotations: Array<Annotation>, retrofit: Retrofit): CallAdapter<*, *>? {
    if (annotations.none { it is MockResponse } || !isEnabled) {
      return null
    }

    val mockResponse = annotations.find { it is MockResponse } as MockResponse
    val status = mockResponse.status

    if (status == SUCCESS) {
      return SuccessCallAdapter(type, annotations, mockResponse)
    }

    return ErrorStatusCallAdapter(type, mockResponse)
  }

  inner class SuccessCallAdapter(type: Type, annotations: Array<Annotation>, private val mockAnnotation: MockResponse) : CallAdapter<Any, Any> {
    private var apiResultType: Type

    init {
      val dataType = (type as ParameterizedType).actualTypeArguments[0]
      apiResultType = if (annotations.any { it is WrappedResponse }) {
        val wrappedType = annotations.find { it is WrappedResponse } as WrappedResponse
        TypeToken.getParameterized(wrappedType.value.java, dataType).type
      } else {
        dataType
      }
    }

    override fun adapt(call: Call<Any>?): Any {
      return Observable.fromCallable {
        if (mockAnnotation.json == -1) {
          Unit
        } else {
          val json = Utils.toText(context, mockAnnotation.json)
          val data = jsonParser.parse<Any>(json, apiResultType)
          if (data is SimpleApiResult<Any>) {
            data.result
          } else {
            data
          }
        }
      }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    override fun responseType(): Type {
      return apiResultType
    }

  }

  inner class ErrorStatusCallAdapter(private val type: Type, private val mockResponse: MockResponse) : CallAdapter<Any, Observable<Throwable>> {
    override fun adapt(call: Call<Any>?): Observable<Throwable> {
      val httpErrorObservable: (Int) -> Observable<Throwable> = { code ->
        Observable.error<Throwable> {
          val message = if (mockResponse.json != -1) {
            Utils.toText(context, mockResponse.json)
          } else {
            ""
          }
          val response = Response.error<String>(code, ResponseBody.create(MediaType.parse("application/json"), message))
          HttpException(response)
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
      }

      return when (mockResponse.status) {
        AUTHENTICATION_ERROR -> { httpErrorObservable(403) }
        CLIENT_ERROR -> { httpErrorObservable(400)}
        SERVER_ERROR -> { httpErrorObservable(500) }
        NETWORK_ERROR -> { Observable.error(UnknownHostException()) }
        SSL_ERROR -> { Observable.error(SSLPeerUnverifiedException("mock ssl error")) }
        else -> { Observable.error(IllegalStateException("Impossible!!")) }
      }
    }

    override fun responseType(): Type {
      return type
    }
  }
}