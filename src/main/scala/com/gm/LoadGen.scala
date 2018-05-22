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
      System.err.println(s"Usage: LoadGen <metadataBrokerList> <topic>. was: $args with size ${args.size}")
      System.exit(1)
    }

    val Array(brokers, topics) = args

    val system: akka.actor.ActorSystem = ActorSystem("CommSystem")
    val dataFile = "./data/temp-hum.csv"
    val data = Source.fromFile(dataFile).getLines().flatMap(RawReading.parse)

    println("kafkaProducer")
    val kafkaProducer = system.actorOf(Props(new KafkaProducer(brokers, topics)), name = "kafkaProducerActor")
    println("simulationActor")
    val simManager = system.actorOf(SimManagerActor.props(data.toList, 1000, kafkaProducer))
    val serialCommActor = system.actorOf(Props(new SerialCommActor(kafkaProducer)), name = "sensorDataActor")
    simManager ! Tick //start
    println("done")

  }

}
