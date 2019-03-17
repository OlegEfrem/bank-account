package com.oef.bank.account.infrastructure.config
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

import scala.concurrent.ExecutionContext

trait ActorContext {
    implicit val system: ActorSystem             = ActorSystem()
    implicit val executor: ExecutionContext      = system.dispatcher
    implicit val materializer: ActorMaterializer = ActorMaterializer()
  }
