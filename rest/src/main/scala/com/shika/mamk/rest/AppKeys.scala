package com.shika.mamk.rest

object AppKeys {
  //Keys for parser
  val keys = Seq(
    //Test
    Key(
      appName = "Test Environment",
      appId = "ZLlTnDgU2S3xCdSLe5VbNdlGEW1BAhXHAJQdEvGg",
      restApiKey = "FzHSVSCdGTjG7SDcVo01dpdq8PCo0K3PDCQTutAA",
      masterKey = "j2DkcB83qz0nLRZ1Q6JtnpBCU8fx18ARzLwfTZXw"
    )/*,
    //Production
    Key(
      appName = "Android production v0.5"
      appId = "eR4X3CWg0H0dQiykPaWPymOLuceIj7XlCWu3SLLi",
      restApiKey = "25f0GzM2eHCVFN5sN3Gp4KYso0DETyCi3QWBKaby",
      masterKey = "AafJe4bHembFbmp1GxJzobtsLTNQE4u3SXAhgDB0"
    )*/
    //Add other keys there
  )
}

case class Key (
  appName: String = "",
  appId: String,
  restApiKey: String,
  masterKey: String
)
