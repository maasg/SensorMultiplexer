package com.gm.sim

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.gm.Sensor
import com.gm.sim.SensorSimActor.Tick

import scala.concurrent.duration._
import scala.util.Random
class SimManagerActor(dataset: List[Sensor.RawReading], simulatorCount: Int, kafkaProducer: ActorRef) extends Actor with ActorLogging {

  import context.dispatcher

  val dataSize = dataset.size
  val simActors = {
    for {
      i <- (1 to simulatorCount)
      from <- Seq(Random.nextInt(dataSize))
      to <- Seq(from + Random.nextInt(dataSize))
    } yield context.actorOf(SensorSimActor.props(s"sim-$i", dataset.slice(from, to), kafkaProducer))
  }.toList

  var _simActors = simActors

  override def receive = {
    case Tick =>
      val head :: tail = _simActors
      head ! Tick
      _simActors = if (tail.nonEmpty) tail else simActors
      context.system.scheduler.scheduleOnce(10 millis, self, Tick)
  }
}
object SimManagerActor {
  def props(data: List[Sensor.RawReading], simCount: Int, kafkaProducer: ActorRef) =
    Props(new SimManagerActor(data, simCount, kafkaProducer))
}