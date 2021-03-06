/******************************************************************************
* Copyright (c) 2014, Equal Experts Ltd
* All rights reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are
* met:
*
* 1. Redistributions of source code must retain the above copyright notice,
*    this list of conditions and the following disclaimer.
* 2. Redistributions in binary form must reproduce the above copyright
*    notice, this list of conditions and the following disclaimer in the
*    documentation and/or other materials provided with the distribution.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
* "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
* TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
* PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
* OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
* EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
* PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
* PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
* LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
* NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
* SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*
* The views and conclusions contained in the software and documentation
* are those of the authors and should not be interpreted as representing
* official policies, either expressed or implied, of the Midas Project.
******************************************************************************/

package com.ee.midas.dsl.expressions

import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.bson.{BSONObject, BasicBSONObject}
import org.specs2.mutable.Specification
import org.specs2.matcher.DataTables

@RunWith(classOf[JUnitRunner])
class ArithmeticExpressionSpecs extends Specification with DataTables {
  "Add" should {
    "Give result as 0 when no values are supplied" in {
      //Given
      val add = Add()
      val document = new BasicBSONObject()

      //When
      val result = add.evaluate(document).value

      //Then
      result mustEqual 0
    }

    "Give sum of Integer values supplied " in {
      //Given
      val add = Add(Literal(1), Literal(3))
      val document = new BasicBSONObject()

      //When
      val result = add.evaluate(document).value

      //Then
      result mustEqual 4
    }

    "Give sum of homogenous types" in {
      //Given
      val add = Add(Literal(1d), Literal(3.5))
      val document = new BasicBSONObject()

      //When
      val result = add.evaluate(document).value

      //Then
      result mustEqual 4.5
    }

    "Give results as 0 for sum of nulls" in {
      //Given
      val add = Add(Literal(null))
      val document = new BasicBSONObject()

      //When
      val result = add.evaluate(document).value

      //Then
      result mustEqual 0
    }


    "Give sum of field value and literal" in {
      //Given
      val add = Add(Literal(1d), Field("age"))
      val document = new BasicBSONObject().append("age", 2)

      //When
      val result = add.evaluate(document).value

      //Then
      result mustEqual 3.0
    }

    "Give literal value as sum when field does not exist" in {
      //Given
      val add = Add(Field("$age"), Literal(1d))
      val document = new BasicBSONObject()

      //When
      val result = add.evaluate(document).value

      //Then
      result mustEqual 1.0
    }

  }

  "Multiply" should {
    "Give result as 0 when no values are supplied" in {
      //Given
      val multiply = Multiply()
      val document = new BasicBSONObject()

      //When
      val result = multiply.evaluate(document)

      //Then
      result mustEqual Literal(0)
    }

    "Give same value when 1 value is supplied" in {
      //Given
      val value = 3.5
      val multiply = Multiply(Literal(value))
      val document = new BasicBSONObject()

      //When
      val result = multiply.evaluate(document)

      //Then
      result mustEqual Literal(value)
    }

    "Give product of homogeneous types" in {
      //Given
      val multiply = Multiply(Literal(.5), Literal(2d))
      val document = new BasicBSONObject()

      //When
      val result = multiply.evaluate(document)

      //Then
      result mustEqual Literal(1.0)
    }

    "Give product of field value and literal" in {
      //Given
      val multiply = Multiply(Literal(1d), Field("age"))
      val document = new BasicBSONObject().append("age", 2)

      //When
      val result = multiply.evaluate(document).value

      //Then
      result mustEqual 2.0
    }

    "Give product as 0 when field does not exist" in {
      //Given
      val multiply = Multiply(Field("age"), Literal(5))
      val document = new BasicBSONObject()

      //When
      val result = multiply.evaluate(document).value

      //Then
      result mustEqual 0
    }
  }

  "Subtract" should {

    "Give difference when minuend and subtrahend are supplied" in {
      //Given
      val subtract = Subtract(Literal(1d), Literal(3.5))
      val document = new BasicBSONObject()

      //When
      val result = subtract.evaluate(document).value

      //Then
      result mustEqual -2.5
    }

    "Gives difference between field value and literal" in {
      //Given
      val subtract = Subtract(Field("age"), Literal(1d))
      val document = new BasicBSONObject().append("age", 2)

      //When
      val result = subtract.evaluate(document).value

      //Then
      result mustEqual 1.0
    }

    "Gives difference when field does not exist" in {
      //Given
      val subtract = Subtract(Field("age"), Literal(5))
      val document = new BasicBSONObject()

      //When
      val result = subtract.evaluate(document).value

      //Then
      result mustEqual -5
    }
  }

