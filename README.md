<p align="center">
  <img src="other/assets/raf-logo.png">
</p>

# What's it?

Open-source library for generating valid phone numbers in the E.164 format based on upgraded metadata from [libphonenumber](https://github.com/google/libphonenumber)

# Why not just use the libphonenumber?

There are several reasons for this:
* The libphonenumber doesn't provide an API for generating valid phone numbers by default. Using [getExampleNumber](https://github.com/google/libphonenumber/blob/99a44a6ab50ccbd7654de6b6c803bd81977e9e99/java/libphonenumber/src/com/google/i18n/phonenumbers/PhoneNumberUtil.java#L2016), you can only get one example for each type of phone number in each region.
* The regexs from libphonenumber cannot be used as is due to errors inside them. For example, [the problem with the premium rate phone numbers in Belarus has not yet been fixed](https://issuetracker.google.com/issues/227765488).
* Unnecessary duplication of patterns or parts of patterns in regions with the same country code. For example, the JE national part pattern for personal phone numbers (`701511\d{4}`) is a subset of the GB national part pattern for personal phone numbers (`70\d{8}`) in libphonenumber 8.13.5

# Ok, fine, how to use it?

First, find out which version is needed by looking at [VERSIONS.md](VERSIONS.md).

:bangbang: **The version of libphonenumber in your project SHOULD BE EQUAL to the version of libphonenumber in the raf library**. Otherwise there is no guarantee that the generated phones will be correctly parsed by libphonenumber. Be very careful here! :bangbang:

Then, add the following dependency:
```scala
// rafVersion is the version that you found in the previous step
"io.github.mr-tolmach" %% "raf-generators" % rafVersion
```

Finally, use it in your code:
```scala
import io.github.mr_tolmach.generators.E164Generators
import io.github.mr_tolmach.metadata.model.Regions
import io.github.mr_tolmach.metadata.model.PhoneNumberTypes

// for scalatest + scalacheck property-based testing
forAll(E164Generators.phoneNumberGen(Regions.AW))(check)

// to generate valid phone numbers for the AC region with any type of phone number
E164Generators.phoneNumberGen(Regions.AC).sample

// to generate valid fixed-line phone numbers for the US region
E164Generators.phoneNumberGen(Regions.US, PhoneNumberTypes.FixedLine).sample
```
