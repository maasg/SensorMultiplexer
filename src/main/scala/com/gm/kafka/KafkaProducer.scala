package com.gm.kafka

import java.util.HashMap

import akka.actor.{Actor, ActorLogging}
import com.gm.Sensor.Reading

import org.apache.kafka.clients.producer.{ProducerConfig, ProducerRecord, KafkaProducer => KKProducer}
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._

// Produces String data on the provided Kafka topic
class KafkaProducer(brokers: String, topic:String) extends Actor with ActorLogging {

  // Zookeeper connection properties
  val props = new HashMap[String, Object]()
  props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers)
  props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
    "org.apache.kafka.common.serialization.StringSerializer")
  props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
    "org.apache.kafka.common.serialization.StringSerializer")

  val producer = new KKProducer[String, String](props)
  println ("KafkaProducer: Connected. Producer created")

  var counter = 0L

  def receive = {
    case r:Reading =>
      val msg = r.asJson.noSpaces
      producer.send(new ProducerRecord[String, String](topic, null, msg))
      counter = counter + 1
  }

}
