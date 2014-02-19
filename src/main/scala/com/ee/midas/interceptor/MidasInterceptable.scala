package com.ee.midas.interceptor

import com.ee.midas.pipes.Interceptable
import java.io.{InputStream, OutputStream}
import com.ee.midas.config.{ApplicationListener, Application}
import java.net.InetAddress
import com.ee.midas.utils.Loggable

abstract class MidasInterceptable(private var application: Application, ip: InetAddress)
  extends Interceptable with ApplicationListener with Loggable {
  private var selfTerminate = false

  private val terminate = -1

  override def intercept(src: InputStream, tgt: OutputStream): Int = {
    val header = readHeader(src)
    val inputData = read(src, header)
    if(selfTerminate)
      terminate
    else
      write(inputData, tgt)
  }

  def write(data: Array[Byte], tgt: OutputStream): Int = {
    val bytesToWrite = data.length
    tgt.write(data, 0, bytesToWrite)
    tgt.flush()
    bytesToWrite
  }

  def onUpdate(application: Application): Unit =
    this.synchronized {
      if (application.hasNode(ip)) {
        logInfo(s"${toString} found IP, Updating the NEW application received")
        this.application = application
      } else {
        logInfo(s"${toString} did not find IP, Sending Termination Signal")
        selfTerminate = true
      }
    }

  protected def getApplication =
    this.synchronized {
      application
    }

  def read(src: InputStream, header: BaseMongoHeader) : Array[Byte]

  def readHeader(src: InputStream) : BaseMongoHeader
}
