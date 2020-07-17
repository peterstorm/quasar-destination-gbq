/*
 * Copyright 2020 Precog Data
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package quasar.destination.gbq

import cats.effect.Sync
import cats.implicits._

import scala.{
  Array,
  Byte,
}

import java.io.ByteArrayInputStream

import com.google.auth.oauth2.AccessToken
import com.google.auth.oauth2.GoogleCredentials


object GBQAccessToken {
  def token[F[_]: Sync](auth: Array[Byte]): F[AccessToken] = {
    val credentials = Sync[F] delay {
      val authInputStream = new ByteArrayInputStream(auth)
      GoogleCredentials
        .fromStream(authInputStream)
        .createScoped("https://www.googleapis.com/auth/bigquery")
    }
    credentials.flatMap(creds =>
      Sync[F].delay(creds.refreshIfExpired()) >>
        Sync[F].delay(creds.refreshAccessToken()))
  }
}