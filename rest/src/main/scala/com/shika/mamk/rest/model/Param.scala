package com.shika.mamk.rest.model

import scala.language.postfixOps

case class Param (
  $lt:          Option[String],
  $lte:         Option[String],
  $gt:          Option[String],
  $gte:         Option[String],
  $ne:          Option[String],
  $in:          Option[String],
  $nin:         Option[String],
  $exists:      Option[Boolean],
  $select:      Option[String],
  $dontSelect:	Option[String],
  $all:	        Option[String],
  $regex:	      Option[String]
) {
  def + (param: Param) = {
    val $lt         = if(param.$lt.isDefined)         param.$lt         else this.$lt
    val $lte        = if(param.$lte.isDefined)        param.$lte        else this.$lte
    val $gt         = if(param.$gt.isDefined)         param.$gt         else this.$gt
    val $gte        = if(param.$gte.isDefined)        param.$gte        else this.$gte
    val $ne         = if(param.$ne.isDefined)         param.$ne         else this.$ne
    val $in         = if(param.$in.isDefined)         param.$in         else this.$in
    val $nin        = if(param.$nin.isDefined)        param.$nin        else this.$nin
    val $exists     = if(param.$exists.isDefined)     param.$exists     else this.$exists
    val $select     = if(param.$select.isDefined)     param.$select     else this.$select
    val $dontSelect = if(param.$dontSelect.isDefined) param.$dontSelect else this.$dontSelect
    val $all        = if(param.$all.isDefined)        param.$all        else this.$all
    val $regex      = if(param.$regex.isDefined)      param.$regex      else this.$regex
    Param (
      $lt, $lte, $gt, $gte, $ne, $in, $nin, $exists, $select, $dontSelect, $all, $regex
    )
  }
}

object Param {
  def apply (
             $lt:         String  = null,
             $lte:        String  = null,
             $gt:         String  = null,
             $gte:        String  = null,
             $ne:         String  = null,
             $in:         String  = null,
             $nin:        String  = null,
             $exists:     String  = null,
             $select:     String  = null,
             $dontSelect:	String  = null,
             $all:	      String  = null,
             $regex:	    String  = null) = {
    new Param (
      Option($lt),
      Option($lte),
      Option($gt),
      Option($gte),
      Option($ne),
      Option($in),
      Option($nin),
      Option($exists).map(_.toBoolean ),
      Option($select),
      Option($dontSelect),
      Option($all),
      Option($regex)
    )
  }
}

class QueryParam {
  private var _params: Map[String, Param] = Map()
  private var _values: Map[String, String] = Map()

  def this (name: String, params: Param) = {
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

  def add(name: String, params: Param): QueryParam = {
    if (_params get name nonEmpty)
      _params += (name -> (_params(name) + params))
    else
      _params += (name -> params)
    this
  }

  def add(params: Map[String, Param]): QueryParam = {
    params foreach { case (name, param) => add(name, param)}
    this
  }

  def map = _params ++ _values
}

object QueryParam {
  def or(params: Seq[QueryParam]) = Map("$or" -> params)
}