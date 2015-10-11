package com.shika.mamk.rest.model

import com.shika.mamk.rest.model.QueryParam.MapType

import scala.language.postfixOps

object Param {
  def apply (
    lessThan           :AnyRef = null,
    lessThanOrEqual    :AnyRef = null,
    greaterThan        :AnyRef = null,
    greaterThanOrEqual :AnyRef = null,
    notEqual           :AnyRef = null,
    in                 :AnyRef = null,
    notIn              :AnyRef = null,
    exists             :AnyRef = null,
    select             :AnyRef = null,
    dontSelect	       :AnyRef = null,
    all                :AnyRef = null,
    regex	             :AnyRef = null
  ) = {
    collection.mutable.Map (
      "$lt"         -> Option(lessThan),
      "$lte"        -> Option(lessThanOrEqual),
      "$gt"         -> Option(greaterThan),
      "$gte"        -> Option(greaterThanOrEqual),
      "$ne"         -> Option(notEqual),
      "$in"         -> Option(in),
      "$nin"        -> Option(notIn),
      "$exists"     -> Option(exists),
      "$select"     -> Option(select),
      "$dontSelect" -> Option(dontSelect),
      "$all"        -> Option(all),
      "$regex"      -> Option(regex)
    )
  }
}

class QueryParam {
  private var _params: Map[String, MapType] = Map()
  private var _values: Map[String, String] = Map()

  def this (name: String, params: MapType) = {
    this()
    add(name, params)
  }

  def this (values: Map[String, String]) = {
    this()
    _values ++= values
  }

  def this (name: String, value: String) = {
    this()
    add(name, value)
  }

  def add(name: String, value: String): QueryParam = {
    _values += (name -> value)
    this
  }

  def add(name: String, params: MapType): QueryParam = {
    if(_params get name isDefined)
      _params(name) ++= params
    else
      _params += (name -> params)
    this
  }

  def add(params: Map[String, MapType]): QueryParam = {
    params foreach { case (name, param) => add(name, param)}
    this
  }

  def getMap = _params ++ _values
}

object QueryParam {
  type MapType = collection.mutable.Map[String, Option[AnyRef]]
  
  def or(params: Seq[QueryParam]) = ("$or", params)

  def apply (name: String, params: MapType) = new QueryParam(name, params)
  def apply (values: Map[String, String])   = new QueryParam(values)
  def apply (name: String, value: String)   = new QueryParam(name, value)
  def apply ()                              = new QueryParam
}