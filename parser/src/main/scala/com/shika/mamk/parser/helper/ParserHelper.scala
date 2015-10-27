package com.shika.mamk.parser.helper

import com.shika.mamk.parser.model.StudentEvent
import com.shika.mamk.parser.service._
import com.shika.mamk.rest.helper.JsonHelper
import com.shika.mamk.rest.model.ParseDate
import com.shika.mamk.rest.model.classes.{Course, Lesson}
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.{HttpPost, HttpRequestBase}
import org.apache.http.impl.client.{CloseableHttpClient, HttpClients}
import org.apache.http.message.BasicNameValuePair
import org.apache.http.util.EntityUtils
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

import scala.collection.JavaConverters._
import scala.io.Source
import scala.util.matching.Regex

object ParserHelper {
  private val soleOpsDateFormat = DateTimeFormat.forPattern("dd.MM.yyyy")

  def getHtml(url: String) = {
    Source.fromURL(url).mkString
  }

  def formUrls(name: String, start: DateTime): Array[(Int, String)] = {
    (0 to weeksToParse) map { num =>
      (num + 1, start.plusWeeks(num))
    } map { tuple =>
      val dString = tuple._2.toString(DateTimeFormat.forPattern("yyMMdd"))
      (tuple._1, ScheduleUrl + s"$dString$dString$dString&cluokka=$name")
    } toArray
  }

  def getCourseId(string: String) = {
    val addressPattern = "\\.\\.(.*?)\'".r
    val namePattern = "<th>Selite[\\s\\S]*?<tr[\\s\\S]*?<td>.*?<td>.*?<td>.*?<td>.*?<td>(.*?)</td>".r
    val cidPattern = "([A-Z0-9]{4,} )".r
    val url = "http://tilat.mikkeliamk.fi" + string.search(addressPattern).get

    val html = getHtml(url)
    val name = html.search(namePattern).get
    name.search(cidPattern)
      .getOrElse(name)
      .replaceAll(" ", "")
  }

  def getCourse(lesson: Lesson) = {
    val httpClient = HttpClients.custom()
      .setSSLContext(sslContext)
      .build

    val coursePattern = (
      """<td width="1%"[\s\S]*?showmode">(.*?)</div>[\s\S]*?</td>[\s\S]*?""" +
      """<td[\s\S]*?showmode">(.*?)</div>[\s\S]*?</td>[\s\S]*?""" +
      """<td[\s\S]*?</td>[\s\S]*?""" +
      """<td[\s\S]*?>(.*?)</td>[\s\S]*?""" +
      """<td[\s\S]*?</td>[\s\S]*?""" +
      """<td[\s\S]*?</td>[\s\S]*?""" +
      """<td[\s\S]*?showmode">(.*?)</div>[\s\S]*?</td>""").r

    try {
      val request = new HttpPost(SoleOpsUrl)
      val data = new UrlEncodedFormEntity(
        Seq(
          new BasicNameValuePair("ojtunnus", lesson.courseId),
          new BasicNameValuePair("ryhma", lesson.group)
        ).asJava
      )
      request.setEntity(data)
      request.setHeader("Content-Type", "application/x-www-form-urlencoded")
      request.setHeader("Accept", "text/html")

      val body = httpClient.getResponse(request)

      for (m <- coursePattern findFirstMatchIn body)
        yield {
          val dates = (m group 3).split("-").map(s => Some( ParseDate(soleOpsDateFormat.parseDateTime(s)) ))
          Course(
            courseId = m group 1,
            name = m group 2 replaceAll("\\&auml;", "ä") replaceAll("\\&ouml;", "ö"),
            start = dates(0),
            end = dates(1),
            group = m group 4,
            parent = true
          )
        }
    } finally {
      httpClient.close()
    }
  }

  def getEvent(response: String) = {
    val eventPattern = """\{"ListData":(\{"ContentType":.*?\}),"ListSchema""".r

    response.search(eventPattern) map JsonHelper.fromJson[StudentEvent]
  }

  implicit class HttpClientOpts(val client: CloseableHttpClient) extends AnyVal {
    def getResponse(request: HttpRequestBase) = {
      val entity = client.execute(request).getEntity
      val result = Source.fromInputStream(entity.getContent).mkString
      EntityUtils.consume(entity)
      result
    }
  }

  implicit class StringOpts(val string: String) extends AnyVal {
    def search(regex: Regex): Option[String] =  for (m <- regex findFirstMatchIn string) yield m group 1
  }
}