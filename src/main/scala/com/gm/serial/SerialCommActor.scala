package com.gm.serial

import com.github.jodersky.flow.{Parity, Serial, SerialSettings, AccessDeniedException}

import akka.actor._
import akka.io.IO
import akka.util.ByteString
import com.gm.kafka.DataProducer

import scala.collection.mutable.ArrayBuffer
import java.io._
class SerialCommActor(dataProducer: ActorRef) extends Actor with ActorLogging {

  println("Serial Comms: Opening port ...")

  val ctx = implicitly[ActorContext]
  var operator: ActorRef = _
  implicit val system = ctx.system
  val dataFile = "/home/maasg/playground/sparkfun/SensorMultiplexer/temp-hum.csv"

  val port = "/dev/ttyUSB1"
  val settings = SerialSettings(
    baud = 9600,
    characterSize = 8,
    twoStopBits = false,
    parity = Parity.None
  )

  IO(Serial) ! Serial.Open(port, settings)
  val filePrinter = new PrintWriter(new FileWriter(dataFile, true))

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
      maybeData.foreach{s =>
        println(s"received message: $s" )
        filePrinter.println(s)
        //val rate = s.toInt * 50

        //dataProducer ! DataProducer.MessagesPerSecond(rate)
      }
      buffer = newBuffer
    }
  }
}


