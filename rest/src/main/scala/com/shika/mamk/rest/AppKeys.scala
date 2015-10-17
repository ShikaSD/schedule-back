package com.shika.mamk.rest

object AppKeys {
  //Keys for parser
  val keys = Seq(
    //Test
    Key(
      appId = "ZLlTnDgU2S3xCdSLe5VbNdlGEW1BAhXHAJQdEvGg",
      restApiKey = "FzHSVSCdGTjG7SDcVo01dpdq8PCo0K3PDCQTutAA",
      masterKey = "j2DkcB83qz0nLRZ1Q6JtnpBCU8fx18ARzLwfTZXw"
    )/*,
    //Production
    Key(
      appId = "eR4X3CWg0H0dQiykPaWPymOLuceIj7XlCWu3SLLi",
      restApiKey = "25f0GzM2eHCVFN5sN3Gp4KYso0DETyCi3QWBKaby",
      masterKey = "AafJe4bHembFbmp1GxJzobtsLTNQE4u3SXAhgDB0"
    )*/
    //Add other keys there
  )

  //Keys for comparing entities
  val testKeys = Seq(
    Key(
      appName = "Test",
      appId = "ZLlTnDgU2S3xCdSLe5VbNdlGEW1BAhXHAJQdEvGg",
      restApiKey = "FzHSVSCdGTjG7SDcVo01dpdq8PCo0K3PDCQTutAA",
      masterKey = "j2DkcB83qz0nLRZ1Q6JtnpBCU8fx18ARzLwfTZXw"
    ),
    Key(
      appName = "Production",
      appId = "eR4X3CWg0H0dQiykPaWPymOLuceIj7XlCWu3SLLi",
      restApiKey = "25f0GzM2eHCVFN5sN3Gp4KYso0DETyCi3QWBKaby",
      masterKey = "AafJe4bHembFbmp1GxJzobtsLTNQE4u3SXAhgDB0"
    )
  )
}

case class Key (
  appName: String = "",
  appId: String,
  restApiKey: String,
  masterKey: String
)
