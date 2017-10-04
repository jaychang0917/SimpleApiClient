package com.jaychang.sac

class CertificatePin(val hostname: String,
                     val sha1PublicKeyHash: String? = null,
                     val sha256PublicKeyHash: String? = null) {
  override fun toString(): String {
    return when {
      sha1PublicKeyHash != null -> "sha1/$sha1PublicKeyHash"
      sha256PublicKeyHash != null -> "sha256/$sha256PublicKeyHash"
      else -> throw IllegalArgumentException("You must enter `sha1PublicKeyHash` or `sha256PublicKeyHash`")
    }
  }
}