  "Divide" should {
    "Give division of homogeneous types" in {
      //Given
      val divide = Divide(Literal(-4), Literal(2d))
      val document = new BasicBSONObject()

      //When
      val result = divide.evaluate(document)

      //Then
      result mustEqual Literal(-2.0)
    }

    "Give division of field value and literal" in {
      //Given
      val divide = Divide(Field("age"), Literal(2d))
      val document = new BasicBSONObject().append("age", 2)

      //When
      val result = divide.evaluate(document).value

      //Then
      result mustEqual 1.0
    }

    "Give result as 0 when field does not exist" in {
      //Given
      val divide = Divide(Field("age"), Literal(5))
      val document = new BasicBSONObject()

      //When
      val result = divide.evaluate(document).value

      //Then
      result mustEqual 0
    }

    "Should shout when a positive value is divided by 0" in {
      //Given
      val divide = Divide(Literal(5), Literal(0))
      val document = new BasicBSONObject()

      //When
      val result = divide.evaluate(document).value

      //Then
      result mustEqual Double.PositiveInfinity
    }

    "Should shout when a negative value is divided by 0" in {
      //Given
      val divide = Divide(Literal(-5), Literal(0))
      val document = new BasicBSONObject()

      //When
      val result = divide.evaluate(document).value

      //Then
      result mustEqual Double.NegativeInfinity
    }
  }

  "Mod" should {
    "Give remainder from division of homogeneous types" in {
      //Given
      val mod = Mod(Literal(-4), Literal(2d))
      val document = new BasicBSONObject()

      //When
      val result = mod.evaluate(document)

      //Then
      result mustEqual Literal(0)
    }

    "Give remainder from division of field value and literal" in {
      //Given
      val mod = Mod(Field("age"), Literal(2d))
      val document = new BasicBSONObject().append("age", 5)

      //When
      val result = mod.evaluate(document).value

      //Then
      result mustEqual 1.0
    }

    "Give result as 0 when field does not exist" in {
      //Given
      val mod = Mod(Field("age"), Literal(5))
      val document = new BasicBSONObject()

      //When
      val result = mod.evaluate(document).value

      //Then
      result mustEqual 0
    }

    "Give result as NaN when a value is divided by 0" in {
      //Given
      val mod = Mod(Literal(5), Literal(0))
      val document = new BasicBSONObject()

      //When
      val result = mod.evaluate(document).value

      //Then
      result.asInstanceOf[Double].isNaN mustEqual true
    }
  }

  "Operations when combined with divide by zero should give NaN" ^ {
          "expression"                                        |        "expected"       |
      Subtract(Literal(3), Divide(Literal(2), Literal(0)))    ! Double.NegativeInfinity |
      Add(Literal(3), Divide(Literal(2), Literal(0)))         ! Double.PositiveInfinity |
      Multiply(Literal(3), Divide(Literal(2), Literal(0)))    ! Double.PositiveInfinity |>
      {
        (expression: Expression, expected: Double) =>
          val document = new BasicBSONObject()
          expression.evaluate(document).value mustEqual expected
      }
  }

  "Treats Literal" should {
    val anyArithmeticFunction = new ArithmeticFunction {
      def evaluate(document: BSONObject): Literal = ???
    }

    "null as 0" in {
      //When
      val treatedOutcome = anyArithmeticFunction.value(Literal(null))

      //Then
      treatedOutcome mustEqual 0
    }

    "with Double as String value as Double" in {
      //When
      val treatedOutcome = anyArithmeticFunction.value(Literal("23e-2"))

      //Then
      treatedOutcome mustEqual 0.23
    }

    "with Integer as String value as Double" in {
      //When
      val treatedOutcome = anyArithmeticFunction.value(Literal("23"))

      //Then
      treatedOutcome mustEqual 23.0
    }

    "with character string " in {
      //When-Then
      anyArithmeticFunction.value(Literal("unparseable")) must throwAn[NumberFormatException]
    }
  }
}
