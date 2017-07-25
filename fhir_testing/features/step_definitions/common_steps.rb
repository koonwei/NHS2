server_base = 'http://localhost:8090/fhir'

Given(/^fixture "([^"]*)" has loaded as "([^"]*)"$/) do |file_name, variable_name|
  json_file = File.read(file_name)
  json_hash = JSON.parse(json_file)

  instance_variable_set("@#{variable_name}", json_hash)
end

Given(/^a patient was created with "([^"]*)"$/) do |variable_name|
  patient = instance_variable_get("@#{variable_name}")
  payload = patient.to_json
  begin
    response = RestClient.post server_base + '/Patient', payload, :content_type => 'application/json', :accept => :json
    location = response.headers[:location]
    @patient_id = location.scan(/Patient\/(\d+)/).first.first
  rescue StandardError => e
    puts e.response
  end
end