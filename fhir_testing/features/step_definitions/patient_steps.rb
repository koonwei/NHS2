#encoding: utf-8

require 'rest-client'
require 'json'
require 'json-diff'
require 'crack/xml'
require "rspec"
include RSpec::Matchers

server_base = 'http://localhost:8090/fhir'

def new_patient(family_name, given_name)
  patient = @patient.clone

  patient['name'].first['family'] = []
  patient['name'].first['family'] << family_name
  patient['name'].first['given'] = []
  patient['name'].first['given'] << given_name
  patient
end

def compare_patients (expected_patient, obtained_patient)
  obtained_patient.delete("text")
  obtained_patient.delete("meta")
  return (JsonDiff.diff(obtained_patient,expected_patient))
end

def find_patient (bundle, patient_id)
  bundle['entry'].each do |entry|
    patient = entry['resource']
    if patient['id'] == patient_id
      return patient
    end
  end
end

#
# Given
#

#
# When
#

When(/^I create a patient using "([^"]*)" fixture, with family name "([^"]*)" and given name "([^"]*)"$/) do |variable_name, family_name, given_name|
  patient = new_patient(family_name, given_name)
  payload = patient.to_json
  url = server_base + '/Patient'
  @response = RestClient.post url, payload, :content_type => 'application/json', :accept => :json
  location = @response.headers[:location]
  patient['id'] = location.scan(/Patient\/(\d+)/).first.first
  @created_patients << patient
end

When(/^I read the first patient created$/) do
  url = server_base + "/Patient/#{@created_patients.first['id']}"
  @response = RestClient.get url, :content_type => :json, :accept => :json
end

When(/^I delete the first patient created$/) do
  url = server_base + "/Patient/#{@created_patients.first['id']}"
  @response = RestClient.delete url, :content_type => :json, :accept => :json
end

When(/^I search patients$/) do
  url = server_base + "/Patient"
  @response = RestClient.get url, :content_type => :json, :accept => :json
end

When(/^I search patients with ([^\s]*) "([^"]*)"$/) do |parameter, value|
  url = server_base + "/Patient?#{parameter}=#{value}"
  @response = RestClient.get url, :content_type => :json, :accept => :json
end


When(/^I search patients with family name "([^"]*)" and given name "([^"]*)"$/) do |family_name, given_name|
  url = server_base + "/Patient?family=#{family_name}&given=#{given_name}"
  @response = RestClient.get url, :content_type => :json, :accept => :json
end

When(/^I update the first patient using "([^"]*)" fixture, with family name "([^"]*)" and given name "([^"]*)"$/) do |variable_name, family_name, given_name|
  patient_id = @created_patients.first['id']
  patient = new_patient(family_name, given_name)
  patient['id'] = patient_id
  payload = patient.to_json
  url = server_base + "/Patient/" + patient_id
  @response = RestClient.put url, payload, :content_type => :json, :accept => :json
  @created_patients[0] = patient
end

When(/^I patch the first patient stored to delete marital status, replace given name for "([^"]*)" and add country "([^"]*)" to address$/) do |given_name, country|
  patient = @created_patients.first
  patient_id = patient['id']
  url = server_base + "/Patient/" + patient_id
  payload = [{"op": "remove", "path": "/maritalStatus"}, {"op": "replace", "path": "/name/0/given/0", "value": given_name}, {"op": "add", "path": "/address/0/country", "value": country}].to_json
  @response = RestClient.patch url, payload, :content_type => "application/json-patch+json"
  patient.delete('maritalStatus')
  patient['name'][0]['given'][0] = given_name
  address = patient['address'][0]
  address['country'] = country
  address['text'] = address['line'][0] + " " + address['city'] + " " + address['postalCode'] + " " + address['country']
end

#
# Then
#

Then(/^The server response has status code (\d+)$/) do |code|
  expect(@response.code).to eq(code.to_i)
end

And(/^The server response has the patient id in the location header$/) do 
  location = @response.headers[:location]
  @response_id = location.scan(/Patient\/(\d+)/).first.first
end

And(/^The server has a patient stored with this id, family name "([^"]*)", and given name "([^"]*)"$/) do |family_name, given_name|
  url = server_base + "/Patient/#{@response_id}"
  response = RestClient.get url, :content_type => :json, :accept => :json
  obtained_patient = JSON.parse(response.body)  
  expected_patient = new_patient(family_name, given_name)
  expected_patient['id'] = @response_id
  diff = compare_patients(expected_patient, obtained_patient)
  expect(diff).to eq([])
end

And(/^The server response has a body with the first patient created$/) do
  obtained_patient = JSON.parse(@response.body)	
  expected_patient = @created_patients.first
  diff = compare_patients(expected_patient, obtained_patient)
  expect(diff).to eq([])
end

And(/^the response is a bundle that contains the patients created$/) do
  bundle = JSON.parse(@response.body)	
  @created_patients.each do |expected_patient|
    obtained_patient = find_patient(bundle, expected_patient['id'])
    expect(obtained_patient).not_to be_nil 
    diff = compare_patients(expected_patient, obtained_patient)
    expect(diff).to eq([])
  end
end

And(/^the response is a bundle that contains the first patient created$/) do
  bundle = JSON.parse(@response.body)	
  expected_patient = @created_patients.first
  obtained_patient = find_patient(bundle, expected_patient['id'])
  expect(obtained_patient).not_to be_nil 
  diff = compare_patients(expected_patient, obtained_patient)
  expect(diff).to eq([])
end

And(/^the response is a bundle with patients that have ([^\s]*) "([^"]*)"$/) do |parameter, expected_value|
  bundle = JSON.parse(@response.body)	
  bundle['entry'].each do |entry|
    obtained_patient = entry['resource']
    obtained_value = obtained_patient[parameter]
    expect(obtained_value).to eq(expected_value)
  end
end

And(/^the response is a bundle with patients that have family name "([^"]*)" and give name "([^"]*)"$/) do |expected_family_name, expected_given_name|
  bundle = JSON.parse(@response.body)	
  bundle['entry'].each do |entry|
    obtained_patient = entry['resource']
    obtained_family_name = obtained_patient['name'].first['family'].first
    expect(obtained_family_name).to eq(expected_family_name)
    obtained_given_name = obtained_patient['name'].first['given'].first
    expect(obtained_given_name).to eq(expected_given_name)
  end
end


And(/^The first patient stored in the server has been modified$/) do
  expected_patient = @created_patients.first
  patient_id = expected_patient['id']
  url = server_base + "/Patient/#{patient_id}"
  response = RestClient.get url, :content_type => :json, :accept => :json
  obtained_patient = JSON.parse(response.body)  
  diff = compare_patients(expected_patient, obtained_patient)
  expect(diff).to eq([])
end

And(/^The server has not stored the first patient created$/) do 
  url = server_base + "/Patient/#{@created_patients.first["id"]}"
  begin
    RestClient.get url, :content_type => :json, :accept => :json
  rescue StandardError => e
    expect(e.response.code).to eq(404)
  end
end
