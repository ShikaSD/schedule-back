package com.shika.mamk.parser.rest

import com.shika.mamk.parser.Key
import retrofit.RequestInterceptor.RequestFacade
import retrofit.{RequestInterceptor, RestAdapter}


object RestService {
  def getService (key: Key) = {

    val request = new RequestInterceptor {
      override def intercept(requestFacade: RequestFacade): Unit = {
        requestFacade.addHeader("X-Parse-Application-Id", key.appId)
        requestFacade.addHeader("X-Parse-REST-API-Key", key.key)
      }
    }

    val restAdapter = new RestAdapter.Builder()
      .setEndpoint("https://api.parse.com")
      .setRequestInterceptor(request)
      .build()

    restAdapter.create(classOf[RestDefinition])
  }
}