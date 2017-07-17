Feature: Patient API 
    Server has patient create api
    Server has patient search api
    Server has patient read api
    Server has patient update api
    Server has patient patch api
    Server has patient delete api
  
    #Scenario: Create a new patient
    #When I create a patient with family name "Foo" and given name "Bar"
    #Then The server response has status code 201
    #And A patient is stored with family name "Foo" and given name "Bar" 

Scenario: Search a patient by GET
    When I search a patient with family name "Foo" and given name "Bar"
    Then the server response has json key "entry" 
    And has status code 200

Scenario: Read a patient
	Given The server has a patient stored with id 1, family name "Foo", and given name "Bar"
	When I read a patient with id 1
	Then The server response has status code 200
	And The server response has header parameter with key "location" and value "http://localhost:4567/fhir/Patient/1" 
	#	And The server response has a body with id 1, family name "Foo" and given name "Bar"

Scenario: Read a patient in XML
    When I read a patient with id 1 and format application/xml
    Then the server response has XML tag "entry"
    And has status code 200

Scenario: Read a patient in JSON
    When I read a patient with id 1 and format application/json
    Then the server response has json key "entry"
    And has status code 200

Scenario: Update a patient
    When I update a patient with id 1 and family name "Foo", given name "Bar"
    Then the server response has json key "message"
    And has status code 200

Scenario: Patch a patient
    When I patch a patient with id 1 and family name "Foo", given name "Bar"
    Then the server response has json key "message"
    And has status code 200

# Scenario: Delete a patient
#     When I delete a patient with id 155
#     Then the server response has json key "message"
#     And has status code 400

