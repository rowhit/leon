/* Copyright 2009-2014 EPFL, Lausanne */

package leon
package datagen

import purescala.Common._
import purescala.Expressions._
import purescala.Types._
import purescala.Definitions._
import utils.StreamUtils._

import purescala.Definitions._
import purescala.ExprOps._
import purescala.Types.TypeTree
import purescala.Common._
import purescala.Constructors._
import purescala.Extractors._
import evaluators._
import synthesis.utils._
import bonsai.enumerators._

import scala.collection.mutable.{Map=>MutableMap}

import synthesis.utils._

/** Utility functions to generate values of a given type.
  * In fact, it could be used to generate *terms* of a given type,
  * e.g. by passing trees representing variables for the "bounds". */
class GrammarDataGen(evaluator: Evaluator, grammar: ExpressionGrammar[TypeTree] = ExpressionGrammars.ValueGrammar) extends DataGenerator {

  def generateFor(ins: Seq[Identifier], satisfying: Expr, maxValid: Int, maxEnumerated: Int): Iterator[Seq[Expr]] = {
    val enum = new MemoizedEnumerator[TypeTree, Expr](grammar.getProductions)
    val values = enum.iterator(tupleTypeWrap(ins.map{ _.getType }))

    val detupled = values.map {
      v => unwrapTuple(v, ins.size)
    }
    
    def filterCond(vs: Seq[Expr]): Boolean = satisfying match {
      case BooleanLiteral(true) =>
        true 
      case e => 
        // in -> e should be enough. We shouldn't find any subexpressions of in.
        evaluator.eval(e, (ins zip vs).toMap) match {
          case EvaluationResults.Successful(BooleanLiteral(true)) => true
          case _ => false
        }
    }

    detupled.take(maxEnumerated)
            .filter(filterCond)
            .take(maxValid)
  }

}