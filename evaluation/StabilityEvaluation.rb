require "selenium-webdriver"
require "test/unit"

extend Test::Unit::Assertions

EXPECTED_NUMBER_OF_EXECUTIONS = 1 
ERROR_MESSAGE = "%{value_type} retrieved by GCA from NHS platform incorrect in execution number %{execution_number}"
length_expected_list = ["177", "82.3", "86.3", "90.1cm"]
weight_expected_list = ["9.8", "10.9", "11.9", "12.7kg"]
head_circ_expected_list = ["50", "52", "52", "52cm"]
bmi_expected_list = ["17.2", "—", "—", "—"]

def check_heading_elements(heading, expected_elements, value_type)
	obtained_elements = heading.find_elements(:css, "td")
	obtained_elements.zip(expected_elements).each do |obtained_element, expected_element|
		assert_equal obtained_element.text, expected_element, ERROR_MESSAGE % {:execution_number => @number_of_executions, :value_type => value_type} 
	end
end

@number_of_executions = 0

begin

	@number_of_executions += 1

	driver = Selenium::WebDriver.for :chrome
	driver.navigate.to "http://51.140.57.74:3000/growth-chart/id/1"

	sleep(4)

	patient_name = driver.find_element(:css, "span.patient-name.title")
	assert_equal patient_name.text, "Keira Jones", ERROR_MESSAGE % {:execution_number => @number_of_executions, :value_type => "Patient Name"}

	patient_gender = driver.find_element(:css, "span.patient-gender.title")
	assert_equal patient_gender.text, "female", ERROR_MESSAGE % {:execution_number => @number_of_executions, :value_type => "Patient Gender"}

	patient_dob = driver.find_element(:css, "span.patient-birth.title")
	assert_equal patient_dob.text, "23Mar2009", ERROR_MESSAGE % {:execution_number => @number_of_executions, :value_type => "Patient DOB"}


	driver.find_element(:xpath, '//span[@data-value="table"]').click

	length_heading = driver.find_element(:css, "tr.length.heading")
	check_heading_elements(length_heading, length_expected_list, "Patient Length")

	weight_heading = driver.find_element(:css, "tr.weight.heading")
	check_heading_elements(weight_heading, weight_expected_list, "Patient Weight")

	head_circ_heading = driver.find_element(:css, "tr.headc.heading")
	check_heading_elements(head_circ_heading, head_circ_expected_list, "Patient Head Circumference")

	bmi_heading = driver.find_element(:css, "tr.bmi.heading")
	check_heading_elements(bmi_heading, bmi_expected_list, "Patient BMI")

end until @number_of_executions.eql? EXPECTED_NUMBER_OF_EXECUTIONS

puts "Data retrieved successfuly by GCA from NHS platform #{@number_of_executions} times." 


