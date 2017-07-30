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

def new_patch(family_name, given_name)
  patch= [ {op: "add", path: "/given", value: given_name}]
end

def check_patient_values (patient, id, family_name, given_name)
  unless id.nil?
    expect(patient).to have_key("id")
    expect(patient["id"]).to eq(id)
  end
  unless family_name.nil? and family_name.nil?
    expect(patient).to have_key("name")
  end
  unless family_name.nil?
    expect(patient["name"][0]).to have_key("family")
  expect(patient["name"][0]["family"][0]).to eq(family_name)
  end
  unless given_name.nil?
    expect(patient["name"][0]).to have_key("given")
    expect(patient["name"][0]["given"][0]).to eq(given_name)
  end
end

def compare_patients (expected_patient, obtained_patient)
  obtained_patient.delete("text")
  obtained_patient.delete("meta")
  return (JsonDiff.diff(obtained_patient,expected_patient))
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

When(/^I search patients with family name "([^"]*)" and given name "([^"]*)"$/) do |family_name, given_name|
  url = server_base + "/Patient?family=#{family_name}&given=#{given_name}"
  @response = RestClient.get url, :content_type => :json, :accept => :json
end

# When(/^I read a patient with id (\d+)(?: and format ([a-zA-Z\/\+]+))?$/) do |id, _format|
#   if _format.nil?
#     url = "http://localhost:8080/fhir/Patient/#{id}"
#   else
#     url = "http://localhost:8080/fhir/Patient/#{id}?_format=#{_format}"
#   end
#   @response = RestClient.get url, :content_type => :json, :accept => :json
# end

# When(/^I update a patient with id (\d+) and family name "([^"]*)", given name "([^"]*)"$/) do |id, family_name, given_name|
#   payload = new_patient(family_name, given_name).to_json
#   @response = RestClient.put "http://localhost:8080/fhir/Patient/#{id}", payload, :content_type => :json, :accept => :json
# end

# When(/^I patch a patient with id (\d+) and family name "([^"]*)", given name "([^"]*)"$/) do |id, family_name, given_name|
#   payload = new_patch(family_name, given_name).to_json
#   @response = RestClient.patch "http://localhost:8080/fhir/Patient/#{id}", payload, :content_type => :json, :accept => :json
# end

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
     expected_patient_found = FALSE
     bundle['entry'].each do |entry|
       obtained_patient = entry['resource']
       if obtained_patient['id'] == expected_patient['id']
         diff = compare_patients(expected_patient, obtained_patient)
         expect(diff).to eq([])
         expected_patient_found = TRUE
	 break
       end
     end
  end
 expect(expected_patient_found.to eq(true))
end

And(/^The server has not stored the first patient created$/) do 
  url = server_base + "/Patient/#{@created_patients.first["id"]}"
  begin
    RestClient.get url, :content_type => :json, :accept => :json
  rescue StandardError => e
    expect(e.response.code).to eq(404)
  end
end

And(/^the response has json key "([^"]*)"$/) do |key|
  json_response = JSON.parse(@response.body)
  expect(json_response).to have_key(key)
end
