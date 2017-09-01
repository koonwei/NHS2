require 'rest-client'
require 'json'

def get_session(server_base, username, password)
    url = "#{server_base}/session?username=#{username}&password=#{password}"
    response = RestClient.post url, :content_type => :json, :accept => :json
    return JSON.parse(response.body)['sessionId']
end

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

def commit_composition(server_base, composition, ehr_id, session_id)
    url = "#{server_base}/composition?ehrId=#{ehr_id}&templateId=Smart Growth Chart Data.v0&format=FLAT"
    puts url
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


def create_ehr(server_base, subject_id, namespace, session_id)
    url = "#{server_base}/ehr?subjectId=#{subject_id}&subjectNamespace=#{namespace}"
    puts url
    begin
        response = RestClient.get url, :content_type => :json, :accept => :json, 'Ehr-Session' => session_id
        if response.code == 200
            ehrid = JSON.parse(response.body)['ehrId']
            return ehrid
        elsif response.code == 204
            response = RestClient.post url, nil, :content_type => :json, :accept => :json, 'Ehr-Session' => session_id
            ehrid = JSON.parse(response.body)['ehrId']
            return ehrid
        end
    rescue StandardError => e
        if e.response.code == 404
            response = RestClient.post url, nil, :content_type => :json, :accept => :json, 'Ehr-Session' => session_id
            ehrid = JSON.parse(response.body)['ehrId']
            return ehrid
        end
    end
end

def clear_compositions(ehr_server_base, ehr_id, session_id)
    aql = "select a/uid from EHR [ehr_id/value='#{ehr_id}'] contains COMPOSITION a"
    url = "#{ehr_server_base}/query?aql=#{aql}"
    response = RestClient.get url, :content_type => :json, :accept => :json, 'Ehr-Session' => session_id
    JSON.parse(response.body)['resultSet'].each do |c|
        id = c['#0']['value']
        puts id
        url = "#{ehr_server_base}/composition/#{id}"
        RestClient.delete url, :content_type => :json, :accept => :json, 'Ehr-Session' => session_id
    end
end

fhir_server_base = 'http://51.140.111.127:8090/fhir'

ehr_server_base = 'https://test.operon.systems/rest/v1'
ehr_username = 'oprn_jarrod'
ehr_password = 'ZayFYCiO644'

# ehr_server_base = 'http://51.140.111.127:8888/rest/v1'
# ehr_username = 'guest'
# ehr_password = 'guest'

session_id = get_session(ehr_server_base, ehr_username, ehr_password)

puts session_id
ehr_id = create_ehr(ehr_server_base, 'TEST-123456789', 'uk.nhs.nhs_number', session_id)
puts ehr_id

composition = new_composition('1957-02-01T02:20:00Z', '78.5', '9.8', '17.2', '50')
commit_composition(ehr_server_base, composition, ehr_id, session_id)

composition = new_composition('1957-03-02T02:20:00Z', '88.5', '12.8', '19.2', '55')
commit_composition(ehr_server_base, composition, ehr_id, session_id)

composition = new_composition('1957-04-03T02:20:00Z', '78.5', '26.8', '25.2', '56')
commit_composition(ehr_server_base, composition, ehr_id, session_id)

# clear_compositions(ehr_server_base, ehr_id, session_id)


