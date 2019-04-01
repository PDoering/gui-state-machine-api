package de.retest.guistatemachine.api.impl

import de.retest.guistatemachine.api.{ActionTransitions, State}
import de.retest.recheck.ui.descriptors.SutState
import de.retest.surili.commons.actions.Action

import scala.collection.immutable.HashMap

@SerialVersionUID(1L)
case class StateImpl(sutState: SutState) extends State with Serializable {

  /**
    * Currently, there is no MultiMap trait for immutable maps in the Scala standard library.
    * The legacy code used `AmbigueState` here which was more complicated than just a multi map.
    */
  private var outgoingActionTransitions = HashMap[Action, ActionTransitions]()

  /**
    * Redundant information but helpful to be retrieved.
    */
  private var incomingActionTransitions = HashMap[Action, ActionTransitions]()

  override def getSutState: SutState = this.synchronized { sutState }
  override def getOutgoingActionTransitions: Map[Action, ActionTransitions] = this.synchronized { outgoingActionTransitions }
  override def getIncomingActionTransitions: Map[Action, ActionTransitions] = this.synchronized { incomingActionTransitions }

  private[api] override def addTransition(a: Action, to: State): Int = {
    val executionCounter = this.synchronized {
      outgoingActionTransitions.get(a) match {
        case Some(oldTransitions) =>
          val updatedTransitions = ActionTransitions(oldTransitions.states + to, oldTransitions.executionCounter + 1)
          outgoingActionTransitions = outgoingActionTransitions + (a -> updatedTransitions)
          updatedTransitions.executionCounter

        case None =>
          outgoingActionTransitions += (a -> ActionTransitions(Set(to), 1))
          1
      }
    }

    to.synchronized {
      val other = to.asInstanceOf[StateImpl]
      other.incomingActionTransitions.get(a) match {
        case Some(oldTransitions) =>
          val updatedTransitions = ActionTransitions(oldTransitions.states + this, oldTransitions.executionCounter + 1)
          other.incomingActionTransitions = other.incomingActionTransitions + (a -> updatedTransitions)

        case None =>
          other.incomingActionTransitions += (a -> ActionTransitions(Set(this), 1))
      }
    }

    executionCounter
  }
}
