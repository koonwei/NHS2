#encoding: utf-8

require 'rest-client'

server_base = 'http://localhost:8090/fhir'

def server_up_and_running?
    begin
        RestClient.get 'http://localhost:8090/fhir/metadata'
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

stop_server

start_server

at_exit do 
    stop_server
end

Before do |scenario|
    # stop_server

    # start_server
end

After do |scenario|
  if scenario.name != "Delete a patient"
    @created_patients.each{
      |patient|
      url = server_base + "/Patient/#{patient['id']}"
      @response = RestClient.delete url, :content_type => :json, :accept => :json
    }
  end
  # stop_server
end
