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

When(/^I search a patient with family name "([^"]*)" and given name "([^"]*)"$/) do |family_name, given_name|
  @response = RestClient.get "http://localhost:4567/fhir/Patient?family=#{family_name}&given=#{given_name}", :content_type => :json, :accept => :json
end

When(/^I read a patient with the same id$/) do
  url = "http://localhost:4567/fhir/Patient/#{@id}"
  @response = RestClient.get url, :content_type => :json, :accept => :json
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

When(/^I delete a patient with id (\d+)$/) do |id|
  begin
    @response = RestClient.delete "http://localhost:4567/fhir/Patient/#{id}", :content_type => :json, :accept => :json
  rescue StandardError => e
    @response = e.response
  end
end

#
# Then
#

Then(/^The server response has status code (\d+)$/) do |code|
  expect(@response.code).to eq(code.to_i)
end

And(/^The server response has a body with the same id, family name "([^"]*)", and given name "([^"]*)"$/) do |family_name, given_name|
	json_response = JSON.parse(@response.body)
	expect(json_response).to have_key("id")
	expect(json_response["id"]).to eq(@id)
	expect(json_response).to have_key("name")
	expect(json_response["name"][0]).to have_key("family")
	expect(json_response["name"][0]).to have_key("given")
	puts json_response["name"][0]["family"]
	expect(json_response["name"][0]["family"]).to eq(family_name)
	expect(json_response["name"][0]["given"][0]).to eq(given_name)
end

And(/^The server response has header parameter with key "([^"]*)" and value "([^"]*)"$/) do |header_key, header_value|
	expect(@response.headers).to have_key(header_key)
	expect(@response.headers[header_key]).to eq(header_value)
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


