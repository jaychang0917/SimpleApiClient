package com.jaychang.sac.annotation;

import com.jaychang.sac.SimpleApiResult;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

// kotlin do not support: `annotation class Unwrap(val value: KClass<out SimpleApiResult<*>>)`
@Target(ElementType.METHOD)
public @interface WrappedResponse {
  Class<? extends SimpleApiResult> value();
}