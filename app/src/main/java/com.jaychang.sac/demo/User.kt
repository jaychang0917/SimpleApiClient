package com.jaychang.sac.demo

import com.squareup.moshi.Json

data class User(
  @Json(name = "login")
  val name: String
)