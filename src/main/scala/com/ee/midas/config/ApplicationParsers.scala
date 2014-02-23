package com.ee.midas.config

import scala.util.parsing.combinator.JavaTokenParsers
import java.net.{URL, InetAddress}
import com.ee.midas.transform.TransformType
import scala.util.Try
import java.io.File

/**
 * Example:  app1.midas
 *
 * app1 {
 *   mode = expansion
 *   siteANode1 {
 *     ip = x.x.x.x
 *     changeSet = 1
 *   }
 *   siteANode2 {
 *     ip = y.y.y.y
 *     changeSet = 1
 *   }
 *   siteBNode1 {
 *     ip = z.z.z.z
 *     changeSet = 1
 *   }
 *   siteBNode2 {
 *     ip = u.u.u.u
 *     changeSet = 1
 *   }
 * }
 *
 * BNF for Application
 * --------------------------------------
 * app  ::=  name "{" mode nodes "}"
 * mode ::= "mode" "=" "expansion" | "contraction"
 * nodes ::= "{" node {node} "}"
 * node ::=  name "{" ip changeSet "}"
 * ip   ::=  "ip" "=" ipv4 | ipv6 | ipv4MappedIpv6
 * ipv6 ::= ipv6Full | ipv6Compressed
 * changeSet ::= wholeNumber
 * name ::=  ident
 *
 */

trait ApplicationParsers extends JavaTokenParsers {

  //Eat Java-Style comments like whitespace
  protected override val whiteSpace = """(\s|//.*|(?m)/\*(\*(?!/)|[^*])*\*/)+""".r

  def app(configDir: URL): Parser[Application] = ident ~ "{" ~ mode ~  rep1(node) ~ "}" ^^ { case name~"{"~mode~nodes~"}" => new Application(configDir, name, mode, nodes.toSet) }

  def node: Parser[Node] = ident ~ "{" ~ ip ~ changeSet ~ "}" ^^ { case name~"{"~addr~cs~"}" => new Node(name, addr, cs) }

  def ip: Parser[InetAddress] = "ip" ~ "=" ~> (ipv4 | ipv6Full) ^^ (InetAddress.getByName(_))

  def ipv4: Parser[String] = """\b([0-1]?\d{1,2}|2[0-4]\d|25[0-5])(\.([0-1]?\d{1,2}|2[0-4]\d|25[0-5])){3}\b""".r

  def ipv6Full: Parser[String] = """^([0-9A-Fa-f]{1,4}:){7}[0-9A-Fa-f]{1,4}$""".r

  def changeSet: Parser[ChangeSet] = "changeSet" ~ "=" ~> wholeNumber ^^ (s => ChangeSet(s.toLong))

  def mode: Parser[TransformType] = "mode" ~ "=" ~> ("expansion" | "contraction") ^^ (s => TransformType.valueOf(s.toUpperCase))

  def parse(input: String, configDir: URL): Application = parseAll(app(configDir), input) match {
    case Success(value, _) => value
    case NoSuccess(message, _) =>
      throw new IllegalArgumentException(s"Parsing Failed: $message")
  }

  final val appConfigFileExtn = ".midas"

  def parse(configDir: URL): Try[Application] = Try {
    val dir = new File(configDir.getFile)
    val configFilename = s"${dir.getName}$appConfigFileExtn"
    val configFile: URL = new URL(s"${configDir}${File.separator}${configFilename}")
    val configText = scala.io.Source.fromURL(configFile).mkString
    parse(configText, configDir)
  }
}

