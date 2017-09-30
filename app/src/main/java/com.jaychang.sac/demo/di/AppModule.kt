package com.jaychang.sac.demo.di

import com.jaychang.sac.demo.GithubApi
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object AppModule {

  @JvmStatic
  @Singleton
  @Provides
  fun provideGithubApi(): GithubApi {
    return GithubApi.create()
  }

}