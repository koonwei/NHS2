# NHS2 - SMART-on-FHIR-on-openEHR

[![Build Status](https://travis-ci.org/koonwei/NHS2.svg?branch=master)](https://travis-ci.org/koonwei/NHS2)
[![codecov](https://codecov.io/gh/koonwei/NHS2/branch/master/graph/badge.svg)](https://codecov.io/gh/koonwei/NHS2)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/8262f447922a4384b8f858c1b978fac2)](https://www.codacy.com/app/Yuan-W/NHS2?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=koonwei/NHS2&amp;utm_campaign=Badge_Grade)


An implementation of SMART-on-FHIR for openEHR. This project aims to provide a FHIR facade for openEHR.

## Repository Structure
### FHIR-MAPPING
FHIR facade for openEMPI and Think!EHR/etherCIS

### evaluation
Evaluation scripts for `FHIR-MAPPING`

### cookbooks
Chef cookbooks to provision the development environment.

### fhir_testing
End-to-end cucumber testing scripts in Ruby.

### launcher
Node.js application to view all patients and show growth chart apps. To establish the connection, Growth chart app must be installed locally.

### pathgenerater
XQuery scripts to parse openEHR archetype paths from XSD template files.



