Feature: Observation API 
   Server has observation search api

Scenario: Search Height, Weight, BMI and Head Circumference Observations of a Subject Id
   When I search Height, Weight, BMI, Head Circumference observation with Subject Id "SMART-1482713"
   Then The server response has status code 200
   And the response is a bundle with patients that have family name "Foo" and give name "Bar"
   And the response is a bundle that contains the first patient created


