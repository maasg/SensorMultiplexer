package com.gm.serial

import akka.util.ByteString

class ParsingBuffer(data: ByteString){
  val CR = 13.toByte
  val LF = 10.toByte
  val CRLF = Array(CR,LF)

  def add(other: ByteString): ParsingBuffer = {
    new ParsingBuffer(data ++ other)
  }

  val isEmpty = data.isEmpty

  def poll:(Option[String], ParsingBuffer) = {
    val (maybeComplete, rest) = data.span(_ != CR)
    val crlf = rest.take(2).toArray
    if (crlf.deep == CRLF.deep) {
      (Some(maybeComplete.utf8String), new ParsingBuffer(rest.drop(2)))
    } else (None, this)
  }
}