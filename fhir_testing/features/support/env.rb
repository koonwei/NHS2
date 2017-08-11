#encoding: utf-8

require 'rest-client'

$fhir_server_base = 'http://localhost:8090/fhir'
$ehr_server_base = 'https://test.operon.systems/rest/v1'

if not ENV['CUCUMBER_ENV'].nil?
    env = ENV['CUCUMBER_ENV']
else
    env = 'development'
end

def server_up_and_running?
    begin
        RestClient.get "#{$fhir_server_base}/metadata"
    rescue StandardError => e
        $stdout.puts "Error #{e}"
        return false
    end
end

def start_server
    system 'sh start_server.sh'
    $stdout.puts 'wait 15 seconds fo server to start...'
    sleep(15)
    attempts_left = 60
    while attempts_left > 0
        up_and_running = server_up_and_running?
        return if up_and_running
        $stdout.puts "Waiting for the application... (attemps left #{attempts_left})"
        sleep(2)
        attempts_left = attempts_left - 1
    end
    stop_server
    raise Exception.new('The application does not respond, giving up')
end

def stop_server
    system 'sh stop_server.sh'
end

if env == 'development'
    stop_server
    start_server
end

at_exit do 
    if env == 'development'
        stop_server
    end
end

After do |scenario|
    @created_patients.each do |patient|
        url = "#{$fhir_server_base}/Patient/#{patient['id']}"
        begin
            @response = RestClient.delete url, :content_type => :json, :accept => :json
        rescue StandardError => e 
        end
    end
end
