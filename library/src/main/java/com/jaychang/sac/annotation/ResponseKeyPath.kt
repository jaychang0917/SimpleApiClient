package com.jaychang.sac.annotation

@Target(AnnotationTarget.FUNCTION)
annotation class ResponseKeyPath(val value: String)