#!/bin/bash

# kill -9 `cat .saved_pid 2>/dev/null` 2>/dev/null && rm .saved_pid -f 

gradle -p ../FHIR-MAPPING appStop 2>/dev/null