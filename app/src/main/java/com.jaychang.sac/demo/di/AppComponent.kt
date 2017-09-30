package com.jaychang.sac.demo.di

import com.jaychang.sac.demo.app.App
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AndroidInjectionModule::class, AppModule::class, BuilderModule::class))
interface AppComponent {

  @Component.Builder
  interface Builder {
    @BindsInstance
    fun app(app: App): Builder

    fun build(): AppComponent
  }

  fun inject(app: App)
  
}