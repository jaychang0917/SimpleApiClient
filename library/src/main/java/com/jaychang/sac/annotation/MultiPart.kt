package com.jaychang.sac.annotation

@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class MultiPart(val name: String, val mimeType: String)