package de.retest.guistatemachine.api

trait GuiStateMachineApi {

  /**
    * Creates a new [[GuiStateMachine]].
    *
    * @return The new GUI state machine.
    */
  def createStateMachine(): Id

  /**
    * Removes an existing [[GuiStateMachine]].
    *
    * @param id The ID of the GUI state machine.
    * @return True if it existed and was removed by this call. Otherwise, false.
    */
  def removeStateMachine(id: Id): Boolean

  /**
    * Gets an existing [[GuiStateMachine]].
    *
    * @param id The ID of the GUI state machine.
    * @return The existing GUI state machine or nothing.
    */
  def getStateMachine(id: Id): Option[GuiStateMachine]

  /**
    * Stores all state machines on the disk.
    * Persistence can be useful when the state machines become quite big and the generation/modification is interrupted
    * and continued later.
    */
  def persist(): Unit

  /**
    * Loads all state machines from the disk.
    */
  def load(): Unit
}
