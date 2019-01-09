package de.retest.guistatemachine.api.impl

import de.retest.guistatemachine.api.{ActionTransitions, State}
import de.retest.surili.model.actions.Action
import de.retest.ui.descriptors.SutState

import scala.collection.immutable.HashMap

@SerialVersionUID(1L)
class StateImpl(sutState: SutState, var neverExploredActions: Set[Action]) extends State with Serializable {

  /**
    * TODO #4 Currently, there is no MultiMap trait for immutable maps in the Scala standard library.
    * The legacy code used `AmbigueState` here which was more complicated than just a multi map.
    */
  var transitions = new HashMap[Action, ActionTransitions]

  override def getSutState: SutState = sutState
  override def getNeverExploredActions: Set[Action] = neverExploredActions
  override def getTransitions: Map[Action, ActionTransitions] = transitions

  private[api] override def addTransition(a: Action, to: State): Int = {
    val old = transitions.get(a)
    old match {
      case Some(o) =>
        val updated = ActionTransitions(o.to + to, o.executionCounter + 1)
        transitions = transitions + (a -> updated)
        updated.executionCounter

      case None =>
        transitions += (a -> ActionTransitions(Set(to), 1))
        // In the legacy code this is done in `increaseTimesExecuted`.
        neverExploredActions = neverExploredActions - a
        1
    }
  }
}
