require "selenium-webdriver"

driver = Selenium::WebDriver.for :chrome
driver.navigate.to "http://51.140.57.74:3000/growth-chart/id/1"

sleep(10)
patient_name = driver.find_element(:css, "span.patient-name.title")
puts patient_name.text

puts driver.title

