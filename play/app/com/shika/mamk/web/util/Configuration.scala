package com.shika.mamk.web.util

import com.escalatesoft.subcut.inject.NewBindingModule
import com.shika.mamk.parser.parser.{StudentParserImpl, StudentParser, ScheduleParser, ScheduleParserImpl}

object Configuration extends NewBindingModule (implicit module => {
  import module._
  // optional but convenient - allows use of bind instead of module.bind

  bind[ScheduleParser] toSingle new ScheduleParserImpl
  bind[StudentParser]  toSingle new StudentParserImpl
})