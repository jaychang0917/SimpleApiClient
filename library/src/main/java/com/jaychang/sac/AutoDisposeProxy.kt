package com.jaychang.sac

import android.arch.lifecycle.LifecycleOwner
import android.view.View
import io.reactivex.Observable

interface AutoDisposeProxy<T> {
  val sourceObservable: Observable<T>
}

interface AutoDisposeLifecycleOwnerProxy<T>: AutoDisposeProxy<T> {
  val lifecycleOwner: LifecycleOwner
}

interface AutoDisposeViewProxy<T>: AutoDisposeProxy<T> {
  val view: View
}

class AutoDisposeLifecycleOwnerProxyImpl<T>(override val sourceObservable: Observable<T>, override val lifecycleOwner: LifecycleOwner): AutoDisposeLifecycleOwnerProxy<T>

class AutoDisposeViewProxyImpl<T>(override val sourceObservable: Observable<T>, override val view: View): AutoDisposeViewProxy<T>