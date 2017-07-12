Feature: Patient API 
    Server has patient create api
    Server has patient search api
    Server has patient read api
    Server has patient update api
    Server has patient patch api
    Server has patient delete api
  
Scenario: Create a new patient
    When I create a patient with family name "Foo" and given name "Bar"
    Then the server has response content "Created Patient null" and code 200

Scenario: Search a patient
    When I search a patient with family name "Foo" and given name "Bar"
    Then the server has response content "Search Patient " and code 200

Scenario: Read a patient
    When I read a patient with id 1
    Then the server has response content "Read Patient 1" and code 200

Scenario: Update a patient
    When I update a patient with id 1 and family name "Foo", given name "Bar"
    Then the server has response content "Update Patient 1" and code 200

Scenario: Patch a patient
    When I patch a patient with id 1 and family name "Foo", given name "Bar"
    Then the server has response content "Patch Patient 1" and code 200

Scenario: Delete a patient
    When I delete a patient with id 1
    Then the server has response content "Delete Patient 1" and code 200