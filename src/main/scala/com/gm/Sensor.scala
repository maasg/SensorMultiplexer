package com.gm

object Sensor {
  case class Reading(id: String, ts: Long, values: Array[Double])
  object Reading {
    def fromRawPlusTime(id:String, raw: RawReading): Reading = Reading(id, System.currentTimeMillis(), Array(raw.v1, raw.v2))
  }
  case class RawReading(v1: Double, v2: Double)
}
