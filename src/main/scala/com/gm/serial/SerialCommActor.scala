package com.gm.serial

import com.github.jodersky.flow.{AccessDeniedException, Parity, Serial, SerialSettings}
import akka.actor._
import akka.io.IO
import akka.util.ByteString
import com.gm.kafka.DataProducer

import scala.collection.mutable.ArrayBuffer
import java.io._

import com.gm.Sensor.{RawReading, Reading}

class SerialCommActor(kafkaProducer: ActorRef) extends Actor with ActorLogging {

  println("Serial Comms: Opening port ...")

  val ctx = implicitly[ActorContext]
  var operator: ActorRef = _
  implicit val system = ctx.system

  val port = "/dev/ttyUSB1"
  val settings = SerialSettings(
    baud = 9600,
    characterSize = 8,
    twoStopBits = false,
    parity = Parity.None
  )

  IO(Serial) ! Serial.Open(port, settings)

  var buffer = new ParsingBuffer(ByteString())
  def receive = {
    case Serial.CommandFailed(cmd: Serial.Open, reason: AccessDeniedException) =>
      println("You're not allowed to open that port!")
    case Serial.CommandFailed(cmd: Serial.Open, reason) =>
      println("Could not open port for some other reason: " + reason.getMessage)
      reason.printStackTrace()
    case Serial.Opened(settings) => {
      operator = sender
      //do stuff with the operator, e.g. context become opened(op)
    }

    case Serial.Received(data) => {
      buffer = buffer.add(data)
      val (maybeData, newBuffer) = buffer.poll
      maybeData.flatMap(RawReading.parse).map(raw => Reading.fromRawPlusTime("office", raw)).foreach{ s =>
        println(s"received message: $s" )
        kafkaProducer ! s
      }
      buffer = newBuffer
    }
  }
}


