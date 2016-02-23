package io.l5d

import com.fasterxml.jackson.annotation.JsonIgnore
import com.twitter.finagle.{Stack, Path}
import io.buoyant.linkerd.config.Parser
import io.buoyant.linkerd.config.types.Port
import io.buoyant.linkerd.namer.serversets.ServersetNamer
import io.buoyant.linkerd.{NamerConfig, NamerInitializer}

class serversets extends NamerInitializer {
  val configClass = classOf[ServersetsConfig]
  val configId = "io.l5d.serversets"
}

object serversets extends serversets

case class ServersetsConfig(zkAddrs: Seq[ZkAddr]) extends NamerConfig {
  @JsonIgnore
  override def defaultPrefix: Path = Path.read("/io.l5d.serversets")

  @JsonIgnore
  val connectString = zkAddrs.map(_.addr).mkString(",")

  /**
   * Construct a namer.
   */
  def newNamer(params: Stack.Params) = new ServersetNamer(connectString)
}

case class ZkAddr(host: String, port: Option[Port]) {

  // TODO: better validation failure
  if (host == null) throw new IllegalArgumentException("zkAddr must specify host")

  def getPort = port match {
    case Some(p) => p.port
    case None => 2181
  }
  def addr: String = s"$host:$getPort"
}
