package com.shika.mamk.rest

import com.shika.mamk.rest.helper.JsonConverter
import com.shika.mamk.rest.model.BaseModel
import retrofit.RequestInterceptor.RequestFacade
import retrofit.RestAdapter.LogLevel
import retrofit.{RequestInterceptor, RestAdapter}


object RestService {
  private var _key: Option[Key] = None

  def initialize (key: Key) = {
    _key = Some(key)
  }

  def getAdapter[T <: BaseModel] (apiPath: String, converter: JsonConverter[T]) = {

    val request = new RequestInterceptor {
      override def intercept(requestFacade: RequestFacade): Unit = {
        if(_key.isEmpty)
          return

        requestFacade.addHeader("X-Parse-Application-Id", _key.get.appId)
        requestFacade.addHeader("X-Parse-REST-API-Key", _key.get.restApiKey)
        requestFacade.addHeader("X-Parse-Master-Key", _key.get.masterKey)
      }
    }

    val restAdapter = new RestAdapter.Builder()
      .setEndpoint(s"https://api.parse.com/1/classes/$apiPath")
      .setRequestInterceptor(request)
      .setConverter(converter)
      .setLogLevel(LogLevel.NONE)
      .build()

    restAdapter.create(classOf[RestDefinition])
  }
}