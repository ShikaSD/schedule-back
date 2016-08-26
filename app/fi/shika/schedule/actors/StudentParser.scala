package fi.shika.schedule.actors

import akka.actor.Actor
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

import scala.collection.JavaConverters._
import scala.io.Source

/*class StudentParser extends Actor {

  private val requestFormatter = DateTimeFormat.forPattern("dd.MM.yyyy")
  private val dateFormatter    = DateTimeFormat.forPattern("dd.MM.yyyy HH:mm")

  private val OptionNumber = "63"
  private val DataSourceId = "00000000-0000-0000-0000-000000000000"
  private val ViewType     = "month"

  private lazy val requestConfig = RequestConfig.custom()
    .setSocketTimeout(1000000)
    .setConnectTimeout(1000000)
    .setTargetPreferredAuthSchemes(Seq(AuthSchemes.NTLM).asJava)
    .build()

  private val credentialsProvider = new BasicCredentialsProvider()
  credentialsProvider.setCredentials(AuthScope.ANY, new NTCredentials(Login, Password, "", ""))

  override def parseEvents (implicit startDate: DateTime) =
    parseCalendar(
      getViewNames(FullDescSource(Event.Default)),
      Event.Default
    )

  override def parseChanges(implicit startDate: DateTime) =
    parseCalendar(
      getViewNames(FullDescSource(Event.Cancelled)),
      Event.Cancelled
    )

  private def getHttpClient = {
    HttpClients.custom()
      .setDefaultCredentialsProvider(credentialsProvider)
      .setDefaultRequestConfig(requestConfig)
      .build()
  }

  def getViewNames(url: String) = {
    val listNameRegex = """pageListId:"\{(.*?)\}"""".r
    val viewNameRegex = """storageId="(.*?)";""".r

    val httpClient = getHttpClient
    val request = new HttpGet(url)

    val response = httpClient.getResponse(request)

    val listName = response.search(listNameRegex).getOrElse("")
    val viewName = response.search(viewNameRegex).getOrElse("")

    (listName, viewName)
  }

  private def getEvent(response: String) = {
    val eventPattern = """\{"ListData":(\{"ContentType":.*?\}),"ListSchema""".r

    response.search(eventPattern) map JsonHelper.fromJson[StudentEvent]
  }

  private def getRequestDigest(httpClient: CloseableHttpClient) = {
    val pageReq = new HttpPost(TokenUrl)
    val page = httpClient.execute(pageReq)
    Source.fromInputStream(page.getEntity.getContent).mkString
      .search("<d:FormDigestValue>(.*?)</".r).get
  }

  private def parseCalendar(params: (String, String), eventType: String)(implicit startDate: DateTime) = {
    val (listName, viewName) = params

    val httpClient = getHttpClient
    val rdigest    = getRequestDigest(httpClient)

    var added = 0
    var deleted = 0

    (0 to MonthsToParse).map(num => requestFormatter.print(startDate plusMonths num)).foreach {
      date =>
      try {
        val request = new HttpPost(CalendarUrl)
        request.setHeader("X-RequestDigest", rdigest)

        val params = Seq(
          new BasicNameValuePair("cmd",           "query"),
          new BasicNameValuePair("viewType",      ViewType),
          new BasicNameValuePair("selectedDate",  date),
          new BasicNameValuePair("options",       OptionNumber),
          new BasicNameValuePair("dataSourceId",  DataSourceId),
          new BasicNameValuePair("listName",      listName),
          new BasicNameValuePair("viewName",      viewName)
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
    val id = strings (data head)

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
          identifier  = id.toLong,
          name        = e.Title,
          description = e.Description,
          start       = ParseDate(dateFormatter.parseDateTime(e.EventDate)),
          end         = ParseDate(dateFormatter.parseDateTime(e.EndDate)),
          modified    = ParseDate(dateFormatter.parseDateTime(e.Modified)),
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
        val created = parsed.filter(e => !events.exists(_ equals e))
          .map {
            added += 1
            _.create
          }

        events.filter(e => created.exists(_.identifier == e.identifier))
              .foreach {
                deleted += 1
                _.delete
              }
    }
    (added, deleted)
  }
}
*/