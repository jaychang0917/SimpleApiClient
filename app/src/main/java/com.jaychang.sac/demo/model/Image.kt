package com.jaychang.sac.demo.model

import com.google.gson.annotations.SerializedName

data class Image(
  @SerializedName("url")
  val url: String
)