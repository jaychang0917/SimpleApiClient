package com.jaychang.sac

sealed class Error : Throwable()
class AuthenticationError(val code: Int, override val message: String) : Error()
class ClientError(val code: Int, override val message: String) : Error()
class ServerError(val code: Int, override val message: String) : Error()
class NetworkError(val source: Throwable) : Error()
class SSLError(val source: Throwable) : Error()