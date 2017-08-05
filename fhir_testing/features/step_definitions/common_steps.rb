server_base = 'http://localhost:8090/fhir'

Given(/^fixture "([^"]*)" has loaded as "([^"]*)"$/) do |file_name, variable_name|
  json_file = File.read(file_name)
  json_hash = JSON.parse(json_file)

  instance_variable_set("@#{variable_name}", json_hash)
  @created_patients = Array.new(0)
end

Given(/^a patient was created using "([^"]*)" fixture, with family name "([^"]*)", given name "([^"]*)", gender "([^"]*)", birthDate "([^"]*)", and NHS identifier "([^"]*)"$/) do |variable_name, family_name, given_name, gender, birthdate, nhs_id|
  #clone method doesn't deep copy json arrays (like name)
  patient = JSON.parse(instance_variable_get("@#{variable_name}").to_json)
  patient['name'].first['family'] = []
  patient['name'].first['family'] << family_name
  patient['name'].first['given'] = []
  patient['name'].first['given'] << given_name
  patient['gender'] = gender
  patient['birthDate'] = birthdate
  patient['identifier'][0]['value'] = nhs_id

  payload = patient.to_json
  begin
    response = RestClient.post server_base + '/Patient', payload, :content_type => 'application/json', :accept => :json
    location = response.headers[:location]
    patient['id'] = location.scan(/Patient\/(\d+)/).first.first
    @created_patients << patient.clone
  rescue StandardError => e
    puts e.response
  end
end
