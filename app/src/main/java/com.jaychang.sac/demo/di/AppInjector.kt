package com.jaychang.sac.demo.di

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.jaychang.sac.demo.app.App
import dagger.android.AndroidInjection

object AppInjector {

  fun init(app: App) {
    DaggerAppComponent.builder().app(app).build().inject(app)
    
    app.registerActivityLifecycleCallbacks(object: Application.ActivityLifecycleCallbacks {
      override fun onActivityCreated(activity: Activity?, bundle: Bundle?) {
        handleActivity(activity)
      }

      override fun onActivityPaused(activity: Activity?) = Unit

      override fun onActivityResumed(activity: Activity?) = Unit

      override fun onActivityStarted(activity: Activity?) = Unit

      override fun onActivityDestroyed(activity: Activity?) = Unit

      override fun onActivitySaveInstanceState(activity: Activity?, bundle: Bundle?) = Unit

      override fun onActivityStopped(activity: Activity?) = Unit
    })
  }

  private fun handleActivity(activity: Activity?) {
    if (activity is Injectable) {
      AndroidInjection.inject(activity)
    }
  }

}