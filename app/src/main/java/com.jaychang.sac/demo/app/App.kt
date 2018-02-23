package com.jaychang.sac.demo.app

import android.app.Activity
import android.app.Application
import com.jaychang.sac.demo.di.AppInjector
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject

class App : Application(), HasActivityInjector {

  @Inject
  lateinit var androidInjector : DispatchingAndroidInjector<Activity>

  override fun onCreate() {
    super.onCreate()

    AppInjector.init(this)
  }

  override fun activityInjector(): AndroidInjector<Activity> = androidInjector

}
