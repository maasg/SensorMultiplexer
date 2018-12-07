package com.gm

import akka.actor.{ActorSystem, Props}
import com.gm.Sensor.RawReading
import com.gm.kafka.{DataProducer, KafkaProducer, PrintlnProducer}
import com.gm.serial.SerialCommActor
import com.gm.sim.SensorSimActor.Tick
import com.gm.sim.SimManagerActor

import scala.io.Source
import scala.util.Try

object LoadGen {
  def main(args: Array[String]): Unit = {

    if (args.length < 2) {
      args.zipWithIndex.foreach(println _)
      System.err.println(s"Usage: LoadGen <metadataBrokerList> <topic> [--use-sensor]. was: $args with size ${args.size}")
      System.exit(1)
    }

    val brokers = args(0)
    val topics  = args(1)
    val sensorEnabled = args.size >= 3  && args(2) == "--use-sensor"


    val system: akka.actor.ActorSystem = ActorSystem("CommSystem")
    val dataFile = "./data/temp-hum-clean.csv"
    val data = Source.fromFile(dataFile).getLines().flatMap(RawReading.parse)

    println("kafkaProducer")
    val kafkaProducer = system.actorOf(Props(new KafkaProducer(brokers, topics)), name = "kafkaProducerActor")
    println("simulationActor")
    val simManager = system.actorOf(SimManagerActor.props(data.toList, 1000, kafkaProducer))
    if (sensorEnabled) {
      system.actorOf(Props(new SerialCommActor(kafkaProducer)), name = "sensorDataActor")
    }
    simManager ! Tick //start
    println("done")

  }

}
