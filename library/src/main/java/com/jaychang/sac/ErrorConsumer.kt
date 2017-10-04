package com.jaychang.sac

import io.reactivex.functions.Consumer
import retrofit2.HttpException
import java.net.UnknownHostException
import javax.net.ssl.SSLPeerUnverifiedException
import kotlin.properties.Delegates

internal class ErrorConsumer<T : Throwable>(private val handler: (Throwable) -> Unit) : Consumer<T> {
  override fun accept(error: T) {
    var result: Throwable by Delegates.notNull()
    when (error) {
      is HttpException -> {
        val code = error.response().code()
        val errorJson = error.response().errorBody()?.string()
        val message = (ApiManager.gson.fromJson(errorJson!!, ApiManager.apiErrorClass) as SimpleApiError).message
        when (code) {
          401, 403 -> result = AuthenticationError(code = code, message = message)
          in 400..499 -> result = ClientError(code = code, message = message)
          in 500..599 -> result = ClientError(code = code, message = message)
        }
      }
      is UnknownHostException -> {
        result = NetworkError(source = error)
      }
      is SSLPeerUnverifiedException -> {
        result = SSLError(source = error)
      }
      else -> {
        result = error
      }
    }
    handler(result)

    throw result
  }
}