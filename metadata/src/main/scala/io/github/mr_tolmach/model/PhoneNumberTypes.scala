package io.github.mr_tolmach.metadata.model

object PhoneNumberTypes extends Enumeration {

  type PhoneNumberType = Value

  val FixedLine = Value
  val FixedLineOrMobile = Value
  val Mobile = Value
  val Pager = Value
  val PersonalNumber = Value
  val PremiumRate = Value
  val SharedCost = Value
  val TollFree = Value
  val UAN = Value
  val VoIP = Value
  val Voicemail = Value

  val All: Set[PhoneNumberType] = values

}


