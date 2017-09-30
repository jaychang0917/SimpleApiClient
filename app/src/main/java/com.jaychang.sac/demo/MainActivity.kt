package com.jaychang.sac.demo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.jaychang.sac.*
import com.jaychang.sac.demo.di.Injectable
import javax.inject.Inject

class MainActivity : AppCompatActivity(), Injectable {

  @Inject
  lateinit var githubApi: GithubApi

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    githubApi.getUsers("google")
      .then { githubApi.getRepo("google", "gson") }
      .thenAll(
        githubApi.getUsers("ReactiveX"),
        githubApi.getRepo("ReactiveX", "RxJava")
      )
      .retryExponential(maxRetryCount = 3, delaySeconds = 5)
      .retryInterval(maxRetryCount = 3, delaySeconds = 5)
      .autoDispose(this)
      .observe(
        onStart = { println("show loading") },
        onEnd = { println("hide loading") },
        onSuccess = { println(it) },
        onError = { println(it.message) }
      )
  }

}