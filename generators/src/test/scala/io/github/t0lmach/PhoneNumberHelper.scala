package io.github.t0lmach

import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber
import io.github.t0lmach.metadata.model.Regions.Region

import scala.jdk.CollectionConverters._

object PhoneNumberHelper {

  val Util = PhoneNumberUtil.getInstance()

  def parseValidPhoneNumber(phoneNumber: String, region: Region): PhoneNumber = {
    val parsed = Util.parse(phoneNumber, region.toString)
    if (Util.isValidNumber(parsed)) {
      parsed
    } else {
      throw new IllegalArgumentException(s"$phoneNumber is not a valid phone number")
    }
  }

  def parseValidPhoneNumber(phoneNumber: String): PhoneNumber = {
    val parsed = Util.parse(phoneNumber, null)
    if (Util.isValidNumber(parsed)) {
      parsed
    } else {
      throw new IllegalArgumentException(s"$phoneNumber is not a valid phone number")
    }
  }

}
