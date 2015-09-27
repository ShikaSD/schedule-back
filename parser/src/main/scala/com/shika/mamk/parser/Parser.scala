package com.shika.mamk.parser

import com.shika.mamk.rest.AppKeys._
import com.shika.mamk.rest.RestService
import com.shika.mamk.rest.model.classes.Group
import com.shika.mamk.rest.model.{Param, QueryParam}

object Parser extends App {
  override def main(args: Array[String]): Unit = {
    keys.foreach {
      key =>
        RestService.initialize(key)

        val params =
          new QueryParam()
            .add("name", Param($lt = "V", $gte = "T"))
            .add("objectId", "ETRiNgtOn6")
        Group query(params, order = "name") foreach println
    }
  }
}
