package de.retest.guistatemachine.api.neo4j

import de.retest.recheck.ui.descriptors.SutState
import org.neo4j.ogm.annotation._
import org.neo4j.ogm.annotation.typeconversion.Convert

// TODO #19 Use this entity and sessions instead of manual transactions.
@NodeEntity
class SutStateEntity(state: SutState) extends Entity {

  def this() = this(null)

  @Convert(classOf[SutStateConverter])
  val sutState: SutState = state
}
