require "selenium-webdriver"
require "test/unit"

extend Test::Unit::Assertions

driver = Selenium::WebDriver.for :chrome
driver.navigate.to "http://51.140.57.74:3000/growth-chart/id/1"


#maybe the method "wait" from selenium gem can be used to stop execution until some element appears
sleep(2)

patient_name = driver.find_element(:css, "span.patient-name.title")
assert_equal patient_name.text, "Keira Jones", "Patient name retrieved incorrectly"

patient_gender = driver.find_element(:css, "span.patient-gender.title")
assert_equal patient_gender.text, "female", "Patient gender retrieved incorrectly"

patient_dob = driver.find_element(:css, "span.patient-birth.title")
assert_equal patient_dob.text, "23Mar2009", "Patient date of birth retrieved incorrectly"

puts "Data Retrieved Successfully"


