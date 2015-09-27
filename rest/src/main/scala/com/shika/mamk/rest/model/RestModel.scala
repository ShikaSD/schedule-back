package com.shika.mamk.rest.model

import com.shika.mamk.rest.helper.{JsonConverter, JsonHelper}
import com.shika.mamk.rest.{RestDefinition, RestService}
import org.joda.time.DateTime

trait RestModel {
  def objectId: String
  def createdAt: Option[DateTime]

  def get()
  def create()
  def update()
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

  def query(params: QueryParam = null, order: String = dOrder, limit: Int = dLimit, skip: Int = dSkip) = {
    if (params == null)
      getAdapter.query(null, order, limit, skip)
    else
      getAdapter.query(JsonHelper.toJson(params.map), order, limit, skip)
  }
}
