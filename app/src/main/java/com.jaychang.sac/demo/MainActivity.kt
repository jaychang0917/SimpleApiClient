package com.jaychang.sac.demo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.jaychang.sac.autoCancel
import com.jaychang.sac.demo.di.Injectable
import com.jaychang.sac.observe
import javax.inject.Inject

class MainActivity : AppCompatActivity(), Injectable {

  @Inject
  lateinit var githubApi: GithubApi

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    githubApi.getUsers("google")
      .autoCancel(this)
      .observe(
        onStart = { println("show loading") },
        onEnd = { println("hide loading") },
        onSuccess = { println(it) },
        onError = { println(it.message) }
      )

  }

}