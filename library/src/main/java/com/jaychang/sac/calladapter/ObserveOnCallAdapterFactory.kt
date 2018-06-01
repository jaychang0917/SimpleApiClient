package com.jaychang.sac.calladapter

import io.reactivex.*
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.Type


internal class ObserveOnCallAdapterFactory(private val scheduler: Scheduler) : CallAdapter.Factory() {
  companion object {
    fun create(scheduler: Scheduler): ObserveOnCallAdapterFactory {
      return ObserveOnCallAdapterFactory(scheduler)
    }
  }

  override fun get(returnType: Type, annotations: Array<Annotation>, retrofit: Retrofit): CallAdapter<*, *>? {
    val rawType = getRawType(returnType)

    val isObservable = rawType === Observable::class.java
    val isFlowable = rawType === Flowable::class.java
    val isSingle = rawType === Single::class.java
    val isMaybe = rawType === Maybe::class.java
    val isCompletable = rawType === Completable::class.java

    if (!isObservable && !isFlowable && !isSingle && !isMaybe && !isCompletable) {
      return null
    }

    val delegate = retrofit.nextCallAdapter(this, returnType, annotations)

    return ObserveOnCallAdapter(delegate, scheduler, isObservable, isFlowable, isSingle, isMaybe, isCompletable)
  }
}

internal class ObserveOnCallAdapter<R>(private val delegate: CallAdapter<R, *>,
                                       private val scheduler: Scheduler,
                                       private val isObservable: Boolean,
                                       private val isFlowable: Boolean,
                                       private val isSingle: Boolean,
                                       private val isMaybe: Boolean,
                                       private val isCompletable: Boolean): CallAdapter<R, Any?> {
  override fun adapt(call: Call<R>): Any? {
    val result = delegate.adapt(call)
    return when {
      isObservable -> (result as Observable<*>).observeOn(scheduler)
      isFlowable -> (result as Flowable<*>).observeOn(scheduler)
      isSingle -> (result as Single<*>).observeOn(scheduler)
      isMaybe -> (result as Maybe<*>).observeOn(scheduler)
      isCompletable -> (result as Completable).observeOn(scheduler)
      else -> throw IllegalArgumentException("Unsupported type")
    }
  }

  override fun responseType(): Type {
    return delegate.responseType()
  }
}