package com.shika.mamk.rest

import retrofit.http._

trait RestDefinition {
  @GET("/")
  def query[T](@Query("where") where: String = null,
            @Query("order") order: String,
            @Query("limit") limit: Int,
            @Query("skip") skip: Int): Seq[T]

  @GET("/{objectId}")
  def get[T](@Path("objectId") objectId: String): T

  @POST("/")
  def create[T](@Body obj: T): T

  @PUT("/{objectId}")
  def update[T](@Path("objectId") objectId: String, @Body obj: T): T
}
