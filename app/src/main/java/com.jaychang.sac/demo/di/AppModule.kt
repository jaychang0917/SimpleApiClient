package com.jaychang.sac.demo.di

import com.jaychang.sac.demo.GithubApi
import com.jaychang.sac.demo.app.App
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object AppModule {

  @JvmStatic
  @Singleton
  @Provides
  fun provideGithubApi(app: App): GithubApi {
    return GithubApi.create(app)
  }

}