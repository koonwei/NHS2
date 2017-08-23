require "selenium-webdriver"
require "test/unit"

extend Test::Unit::Assertions

driver = Selenium::WebDriver.for :chrome
driver.navigate.to "http://51.140.57.74:3000/growth-chart/id/1"

#maybe the method "wait" from selenium gem can be used to stop execution until some element appears
sleep(10)
patient_name = driver.find_element(:css, "span.patient-name.title")
assert_equal patient_name.text, "Keira Jones"
puts "Data Retrieved Successfully"

