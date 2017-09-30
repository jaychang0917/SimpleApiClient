package com.jaychang.sac.calladapter

import io.reactivex.Observable
import io.reactivex.Scheduler
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.Type


internal class ObserveOnCallAdapterFactory(val scheduler: Scheduler) : CallAdapter.Factory() {

  companion object {
    @JvmStatic
    fun create(scheduler: Scheduler): ObserveOnCallAdapterFactory {
      return ObserveOnCallAdapterFactory(scheduler)
    }
  }

  override fun get(returnType: Type, annotations: Array<Annotation>, retrofit: Retrofit): CallAdapter<*, *>? {
    if (CallAdapter.Factory.getRawType(returnType) != Observable::class.java) {
      return null
    }

    val delegate = retrofit.nextCallAdapter(this, returnType,
      annotations) as CallAdapter<Any, Observable<*>>

    return object : CallAdapter<Any, Any> {
      override fun adapt(call: Call<Any>): Any {
        val o = delegate.adapt(call)
        return o.observeOn(scheduler)
      }

      override fun responseType(): Type {
        return delegate.responseType()
      }
    }
  }
}