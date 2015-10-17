package com.shika.mamk.parser

import java.nio.charset.CodingErrorAction
import java.security.cert.X509Certificate

import com.shika.mamk.rest.model.classes.Event
import org.apache.http.ssl.{TrustStrategy, SSLContextBuilder}

import scala.io.Codec

package object parser {
  //Dont use this for other purposes (for any you can), please
  val Login    = "oansh006"
  val Password = "t43aZHLn"

  val TokenUrl    = "https://student.xamk.fi/_api/contextinfo"
  val CalendarUrl = "https://student.xamk.fi/_layouts/15/CalendarService.ashx"

  val FullDescUrl  = Map(
    Event.Default   -> "https://student.xamk.fi/Lists/Tapahtumat/DispForm.aspx",
    Event.Cancelled -> "https://student.xamk.fi/Lists/PerututTunnit/DispForm.aspx"
  )

  val FullDescSource = Map(
    Event.Cancelled -> "https://student.xamk.fi/Lists/PerututTunnit/calendar.aspx",
    Event.Default   -> "https://student.xamk.fi/Lists/Tapahtumat/calendar.aspx"
  )

  val EventListName     = "2123cc38-41fc-41c2-9fd0-92d8dd3cedf9"
  val EventViewName     = "e71b1911-c604-4c5b-86de-4e2475031945"
  val CancelledListName = "af485b8e-ef45-443c-a80d-4898d90947fd"
  val CancelledViewName = "904b1bec-ac88-46ff-8da4-d966a6ee5d32"

  val ScheduleUrl = "http://tilat.mikkeliamk.fi/kalenterit2/index.php?tiedot=kaikki&av_v=1&guest=%2Fmamk&kt=lk&lang=fin&av="
  val SoleOpsUrl  = "https://soleops.mamk.fi/opsnet/disp/fi/ops_TotsuHaku/tab/fet/sea"

  val GroupUrl = "http://tilat.mikkeliamk.fi/kalenterit2/index.php?kt=lk&guest=%2Fmamk&lang=eng"
  val RoomUrls = Array(
    "http://tilat.mikkeliamk.fi/kalenterit2/index.php?kt=tila%2C9410&laji=Kasarmi%2Fluokat%7C%7CKas&guest=%2Fmamk&lang=eng",
    "http://tilat.mikkeliamk.fi/kalenterit2/index.php?kt=tila%2C9410&laji=Kasarmi%2Fmuut_tilat%7C%7CKas&guest=%2Fmamk&lang=eng",
    "http://tilat.mikkeliamk.fi/kalenterit2/index.php?kt=tila%2C9410&laji=Savonlinna%2Fluokat%7C%7CSln&guest=%2Fmamk&lang=eng",
    "http://tilat.mikkeliamk.fi/kalenterit2/index.php?kt=tila%2C9410&laji=Savonlinna%2Fmuut_tilat%7C%7CSln&guest=%2Fmamk&lang=eng",
    "http://tilat.mikkeliamk.fi/kalenterit2/index.php?kt=tila%2C9410&laji=STK%2FElixiiri%7C%7CSTK&guest=%2Fmamk&lang=eng")

  implicit val codec = Codec("ISO-8859-1")
  codec.onMalformedInput(CodingErrorAction.REPLACE)
  codec.onUnmappableCharacter(CodingErrorAction.REPLACE)

  val weeksToParse = 24
  val monthsToParse = 6

  val sslContext = new SSLContextBuilder().loadTrustMaterial(new TrustStrategy {
    def isTrusted(arg0: Array[X509Certificate], arg1: String): Boolean = true
  }).build
}
