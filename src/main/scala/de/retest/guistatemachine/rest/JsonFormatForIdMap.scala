package de.retest.guistatemachine.rest

import de.retest.guistatemachine.rest.model.Id
import spray.json.{JsValue, JsonFormat, RootJsonFormat}

/**
  * Transforms a [[model.Map]] into a `scala.collection.immutable.Map[String, T]`, so it can be converted into valid JSON.
  * Besides, transforms a JSON object which is a `scala.collection.immutable.Map[String, T]` back into a [[model.Map]].
  * This transformer requires a JSON format for the type `K`.
  */
class JsonFormatForIdMap[T](implicit
                            val jsonFormat0: JsonFormat[scala.collection.immutable.Map[String, T]],
                            implicit val jsonFormat1: JsonFormat[T])
    extends RootJsonFormat[model.Map[T]] {

  override def write(obj: model.Map[T]): JsValue =
    jsonFormat0.write(obj.values.map { field =>
      (field._1.id.toString -> field._2)
    })

  override def read(json: JsValue): model.Map[T] = {
    val map = jsonFormat0.read(json)
    new model.Map[T](map.map { x =>
      (Id(x._1.toLong) -> x._2)
    })
  }
}
