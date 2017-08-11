Given(/^fixture "([^"]*)" has loaded as "([^"]*)"$/) do |file_name, variable_name|
  json_file = File.read(File.join('fixtures', file_name))
  json_hash = JSON.parse(json_file)

  instance_variable_set("@#{variable_name}", json_hash)
end

Given(/^a patient was created using "([^"]*)" fixture, with family name "([^"]*)", given name "([^"]*)", gender "([^"]*)", birthDate "([^"]*)", and NHS identifier "([^"]*)"$/) do |variable_name, family_name, given_name, gender, birthdate, nhs_id|
  #clone method doesn't deep copy json arrays (like name)
  patient = JSON.parse(instance_variable_get("@#{variable_name}").to_json)

  patient['name'].first['family'] = [family_name]
  patient['name'].first['given'] = [given_name]
  patient['gender'] = gender
  patient['birthDate'] = birthdate
  patient['identifier'][0]['value'] = nhs_id

  begin
    response = RestClient.post  "#{$fhir_server_base}/Patient", patient.to_json, :content_type => 'application/json', :accept => :json
    location = response.headers[:location]
    patient['id'] = location.scan(/Patient\/(\d+)/).first.first
    if @created_patients.nil? then
      @created_patients = Array.new
    end
    @created_patients << patient
  rescue StandardError => e
    puts e.response
  end
end

Then(/^The server response has status code (\d+)$/) do |code|
  expect(@response.code).to eq(code.to_i)
end