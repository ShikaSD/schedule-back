package com.shika.mamk.parser

import java.nio.charset.CodingErrorAction
import java.security.cert.X509Certificate

import com.shika.mamk.rest.model.classes.Event
import org.apache.http.client.methods.HttpRequestBase
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.ssl.{SSLContextBuilder, TrustStrategy}
import org.apache.http.util.EntityUtils
import org.joda.time.DateTimeZone

import scala.io.{Codec, Source}
import scala.util.matching.Regex

package object service {
  //Set default timezone 
  DateTimeZone.setDefault(DateTimeZone.forID("Europe/Helsinki"))
  
  //Dont use this for other purposes (for any you can), please
  val Login    = "oansh006"
  val Password = "t43aZHLn"

  //Addresses for student parser
  val TokenUrl    = "https://student.xamk.fi/_api/contextinfo"
  val CalendarUrl = "https://student.xamk.fi/_layouts/15/CalendarService.ashx"

  val FullDescUrl  = Map(
    Event.Default   -> "https://student.xamk.fi/Lists/Tapahtumat/DispForm.aspx",
    Event.Cancelled -> "https://student.xamk.fi/Lists/PerututTunnit/DispForm.aspx"
  )

  val FullDescSource = Map(
    Event.Default   -> "https://student.xamk.fi/Lists/Tapahtumat/calendar.aspx",
    Event.Cancelled -> "https://student.xamk.fi/Lists/PerututTunnit/calendar.aspx"
  )

  //Addresses for schedule parser
  val ScheduleUrl = "http://tilat.mikkeliamk.fi/kalenterit2/index.php?tiedot=kaikki&av_v=1&guest=%2Fmamk&kt=lk&lang=fin&av="
  val SoleOpsUrl  = "https://soleops.mamk.fi/opsnet/disp/fi/ops_TotsuHaku/tab/fet/sea"

  val GroupUrl = "http://tilat.mikkeliamk.fi/kalenterit2/index.php?kt=lk&guest=%2Fmamk&lang=eng"
  val RoomUrls = Array(
    "http://tilat.mikkeliamk.fi/kalenterit2/index.php?kt=tila%2C9410&laji=Kasarmi%2Fluokat%7C%7CKas&guest=%2Fmamk&lang=eng",
    "http://tilat.mikkeliamk.fi/kalenterit2/index.php?kt=tila%2C9410&laji=Kasarmi%2Fmuut_tilat%7C%7CKas&guest=%2Fmamk&lang=eng",
    "http://tilat.mikkeliamk.fi/kalenterit2/index.php?kt=tila%2C9410&laji=Savonlinna%2Fluokat%7C%7CSln&guest=%2Fmamk&lang=eng",
    "http://tilat.mikkeliamk.fi/kalenterit2/index.php?kt=tila%2C9410&laji=Savonlinna%2Fmuut_tilat%7C%7CSln&guest=%2Fmamk&lang=eng",
    "http://tilat.mikkeliamk.fi/kalenterit2/index.php?kt=tila%2C9410&laji=STK%2FElixiiri%7C%7CSTK&guest=%2Fmamk&lang=eng")

  //Amount of parsing
  val WeeksToParse = 24
  val MonthsToParse = 6

  //Define default context for https connections
  val sslContext = new SSLContextBuilder().loadTrustMaterial(new TrustStrategy {
    def isTrusted(arg0: Array[X509Certificate], arg1: String): Boolean = true
  }).build

  //Define default codec for requests
  implicit val codec = Codec("ISO-8859-1")
  codec.onMalformedInput(CodingErrorAction.REPLACE)
  codec.onUnmappableCharacter(CodingErrorAction.REPLACE)

  //Extensions for easier implementations of things
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
