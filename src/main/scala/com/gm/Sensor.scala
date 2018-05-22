package com.gm

import scala.util.Try

object Sensor {
  case class Reading(id: String, ts: Long, temp: Double, hum: Double)
  object Reading {
    def fromRawPlusTime(id:String, raw: RawReading): Reading = Reading(id, System.currentTimeMillis(), raw.v1, raw.v2)
  }
  case class RawReading(v1: Double, v2: Double)
  object RawReading {
    def parse(str: String): Option[RawReading] = {
      Try {
        val Array(v1, v2) = str.split(",")
        RawReading(v1.toDouble, v2.toDouble)
      }.toOption
    }
  }
}
