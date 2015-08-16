package com.shika.mamk.parser.rest

import retrofit.http._

trait RestDefinition {
  var apiPath = ""

  def setup(path: String) = {
    apiPath = path
  }

  @GET(s"/1/classes/$apiPath")
  def query(): Seq[RestModel]

  @GET(s"/1/classes/$apiPath/{objectId}")
  def get(@Path("objectId") objectId: String)

  @POST(s"/1/classes/$apiPath")
  def put(@Body obj: RestModel)

  @PUT(s"/1/classes/$apiPath/{objectId}")
  def put(
    @Path("objectId") objectId: String,
    @Body obj: RestModel
  )
}