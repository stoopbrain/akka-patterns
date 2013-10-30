package com.sksamuel.akka.patterns

import akka.actor.{Actor, ActorRef}
import scala.collection.mutable.ListBuffer

/** @author Stephen Samuel */
class Resequencer(types: Seq[Class[_]], target: ActorRef) extends Actor {

  val buffers = types.map(arg => new ListBuffer[AnyRef])

  def receive = {
    case msg: AnyRef =>
      types.indexOf(msg.getClass) match {
        case -1 => unhandled(msg)
        case pos: Int =>
          buffers(pos).append(msg)
          checkForCompleteSequence()
      }
  }

  def checkForCompleteSequence(): Unit = {
    if (buffers.count(_.size > 0) == buffers.size) {
      val msg = buffers.map(_.remove(0))
      target ! msg
    }
  }
}