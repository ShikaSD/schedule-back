package com.shika.mamk.parser

object AppKeys {
  val keys = Seq(
    Key(
      appId = "ZLlTnDgU2S3xCdSLe5VbNdlGEW1BAhXHAJQdEvGg",
      key = "FzHSVSCdGTjG7SDcVo01dpdq8PCo0K3PDCQTutAA"
    )
    //Add other keys there
  )
}

case class Key (
  appId: String,
  key: String
)
