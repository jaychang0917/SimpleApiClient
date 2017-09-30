package com.jaychang.sac.demo.di

import com.jaychang.sac.demo.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class BuilderModule {

  @ContributesAndroidInjector
  internal abstract fun contributeMainActivity(): MainActivity

}