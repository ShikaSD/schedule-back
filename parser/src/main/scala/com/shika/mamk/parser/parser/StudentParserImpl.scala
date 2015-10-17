package com.shika.mamk.parser.parser

import java.time.format.DateTimeFormatter

import com.escalatesoft.subcut.inject.{Injectable, BindingModule}
import com.shika.mamk.parser.model.{StudentDataModel, StudentCalendarEvent}
import com.shika.mamk.rest.RestService
import com.shika.mamk.rest.helper.JsonHelper
import com.shika.mamk.rest.model.{ParseDate, Param, QueryParam}
import com.shika.mamk.rest.model.classes.Event
import org.apache.http.auth.{AuthScope, NTCredentials}
import org.apache.http.client.HttpClient
import org.apache.http.client.config.{RequestConfig, AuthSchemes}
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.{HttpRequestBase, HttpGet, HttpPost}
import org.apache.http.client.utils.URIBuilder
import org.apache.http.impl.client.{CloseableHttpClient, HttpClients, BasicCredentialsProvider}
import org.apache.http.message.BasicNameValuePair
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import com.shika.mamk.rest.AppKeys._

import scala.collection.JavaConverters._
import com.shika.mamk.parser.helper.ParserHelper._
import scala.io.Source

class StudentParserImpl (implicit val bindingModule: BindingModule, implicit val startDate: DateTime)
  extends StudentParser with Injectable {

  override def parseEvents:  (Int, Int) = parseCalendar(EventListName, EventViewName, Event.Default)
  override def parseChanges: (Int, Int) = parseCalendar(CancelledListName, CancelledListName, Event.Cancelled)

  private val requestFormatter = DateTimeFormat.forPattern("dd.MM.yyyy")
  private val dateFormatter    = DateTimeFormat.forPattern("dd.MM.yyyy HH:mm")

  private val OptionNumber = "63"

  private lazy val requestConfig = RequestConfig.custom()
    .setSocketTimeout(1000000)
    .setConnectTimeout(1000000)
    .setTargetPreferredAuthSchemes(Seq(AuthSchemes.NTLM).asJava)
    .build()

  private val credentialsProvider = new BasicCredentialsProvider()
  credentialsProvider.setCredentials(AuthScope.ANY, new NTCredentials(Login, Password, "", ""))

  private def getHttpClient = {
    HttpClients.custom()
      .setDefaultCredentialsProvider(credentialsProvider)
      .setDefaultRequestConfig(requestConfig)
      .build()
  }

  //Need to confirm auth
  private def getRequestDigest(httpClient: CloseableHttpClient) = {
    val pageReq = new HttpPost(TokenUrl)
    val page = httpClient.execute(pageReq)
    Source.fromInputStream(page.getEntity.getContent).mkString
          .search("<d:FormDigestValue>(.*?)</".r).get
  }

  private def parseCalendar(listName: String, viewName: String, eventType: String) = {
    val httpClient = getHttpClient
    val rdigest    = getRequestDigest(httpClient)

    var added = 0
    var deleted = 0

    (0 to monthsToParse).map(num => requestFormatter.print(startDate plusMonths num)).foreach {
      date =>
      try {
        val request = new HttpPost(CalendarUrl)
        request.setHeader("X-RequestDigest", rdigest)

        val params = Seq(
          new BasicNameValuePair("cmd", "query"),
          new BasicNameValuePair("viewType", "month"),
          new BasicNameValuePair("selectedDate", date),
          new BasicNameValuePair("options", OptionNumber),
          new BasicNameValuePair("listName", listName),
          new BasicNameValuePair("viewName", viewName)
        ).asJava

        request.setEntity(new UrlEncodedFormEntity(params))

        val entity = JsonHelper.fromJson[StudentCalendarEvent](httpClient.getResponse(request)).Items

        val tuple = sendToNodes (
          entity.Data.map ( parseEvent(httpClient, _, entity.Strings, eventType) )
                     .filter(_.isDefined)
                     .map(_.get)
        )

        added   += tuple._1
        deleted += tuple._2
      }
      catch {
        case e: Exception => e.printStackTrace()
      }
    }
    httpClient.close()

    (added, deleted)
  }

  private def parseEvent(httpClient: CloseableHttpClient, data: Seq[Int], strings: Seq[String], eventType: String) = {
    val id = data map (num => strings(num)) head

    val request = new HttpGet(FullDescUrl(eventType))

    request.asInstanceOf[HttpRequestBase].setURI(
      new URIBuilder(request.getURI)
        .addParameter("ID", id)
        .addParameter("Source", FullDescSource(eventType))
        .build
    )

    getEvent(httpClient.getResponse(request)) map {
      e =>
        Event(
          name        = e.Title,
          description = e.Description,
          start       = Some( ParseDate(dateFormatter.parseDateTime(e.EventDate)) ),
          end         = Some( ParseDate(dateFormatter.parseDateTime(e.EndDate)) ),
          modified    = Some( ParseDate(dateFormatter.parseDateTime(e.Modified)) ),
          eventType   = eventType
        )
    }
  }

  private def sendToNodes(parsed: Seq[Event]) = {
    var added = 0
    var deleted = 0
    keys foreach {
      key =>
        RestService.initialize(key)
        val events = Event query()
        parsed.filter(e => !events.exists(_ equals e))
              .foreach {
                added += 1
                _.create()
              }
        events.filter(e => !parsed.exists(_ equals e))
              .foreach{
                deleted += 1
                _.delete()
              }
    }
    (added, deleted)
  }
}
