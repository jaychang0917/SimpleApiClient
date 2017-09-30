package com.jaychang.sac.demo.model

import com.google.gson.annotations.SerializedName

data class Repo(
  @SerializedName("name")
  val name: String
)
