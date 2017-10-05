package com.jaychang.sac

import android.content.Context
import io.reactivex.Observable

open class SimpleApiClient {

  companion object {
    @JvmStatic
    inline fun <reified Api : Any, reified Error : Any> create(context: Context, init: ApiClientConfig.() -> Unit): Api {
      val config = ApiClientConfig()
      config.init()
      config.context = context.applicationContext
      return ApiManager.init(config, Error::class.java).create(Api::class.java)
    }

    @JvmStatic
    fun all(vararg calls: Observable<*>): Observable<Array<Any>> {
      return Observable.zip(calls.asIterable(), { objects -> objects })
    }
  }

}