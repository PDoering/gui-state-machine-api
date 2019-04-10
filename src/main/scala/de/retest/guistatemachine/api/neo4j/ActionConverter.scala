package de.retest.guistatemachine.api.neo4j

import de.retest.surili.commons.actions.{Action, NavigateRefreshAction}
import org.neo4j.ogm.typeconversion.AttributeConverter

class ActionConverter extends AttributeConverter[Action, String] {
  def toGraphProperty(value: Action): String = value.toString // TODO #19 convert to XML with element
  def toEntityAttribute(value: String): Action = new NavigateRefreshAction // TODO #19 convert from XML to an action
}
