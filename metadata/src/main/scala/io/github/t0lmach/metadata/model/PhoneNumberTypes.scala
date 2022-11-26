package io.github.t0lmach.metadata.model

object PhoneNumberTypes extends Enumeration {

  type PhoneNumberType = Value

  val FixedLine, FixedLineOrMobile, Mobile, Pager, PersonalNumber, PremiumRate, SharedCost, TollFree, UAN, VoIP, Voicemail = Value

  val All: Set[PhoneNumberType] = values

}


