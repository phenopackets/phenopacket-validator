[![Build Status](https://api.travis-ci.com/phenopackets/phenopacket-validator.svg?branch=master)](https://api.travis-ci.com/phenopackets/phenopacket-validator.svg?branch=master)

# Phenopacket Validator
Library and tools to help validate phenopackets


## Setup

This app uses version 2.0.0 (branch v2) of [phenopacket-schema](https://github.com/phenopackets/phenopacket-schema).
To build this app, clone phenopacket-schema and ``mvn install`` the ``v2`` branch locally. The you should be
able to build and run this app with standard maven/java. Note the app requires Java 11 or higher.
 
It also uses version 0.0.1-SNAPSHOT of [phenopacket-tools](https://github.com/phenopackets/phenopacket-tools)
to create the test cases. It also needs to be install locally using maven.

We plan to put all requirements into maven central shortly to simplify the build.