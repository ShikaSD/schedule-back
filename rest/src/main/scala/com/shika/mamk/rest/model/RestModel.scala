package com.shika.mamk.rest.model

import com.shika.mamk.rest.helper.{JsonConverter, JsonHelper}
import com.shika.mamk.rest.{RestDefinition, RestService}
import org.joda.time.DateTime

trait RestModel {
  def objectId: String
  def createdAt: Option[DateTime]
}

trait RestObject {
  type T <: RestModel

  protected val dOrder: String = null
  protected val dSkip = 0
  protected val dLimit = Int.MaxValue
  
  protected val apiPath: String
  protected val _converter: JsonConverter[T]

  private var _adapter: Option[RestDefinition] = None

  def getAdapter: RestDefinition = {
    if(_adapter.isEmpty)
      _adapter = Some(RestService.getAdapter(apiPath, _converter))
    _adapter.get
  }

  def query(params: AnyRef = null, order: String = dOrder, limit: Int = dLimit, skip: Int = dSkip): Seq[T] =
    params match {
      case (name: String, param: Seq[Any]) =>
        val paramsMap = (name, param.map(_.asInstanceOf[QueryParam].getMap))
        getAdapter.query(JsonHelper.toJson(paramsMap), order, limit, skip)

      case params: QueryParam =>
        getAdapter.query (JsonHelper.toJson (params.getMap), order, limit, skip)

      case _ => getAdapter.query (null, order, limit, skip)
    }


  def query: Seq[T] = getAdapter.query(null, dOrder, dLimit, dSkip)
}
