package com.gm.sim

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.gm.Sensor
import com.gm.sim.SensorSimActor.Tick

class SensorSimActor (id: String, data: List[Sensor.RawReading], kafkaProducer: ActorRef) extends Actor with ActorLogging {
  var _data = data

  def receive = {
    case Tick =>
      val head::tail = _data
      val reading = Sensor.Reading.fromRawPlusTime(id, head)
      kafkaProducer ! reading
      _data = if (tail != Nil) tail else data // circularly loop over the data

  }
}
object SensorSimActor {
  case object Tick
  def props(id: String, data: List[Sensor.RawReading], kafkaProducer: ActorRef) =
    Props(new SensorSimActor(id, data, kafkaProducer))
}
