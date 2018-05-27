package com.jaychang.sac.demo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.jaychang.sac.autoCancel
import com.jaychang.sac.observe

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    ApiClient.githubApi.getUsers("google")
      .autoCancel(this)
      .observe(
        onStart = { println("show loading") },
        onEnd = { println("hide loading") },
        onSuccess = { println(it) },
        onError = { println(it.message) }
      )
  }
}