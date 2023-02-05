<p align="center">
  <img src="other/assets/raf-logo.png">
</p>

# What is it?

Open-source library for generating valid phone numbers in the E.164 format. Based on [libphonenumber](https://github.com/google/libphonenumber) metadata but with some upgrades :)

# Why not just use the libphonenumber?

There are several reasons for this:
* The regexs from libphonenumber cannot be used as is due to errors inside them. For example, [the problem with the phone number with premium rates in Belarus has not yet been fixed](https://issuetracker.google.com/issues/227765488).
* The libphonenumber doesn't provide an API for generating valid phone numbers by default. Using [getExampleNumber](https://github.com/google/libphonenumber/blob/99a44a6ab50ccbd7654de6b6c803bd81977e9e99/java/libphonenumber/src/com/google/i18n/phonenumbers/PhoneNumberUtil.java#L2016), you can only get one example for each type of phone number in each region.

# Ok, fine, how to use it?

First, find out which version is needed by looking at [VERSIONS.md](VERSIONS.md).

:bangbang: **The version of libphonenumber in your project SHOULD BE EQUAL to the version of libphonenumber in the raf library** Otherwise there is no guarantee that the generated phones will be correctly parsed by libphonenumer. Be very careful here! :bangbang:

Then, add the following dependency:
```scala
// rafVersion is the version that you found in the previous step
"io.github.mr-tolmach" %% "raf-generators" % rafVersion
```

Finally, use it in you code:
```scala
import io.github.mr_tolmach.generators.E164Generators
import io.github.mr_tolmach.metadata.model.Regions

// for scalatest + scalacheck property-based testing
forAll(E164Generators.phoneNumberGen(Regions.AW))(check)

// for generating valid phone numbers
E164Generators.phoneNumberGen(Regions.AC).sample
```