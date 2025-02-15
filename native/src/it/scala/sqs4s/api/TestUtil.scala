package sqs4s.api

import java.util.UUID

import cats.effect.{Clock, Resource, Sync, Timer}
import cats.implicits._
import org.http4s.Uri
import org.http4s.client.Client
import sqs4s.api.lo.{CreateQueue, DeleteQueue}

object TestUtil {
  def queueResource[F[_]: Sync: Clock: Timer](
    client: Client[F],
    rootConfig: SqsConfig[F]
  ): Resource[F, CreateQueue.Result] = {
    Resource.make {
      Sync[F].delay("test-" + UUID.randomUUID()).flatMap { name =>
        CreateQueue[F](name, rootConfig.queue).runWith(client, rootConfig)
      }
    } { queue =>
      DeleteQueue[F](Uri.unsafeFromString(queue.queueUrl)).runWith(
        client,
        rootConfig
      ).void
    }
  }
}
