package leon

import purescala.Common._
import purescala.Definitions._
import purescala.Trees._
import purescala.TypeTrees._

object Simplificator extends Pass {

  val description = "Some safe and minimal simplification"

  def apply(pgm: Program): Program = {

    val allFuns = pgm.definedFunctions
    allFuns.foreach(fd => fd.body.map(body => {
      fd.body = Some(simplifyLets(body))
    }))
    pgm
  }

}

