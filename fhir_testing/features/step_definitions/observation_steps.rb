require 'rest-client'
require 'json'
require 'json-diff'
require "rspec"
include RSpec::Matchers

@ehr_username = 'oprn_jarrod'
@ehr_password = 'ZayFYCiO644'

def get_session(username, password)
    url = "#{$ehr_server_base}/session?username=#{username}&password=#{password}"
    response = RestClient.post url, :content_type => :json, :accept => :json
    return JSON.parse(response.body)['sessionId']
end

$session_id = get_session(@ehr_username, @ehr_password)

def add_quantity_date(value, unit, prefix)
    return { "#{prefix}|magnitude" => value, "#{prefix}|unit" => unit }
end

def new_composition(date, weight, height, bmi, head_circumference)
    record = { "ctx/language" => "en",
      "ctx/territory" => "GB",
     "ctx/composer_name" => "Dummy",
     "ctx/time" => "#{date}",
     "ctx/id_namespace" => "Hospital",
     "ctx/id_scheme" => "HOSPITAL-NS"}

    if(weight != 'nil') 
        record = record.merge(add_quantity_date(weight, 'kg', 'smart_growth_report/body_weight/weight')) 
    end

    if(height != 'nil') 
        record = record.merge(add_quantity_date(height, 'cm', 'smart_growth_report/height_length/any_event:0/height_length'))
    end

    if(bmi != 'nil') 
        record = record.merge(add_quantity_date(bmi, 'kg/m2', 'smart_growth_report/body_mass_index/any_event:0/body_mass_index'))
    end

    if(head_circumference != 'nil') 
        record= record.merge(add_quantity_date(head_circumference, 'cm', 'smart_growth_report/head_circumference/any_event:0/head_circumference'))
    end

    return record
end

def commit_composition(composition, ehr_id, session_id)
    url = "#{$ehr_server_base}/composition?ehrId=#{ehr_id}&templateId=Smart Growth Chart Data.v0&format=FLAT"
    begin 
        response = RestClient.post url, composition.to_json, :content_type => :json, 'Ehr-Session' => session_id
        composition_id = JSON.parse(response.body)['compositionUid']
        if $created_compositions.nil?
            $created_compositions = Array.new
        end
        $created_compositions << composition_id
    rescue StandardError => e
        puts e.response.body
    end
end

def compare_observations (expected_observation, obtained_observation)
    [expected_observation, obtained_observation].each do |o|
        o.delete("meta")
        o.delete("id")
        o.delete("link")
        o['entry'].each do |e|
            e.delete('fullUrl')
            e['resource'].delete('subject')
            e['resource'].delete('id')
            e['resource']['valueQuantity'].delete('system')
        end
    end

    return (JsonDiff.diff(obtained_observation,expected_observation))
end

Given(/^an EHRid exists for the subjec id "([^"]*)"$/) do |subject_id|
    # url = "#{$ehr_server_base}/ehr?subjectId=#{subject_id}&subjectNamespace=https://fhir.nhs.uk/Id/nhs-number"
    url = "#{$ehr_server_base}/ehr?subjectId=#{subject_id}&subjectNamespace=uk.nhs.nhs_number"
    response = RestClient.get url, :content_type => :json, :accept => :json, 'Ehr-Session' => $session_id
    if response.code == 200
        @ehrid = JSON.parse(response.body)['ehrId']
        # puts @ehrid
    elsif response.code == 204
        response = RestClient.post url, nil, :content_type => :json, :accept => :json, 'Ehr-Session' => $session_id
        @ehrid = JSON.parse(response.body)['ehrId']
        # puts @ehrid
    end
end

And(/^the following compositions are created with the same ehrid:$/) do |table|
    table.hashes.each do |record|
        composition = new_composition(record[:date], record[:weight], record[:height], record[:bmi], record[:head_circumference])
        commit_composition(composition, @ehrid, $session_id)
    end
end

When(/^I search Height, Weight, BMI, Head Circumference observation with the created patient id$/) do
    patient_id = @created_patients.first['id']
    url = "#{$fhir_server_base}/Observation?code=http://loinc.org|3141-9,http://loinc.org|8302-2,http://loinc.org|8287-5,http://loinc.org|58941-6&patient=#{patient_id}&_format=json"
    @response = RestClient.get url, :content_type => :json, :accept => :json
end

And(/^the response is a bundle that contains the Observations created$/) do
    bundle = JSON.parse(@response.body)
    expected_observations = @observations
    expect(bundle).not_to be_nil 
    diff = compare_observations(expected_observations, bundle)
    expect(diff).to eq([])
end

Then(/^all compositions created are deleted$/) do
    aql = "select a/uid from EHR [ehr_id/value='#{@ehrid}'] contains COMPOSITION a"
    url = "#{$ehr_server_base}/query?aql=#{aql}"
    response = RestClient.get url, :content_type => :json, :accept => :json, 'Ehr-Session' => $session_id
    JSON.parse(response.body)['resultSet'].each do |c|
        id = c['#0']['value']
        url = "#{$ehr_server_base}/composition/#{id}"
        RestClient.delete url, :content_type => :json, :accept => :json, 'Ehr-Session' => $session_id
    end
end