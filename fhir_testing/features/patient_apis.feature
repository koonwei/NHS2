Feature: Patient API 
    Server has patient create api
    Server has patient search api
    Server has patient read api
    Server has patient update api
    Server has patient patch api
    Server has patient delete api

Background:
    Given fixture "patient.json" has loaded as "patient"
    Given a patient was created with "patient"

Scenario: Create a new patient
    When I create a patient with family name "Foo" and given name "Bar"
    Then The server response has status code 201
    And The server response has the patient id in the location header
    And The server has a patient stored with this id, family name "Foo", and given name "Bar" 

Scenario: Read a patient
    When I read the patient created
    Then The server response has status code 200
    And The server response has a body with the same id, family name "Smith", and given name "Richard"

Scenario: Search patients without parameters
   When I search patients
   Then The server response has status code 200
   And the response has json key "entry" 

Scenario: Search patients by family name and given name
   When I search patients with family name "Smith" and given name "Richard"
   Then The server response has status code 200
   And the response has json key "entry"

# Scenario: Update a patient
#    When I update the patient created
#    Then The server response has status code 200
#    And The server response has the patient id in the location header
#    And The server has a patient stored with this id, family name "Foo", and given name "Bar" 

#Scenario: Patch a patient
#    When I patch a patient with id 1 and family name "Foo", given name "Bar"
#    Then the server response has json key "message"
#    And has status code 200

Scenario: Delete a patient
    When I delete the patient created
    Then The server response has status code 204
    And The server has no patient stored with this id 

