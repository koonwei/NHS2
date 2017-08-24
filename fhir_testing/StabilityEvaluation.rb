require "selenium-webdriver"
require "test/unit"

extend Test::Unit::Assertions

length_expected_list = ["177", "82.3", "86.3", "90.1cm"]
weight_expected_list = ["9.8", "10.9", "11.9", "12.7kg"]

def check_heading_elements(heading, expected_elements)
	obtained_elements = heading.find_elements(:css, "td")
	obtained_elements.zip(expected_elements).each do |obtained_element, expected_element|
		assert_equal obtained_element.text, expected_element 
	end
end

driver = Selenium::WebDriver.for :chrome
driver.navigate.to "http://51.140.57.74:3000/growth-chart/id/1"

sleep(4)

patient_name = driver.find_element(:css, "span.patient-name.title")
assert_equal patient_name.text, "Keira Jones", "Patient name retrieved incorrectly"

patient_gender = driver.find_element(:css, "span.patient-gender.title")
assert_equal patient_gender.text, "female", "Patient gender retrieved incorrectly"

patient_dob = driver.find_element(:css, "span.patient-birth.title")
assert_equal patient_dob.text, "23Mar2009", "Patient date of birth retrieved incorrectly"


driver.find_element(:xpath, '//span[@data-value="table"]').click

length_heading = driver.find_element(:css, "tr.length.heading")
check_heading_elements(length_heading, length_expected_list)

weight_heading = driver.find_element(:css, "tr.weight.heading")
check_heading_elements(weight_heading, weight_expected_list)

puts "Data Retrieved Successfully"


