package io.github.mr_tolmach.metadata.model

object PhoneNumberTypes extends Enumeration {

  type PhoneNumberType = Value

  val VoIP = Value
  val Voicemail = Value
  val UAN = Value
  val Pager = Value
  val TollFree = Value
  val SharedCost = Value
  val Mobile = Value
  val PersonalNumber = Value
  val FixedLine = Value
  val PremiumRate = Value
  val FixedLineOrMobile = Value

  val All: Set[PhoneNumberType] = values

}


