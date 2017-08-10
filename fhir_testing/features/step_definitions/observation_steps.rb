require 'rest-client'
require 'json'
require 'json-diff'
require 'crack/xml'
require "rspec"
include RSpec::Matchers

server_base = 'http://localhost:8090/fhir/'
test_operonBase = 'https://test.operon.systems/rest/v1/ehr/'
test_operonCompositionBase = "https://test.operon.systems/rest/v1/composition?"
{{openEhrApi}}/rest/v1/composition?ehrId={{ehrId}}&templateId={{templateId}}&committerName={{committerName}}&format=FLAT

def compare_observations (expected_observation, obtained_observation)
    [expected_observation, obtained_observation].each do |o|
        o.delete("meta")
        o.delete("id")
        o['entry'].each do |e|
            e.delete('fullUrl')
            e['resource'].delete('id')
        end
    end

    return (JsonDiff.diff(obtained_observation,expected_observation))
end

def create_weight_payload (value, date)
    payload = '{
    "ctx/language": "en",
    "ctx/territory": "GB",
    "ctx/composer_name": "Nurse Crachett",
    "ctx/time": "#{date}",
    "ctx/id_namespace": "Hospital",
    "ctx/id_scheme": "HOSPITAL-NS",
    "ctx/health_care_facility|name": "Marandia DGH",
    "ctx/health_care_facility|id": "9095",
    "smart_growth_report/body_weight/weight|magnitude": #{value},
    "smart_growth_report/body_weight/weight|unit": "kg"}'
    return (payload)
end    

def create_height_payload (value, date)
    payload = '{
    "ctx/language": "en",
    "ctx/territory": "GB",
    "ctx/composer_name": "Nurse Crachett",
    "ctx/time": "#{date}",
    "ctx/id_namespace": "Hospital",
    "ctx/id_scheme": "HOSPITAL-NS",
    "ctx/health_care_facility|name": "Marandia DGH",
    "ctx/health_care_facility|id": "9095",
    "smart_growth_report/height_length/any_event:0/height_length|magnitude": #{value},
    "smart_growth_report/height_length/any_event:0/height_length|unit": "cm"}'
    return (payload)
end    

def create_bmi_payload (value, date)
    payload = '{
    "ctx/language": "en",
    "ctx/territory": "GB",
    "ctx/composer_name": "Nurse Crachett",
    "ctx/time": "#{date}",
    "ctx/id_namespace": "Hospital",
    "ctx/id_scheme": "HOSPITAL-NS",
    "ctx/health_care_facility|name": "Marandia DGH",
    "ctx/health_care_facility|id": "9095",
        "smart_growth_report/body_mass_index/any_event:0/body_mass_index|magnitude": #{value},
    "smart_growth_report/body_mass_index/any_event:0/body_mass_index|unit": "kg/m2"
    }'

end  

Given(/^an EHRid exists for the subjec id "([^"]*)"$/) do |subject_id|
    url = test_operonBase + "?subjectId=#{subject_id}&subjectNamespace=uk.nhs.nhs_number"
    @response = RestClient.get url, :content_type => :json, :accept => :json
    if @response.code == 200
      replyJSON = JSON.parse(@response.body)
      @ehrid = replyJSON['"ehrId"']
    end
    if @response.code == 204
      replyJSON = JSON.parse(@response.body)
      @response = RestClient.post url, :content_type => :json, :accept => :json
      replyJSON = JSON.parse(@response.body)
      @ehrid = replyJSON['"ehrId"']
    end
end

Given(/^Create composition for "([^"]*)" observation with value "([^"]*)" and date "([^"]*)"$/) do |observation, value, date|
    url = test_operonCompositionBase + "ehrId=#{ehrid}&templateId=Smart%20Growth%20Chart%20Data.v0&format=FLAT"
    
    
end


When(/^I search Height, Weight, BMI, Head Circumference observation with Subject Id "([^"]*)"$/) do |subject_id|
    url = server_base + "Observation?code=3141-9,8302-2,8287-5,58941-6&patient=#{subject_id}&_format=json"
    @response = RestClient.get url, :content_type => :json, :accept => :json
end

And(/^the response is a bundle that contains the Observations created$/) do
    bundle = JSON.parse(@response.body)   
    expected_observations = @observations
    expect(bundle).not_to be_nil 
    diff = compare_observations(expected_observations, bundle)
    expect(diff).to eq([])
end