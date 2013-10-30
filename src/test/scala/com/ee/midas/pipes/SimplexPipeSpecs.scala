package com.ee.midas.pipes

import org.specs2.mutable.Specification
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import java.io._
import org.specs2.mock.Mockito

@RunWith(classOf[JUnitRunner])
object SimplexPipeSpecs extends Specification with Mockito {

  "simplex pipe" should {
    val pipeName = "test-pipe"
    "transfer data from source to destination" in {
      //given
      val data = "Hello World".getBytes()
      val source = new ByteArrayInputStream(data)
      val destination = new ByteArrayOutputStream()
      val simplexPipe = new SimplexPipe(pipeName,source, destination)

      //when
      simplexPipe.run()

      source.close()
      destination.close()

      //then
      destination.toByteArray() must beEqualTo(data)
    }

    "close on force stop" in {
      //given
      val mockInputStream = mock[InputStream]
      val mockOutputStream = mock[OutputStream]
      val pipe = new SimplexPipe(pipeName, mockInputStream, mockOutputStream)

      //when
      pipe.forceStop

      //then
      there was one(mockInputStream).close()
      there was one(mockOutputStream).close()
    }

    "stop gracefully" in {
      //given
      val mockInputStream = mock[InputStream]
      val mockOutputStream = mock[OutputStream]
      val pipe = new SimplexPipe(pipeName, mockInputStream, mockOutputStream)
      val pipeThread = new Thread(pipe)

      //when
      pipeThread.start()
      Thread.sleep(1000)

      //then
      pipe.isActive must beTrue

      //when
      pipe.stop
      Thread.sleep(1000)
      //then
      pipe.isActive must beFalse
    }

  }
}
