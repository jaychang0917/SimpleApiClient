package com.jaychang.sac.annotations;

import com.jaychang.sac.SimpleApiResult;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

// kotlin do not support: `annotation class Unwrap(val value: KClass<out SimpleApiResult<*>>)`
@Target(ElementType.METHOD)
public @interface Unwrap {
  Class<? extends SimpleApiResult> value();
}