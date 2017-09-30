package com.jaychang.sac.demo.model

import com.google.gson.annotations.SerializedName

data class User(
  @SerializedName("login")
  val name: String
)