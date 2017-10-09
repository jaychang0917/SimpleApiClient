package com.jaychang.sac

import io.reactivex.Observable

open class SimpleApiClient {

  companion object {
    @JvmStatic
    inline fun <reified Api : Any> create(init: ApiClientConfig.() -> Unit): Api {
      val config = ApiClientConfig()
      config.init()
      return ApiManager.init(config).create(Api::class.java)
    }

    @JvmStatic
    fun all(vararg calls: Observable<*>): Observable<Array<Any>> {
      return Observable.zip(calls.asIterable(), { objects -> objects })
    }
  }

}