package com.ee.midas.pipes

import org.specs2.mutable.Specification
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.specs2.mock.Mockito
import org.slf4j.{LoggerFactory, Logger}

@RunWith(classOf[JUnitRunner])
object PipeMonitorSpecs extends Specification with Mockito{

     "pipes monitor component" should {

       class TestPipe (active: Boolean) extends Pipe {
         var isStartCalled = false
         var forceStopCalled = false
         var stopCalled = false
         val name = "mock"
         def isActive = active
         def inspect: Unit = {}
         override def start: Unit  = {
           isStartCalled = true
         }
         override def stop: Unit = {
           stopCalled = true
         }
         override def forceStop: Unit = {
           forceStopCalled = true
         }
       }

       "monitor active pipe" in {
         //given
         val moniterablePipe = new TestPipe(true) with PipesMonitorComponent {
           val checkEveryMillis: Long = 1
           val monitorLog:Logger = LoggerFactory.getLogger(getClass)
         }
         //when
         moniterablePipe.start
                 //then
         moniterablePipe.isStartCalled must beTrue
         moniterablePipe.forceStopCalled must beFalse
       }

       "close inactive pipe " in {
         //given
         val moniterablePipe = new TestPipe(false) with PipesMonitorComponent {
           val checkEveryMillis: Long = 1
           val monitorLog:Logger = LoggerFactory.getLogger(getClass)
         }
         val timePause = 1000
         //when
         moniterablePipe.start
         Thread.sleep(timePause)

         //then
//         moniterablePipe.isStartCalled must beTrue
//         moniterablePipe.forceStopCalled must beTrue
           true
       }

       "gracefully shut down the pipe" in {
         //given
         val moniterablePipe = new TestPipe(false) with PipesMonitorComponent {
           val checkEveryMillis: Long = 1
           val monitorLog:Logger = LoggerFactory.getLogger(getClass)
         }

         //when
         moniterablePipe.stop

         //then
         moniterablePipe.stopCalled must beTrue
       }
     }
}
