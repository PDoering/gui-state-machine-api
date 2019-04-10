package de.retest.guistatemachine.api.neo4j
import de.retest.guistatemachine.api.{ActionTransitions, State}
import de.retest.recheck.ui.descriptors.SutState
import de.retest.surili.commons.actions.Action
import org.neo4j.ogm.cypher.{ComparisonOperator, Filter}

import scala.collection.immutable.HashMap

case class StateNeo4J(sutState: SutState, guiStateMachine: GuiStateMachineNeo4J) extends State {
  implicit val session = guiStateMachine.session

  override def getSutState: SutState = sutState
  override def getTransitions: Map[Action, ActionTransitions] =
    Neo4jSessionFactory.transaction {
      val filter = new Filter("start", ComparisonOperator.EQUALS, sutState)
      val transitions = session.loadAll(classOf[ActionTransitionEntity], filter)
      var result = HashMap[Action, ActionTransitions]()
      val iterator = transitions.iterator()
      while (iterator.hasNext) {
        val relationship = iterator.next()
        val action = relationship.action
        val targetSutState = relationship.end
        val counter = relationship.counter
        val actionTransitions = if (result.contains(action)) {
          val existing = result(action)
          ActionTransitions(existing.to ++ Set(StateNeo4J(targetSutState, guiStateMachine)), existing.executionCounter + counter)
        } else {
          ActionTransitions(Set(StateNeo4J(targetSutState, guiStateMachine)), counter)
        }
        result = result + (action -> actionTransitions)
      }
      result
    }

  private[api] override def addTransition(a: Action, to: State): Int = Neo4jSessionFactory.transaction {
    val filter = new Filter("start", ComparisonOperator.EQUALS, sutState)
    val filter2 = new Filter("action", ComparisonOperator.EQUALS, a)
    val targetSutState = to.asInstanceOf[StateNeo4J].sutState
    val filter3 = new Filter("end", ComparisonOperator.EQUALS, targetSutState)
    val transitions = session.loadAll(classOf[ActionTransitionEntity], filter.and(filter2).and(filter3))
    val first = transitions.stream().findFirst()
    val counter = if (first.isPresent) {
      first.get().counter = first.get().counter + 1
      session.save(first.get())
      first.get().counter
    } else {
      val transition = new ActionTransitionEntity(sutState, targetSutState, a)
      session.save(transition)
      1
    }
    counter
  }
}
