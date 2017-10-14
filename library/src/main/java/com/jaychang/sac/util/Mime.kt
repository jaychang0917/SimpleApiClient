package com.jaychang.sac.util

class Mime {
  enum class Text(name: String) {
    PLAIN("plain"), HTML("html"), CSS("css"), JAVASCRIPT("javascript")
  }
  enum class Image(name: String) {
    GIF("gif"), PNG("png"), JPEG("jpeg"), BMP("bmp"), WEBP("webp")
  }
  enum class Audio(name: String) {
    MIDI("midi"), MPEG("mpeg"), WEBM("webm"), OGG("ogg"), WAV("wav")
  }
  enum class Video(name: String) {
    WEBM("webm"), OGG("ogg")
  }
  enum class Application(name: String) {
    OCTET_STREAM("octet_stream"), PDF("pdf"), PKCS12("pkcs12"), VND_MSPOWERPOINT("vnd.mspowerpoint"), XHTML_XML("xhtml+xml"), XML("xml")
  }
}