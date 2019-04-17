package de.retest.guistatemachine.api
import java.nio.file.Paths

import de.retest.guistatemachine.api.impl.GuiStateMachineApiImpl
import de.retest.guistatemachine.api.neo4j.GuiStateMachineApiNeo4JEmbedded

/**
  * This API allows the creation, modification and deletion of state machines ([[GuiStateMachine]]) which are created
  * during test generations with the help of Genetic Algorithms.
  */
trait GuiStateMachineApi {

  /**
    * Creates a new [[GuiStateMachine]].
    *
    * @return The new GUI state machine.
    */
  def createStateMachine(name: String): GuiStateMachine

  /**
    * Removes an existing [[GuiStateMachine]].
    *
    * @param name The name of the GUI state machine.
    * @return True if it existed and was removed by this call. Otherwise, false.
    */
  def removeStateMachine(name: String): Boolean

  /**
    * Gets an existing [[GuiStateMachine]].
    *
    * @param name The name of the GUI state machine.
    * @return The existing GUI state machine or nothing.
    */
  def getStateMachine(name: String): Option[GuiStateMachine]

  /**
    * Clears all state machines.
    */
  def clear(): Unit
}

object GuiStateMachineApi {

  /**
    * The default directory where all state machines are stored.
    */
  val Neo4JEmbeddedStorageDirectory: Path = Paths.get(System.getProperty("user.home"), ".retest", "guistatemachines").toAbsolutePath.toString

  val default = new GuiStateMachineApiImpl

  /**
    * @return The standard implementaiton of the API.
    */
  def apply(): GuiStateMachineApi = default

  val neo4jEmbedded = new GuiStateMachineApiNeo4JEmbedded(Paths.get(Neo4JEmbeddedStorageDirectory))
}
