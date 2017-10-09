package com.jaychang.sac

import io.reactivex.disposables.Disposable

class Cancelable(private val disposable: Disposable) {

  fun cancel() = disposable.dispose()

  fun isCanceled() = disposable.isDisposed()

}