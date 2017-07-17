#encoding: utf-8

require 'rest-client'
require 'json'
require 'crack/xml'
require "rspec"
include RSpec::Matchers

def new_patient(family_name, given_name)
  patient = { resourceType: "Patient", name: [ {family: family_name, given: [given_name]} ] }
end

def new_patch(family_name, given_name)
  patch= [ {op: "add", path: "/given", value: given_name}]
end

def CheckPatientParameters (patient, id, family_name, given_name)
  expect(patient).to have_key("id")
  expect(patient["id"]).to eq(id)
  expect(patient).to have_key("name")
  expect(patient["name"][0]).to have_key("family")
  expect(patient["name"][0]).to have_key("given")
  expect(patient["name"][0]["family"]).to eq(family_name)
  expect(patient["name"][0]["given"][0]).to eq(given_name)
end

#
# Given
#

Given(/^The server has a patient stored with family name "([^"]*)", and given name "([^"]*)"$/) do |family_name, given_name|
  payload = new_patient(family_name, given_name).to_json
  response = RestClient.post 'http://localhost:4567/fhir/Patient', payload, :content_type => 'application/json', :accept => :json
  location = response.headers[:location]
  @id = location.scan(/\d+$/)[0]

end

#
# When
#

When(/^I create a patient with family name "([^"]*)" and given name "([^"]*)"$/) do |family_name, given_name|
  payload = new_patient(family_name, given_name).to_json
  @response = RestClient.post 'http://localhost:4567/fhir/Patient', payload, :content_type => 'application/json', :accept => :json
end

When(/^I read a patient with the same id$/) do
  url = "http://localhost:4567/fhir/Patient/#{@id}"
  @response = RestClient.get url, :content_type => :json, :accept => :json
end

When(/^I delete a patient with the same id$/) do
  url = "http://localhost:4567/fhir/Patient/#{@id}"
  @response = RestClient.delete url, :content_type => :json, :accept => :json
end

When(/^I search a patient with family name "([^"]*)" and given name "([^"]*)"$/) do |family_name, given_name|
  @response = RestClient.get "http://localhost:4567/fhir/Patient?family=#{family_name}&given=#{given_name}", :content_type => :json, :accept => :json
end

When(/^I read a patient with id (\d+)(?: and format ([a-zA-Z\/\+]+))?$/) do |id, _format|
  if _format.nil?
    url = "http://localhost:4567/fhir/Patient/#{id}"
  else
    url = "http://localhost:4567/fhir/Patient/#{id}?_format=#{_format}"
  end
  @response = RestClient.get url, :content_type => :json, :accept => :json
end

When(/^I update a patient with id (\d+) and family name "([^"]*)", given name "([^"]*)"$/) do |id, family_name, given_name|
  payload = new_patient(family_name, given_name).to_json
  @response = RestClient.put "http://localhost:4567/fhir/Patient/#{id}", payload, :content_type => :json, :accept => :json
end

When(/^I patch a patient with id (\d+) and family name "([^"]*)", given name "([^"]*)"$/) do |id, family_name, given_name|
  payload = new_patch(family_name, given_name).to_json
  @response = RestClient.patch "http://localhost:4567/fhir/Patient/#{id}", payload, :content_type => :json, :accept => :json
end

#
# Then
#

Then(/^The server response has status code (\d+)$/) do |code|
  expect(@response.code).to eq(code.to_i)
end

And(/^The server response has a body with the same id, family name "([^"]*)", and given name "([^"]*)"$/) do |family_name, given_name|
  json_patient = JSON.parse(@response.body)	
  CheckPatientParameters(json_patient, @id, family_name, given_name)
end

And(/^The server response has the id in the location header$/) do 
  location = @response.headers[:location]
  @id = location.scan(/\d+$/)[0]
end

And(/^The server has a patient stored with this id, family name "([^"]*)", and given name "([^"]*)"$/) do |family_name, given_name|
  url = "http://localhost:4567/fhir/Patient/#{@id}"
  response = RestClient.get url, :content_type => :json, :accept => :json
  json_patient = JSON.parse(response.body)	
  CheckPatientParameters(json_patient, @id, family_name, given_name)
end

And(/^The server has no patient stored with this id$/) do 
  url = "http://localhost:4567/fhir/Patient/#{@id}"
  response = RestClient.get url, :content_type => :json, :accept => :json
  expect(response.code).to eq(204)
end

And(/^the server has response with key "([^"]*)" and content "([^"]*)"$/) do |key, content|
  json_response = JSON.parse(@response.body)
  expect(json_response).to have_key(key)
  expect(json_response[key]).to match(content) do |id|
    puts id
  end
end

And(/^the server response has json key "([^"]*)"$/) do |key|
  json_response = JSON.parse(@response.body)
  expect(json_response).to have_key(key)
end

And(/^the server response has XML tag "([^"]*)"$/) do |content|
  xml_json = Crack::XML.parse(@response.body)
  expect(xml_json).to have_key(content)
end

And(/^A patient is stored with family name "([^"]*)" and given name "([^"]*)"$/) do |family_name, given_name|
  payload = new_patient(family_name, given_name).to_json
  @response = RestClient.put "http://localhost:4567/fhir/patient/#{id}", payload, :content_type => :json, :accept => :json
end


