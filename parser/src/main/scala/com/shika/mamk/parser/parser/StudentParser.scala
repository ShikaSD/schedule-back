package com.shika.mamk.parser.parser

class StudentParser {
  /*try {
    val requestConfig = RequestConfig.custom()
      .setSocketTimeout(1000000)
      .setConnectTimeout(1000000)
      .setTargetPreferredAuthSchemes(util.Arrays.asList(AuthSchemes.NTLM))
      .setProxyPreferredAuthSchemes(util.Arrays.asList(AuthSchemes.BASIC))
      .build()

    val credentialsProvider = new BasicCredentialsProvider()
    credentialsProvider.setCredentials(AuthScope.ANY, new NTCredentials("oansh006", "t43aZHLn", "", ""))

    // Finally we instantiate the client. Client is a thread safe object and can be used by several threads at the same time.
    // Client can be used for several request. The life span of the client must be equal to the life span of this EJB.
    val httpClient = HttpClients.custom()
      .setDefaultCredentialsProvider(credentialsProvider)
      .setDefaultRequestConfig(requestConfig)
      .build()

    val pageReq = new HttpPost("https://student.xamk.fi/_api/contextinfo")
    val page = httpClient.execute(pageReq)

    println(s"STATUS: ${page.getStatusLine}")
    println("HEADERS:")
    page.getAllHeaders.foreach {header =>
      println(s"${header.getName} ${header.getValue}")
    }
    val rdigest =
      Source.fromInputStream(page.getEntity.getContent).mkString
        .search("<d:FormDigestValue>(.*?)</".r).get
    println(rdigest)

    val request = new HttpPost("https://student.xamk.fi/_layouts/15/CalendarService.ashx")
    request.setHeader("X-RequestDigest", rdigest)

    val params = Seq(
      new BasicNameValuePair("cmd", "query"),
      new BasicNameValuePair("viewType", "month"),
      new BasicNameValuePair("selectedDate", "1.10.2015"),
      new BasicNameValuePair("options", "63"),
      new BasicNameValuePair("listName", "af485b8e-ef45-443c-a80d-4898d90947fd"),
      new BasicNameValuePair("viewName", "904b1bec-ac88-46ff-8da4-d966a6ee5d32"),
      new BasicNameValuePair("entity", "0;#0"),
      new BasicNameValuePair("dataSourceId", "00000000-0000-0000-0000-000000000000")
    ).asJava
    request.setEntity(new UrlEncodedFormEntity(params))

    println("REQUEST HEADERS:")
    request.getAllHeaders.foreach {header =>
      println(s"${header.getName} ${header.getValue}")
    }
    val response = httpClient.execute(request)

    println(s"STATUS: ${response.getStatusLine}")
    println("HEADERS:")
    response.getAllHeaders.foreach {header =>
      println(s"${header.getName} ${header.getValue}")
    }
    val data = JsonHelper.fromJson[TempObj](
      Source.fromInputStream(response.getEntity.getContent).mkString
    )
    println(data.Items.Strings)
  }
  catch {
    case e: Exception => e.printStackTrace()
  }*/
}
