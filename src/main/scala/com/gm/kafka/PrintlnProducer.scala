package com.gm.kafka

import akka.actor.{Actor, ActorLogging}
import com.gm.Sensor.Reading
import io.circe.generic.auto._
import io.circe.syntax._

// Produces String data on the console for testing purposes
class PrintlnProducer(brokers: String, topic:String) extends Actor with ActorLogging {

  var counter = 0L

  def receive = {
    case r:Reading =>
      val msg = r.asJson.noSpaces
      println(msg)
      counter = counter + 1
      if (counter % 100 == 0) println(s"messages sent: $counter")
  }

}
