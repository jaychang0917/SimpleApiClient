package com.jaychang.sac

interface SimpleApiResult<out T: Any> {
  val result : T
}
