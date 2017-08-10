Feature: Observation API 
   Server has observation search api

Background:
   Given fixture "observationBundle.json" has loaded as "observations"
   Given fixture "patient.json" has loaded as "patient"
   Given a patient was created using "patient" fixture, with family name "Evanthia", given name "Tingiri", gender "female", birthDate "1994-04-06", and NHS identifier "TEST-1234"
   Given an EHRid exists for the subjec id "TEST-12345"
   Given Create composition for "weight" observation with value "10" and date "1957-01-01T02:20:00Z"
   Given Create composition for "weight" observation with value "15" and date "1957-12-01T02:20:00Z"
   Given Create composition for "height" observation with value "90" and date "1957-01-01T02:20:00Z"
   Given Create composition for "height" observation with value "95" and date "1957-12-01T02:20:00Z"
   Given Create composition for "bmi" observation with value "21.15" and date "1957-04-02T18:00:00Z"

Scenario: Search Height, Weight, BMI and Head Circumference Observations of a Subject Id
   When I search Height, Weight, BMI, Head Circumference observation with Subject Id "2"
   Then The server response has status code 200
   And the response is a bundle that contains the Observations created

