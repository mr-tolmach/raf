package io.github.mr_tolmach.metadata.model

/** An enumeration representing the set of supported phone number types.
  */
object PhoneNumberTypes extends Enumeration {

  /** Type alias for values of the [[PhoneNumberTypes]] enumeration. */
  type PhoneNumberType = Value

  /** Value representing a Voice over IP phone number type. This includes TSoIP (Telephony Service over IP).
    *
    * @see
    *   [[https://en.wikipedia.org/wiki/Voice_over_IP]] for more information.
    */
  val VoIP = Value(1)

  /** Value representing a Voice Mail Access Numbers.
    *
    * @see
    *   [[https://en.wikipedia.org/wiki/Voicemail]] for more information.
    */
  val Voicemail = Value(2)

  /** Value representing "Universal Access Number" or "Company Number" phone number type. They may be further routed to
    * specific offices, but allow one number to be used for a company.
    *
    * @see
    *   [[https://www.itu.int/rec/dologin_pub.asp?lang=f&id=T-REC-Q.1211-199303-I!!PDF-E&type=items ITU-T Recommendation Q.1211]]
    *   for more information.
    */
  val UAN = Value(3)

  /** Value representing a pager phone number type.
    *
    * @see
    *   [[https://en.wikipedia.org/wiki/Pager]] for more information.
    */
  val Pager = Value(4)

  /** Value representing freephone lines phone number type.
    *
    * @see
    *   [[https://en.wikipedia.org/wiki/Toll-free_telephone_number]] for more information.
    */
  val TollFree = Value(5)

  /** Value representing a shared cost phone number type.
    *
    * @see
    *   [[http://en.wikipedia.org/wiki/Shared_Cost_Service]] for more information.
    */
  val SharedCost = Value(6)

  /** Value representing a mobile phone number type.
    *
    * @see
    *   [[https://en.wikipedia.org/wiki/Mobile_phone]] for more information.
    */
  val Mobile = Value(7)

  /** Value representing a personal number phone type which is associated with a particular person, and may be routed to
    * either a [[Mobile]] or [[FixedLine]] number.
    *
    * @see
    *   [[http://en.wikipedia.org/wiki/Personal_Numbers]] for more information.
    */
  val PersonalNumber = Value(8)

  /** Value represents a fixed-line (also known as landline) phone number type.
    *
    * @see
    *   [[https://en.wikipedia.org/wiki/Landline]] for more information.
    */
  val FixedLine = Value(9)

  /** Value represents a premium-rate phone number type.
    *
    * @see
    *   [[https://en.wikipedia.org/wiki/Premium-rate_telephone_number]] for more information.
    */
  val PremiumRate = Value(10)

  /** Value represents a phone number type that cannot be differentiated between a [[FixedLine]] and a [[Mobile]] phone
    * type just by looking at the phone number itself, such as in the case of the USA.
    *
    * @see
    *   [[FixedLine]]
    * @see
    *   [[Mobile]]
    */
  val FixedLineOrMobile = Value(11)

  /** Set of all values in the [[PhoneNumberTypes]] enumeration */
  val All: Set[PhoneNumberType] = values

}
