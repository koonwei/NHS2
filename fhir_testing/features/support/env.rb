#encoding: utf-8

require 'rest-client'

def server_up_and_running?
    begin
        response = RestClient.get 'http://localhost:4567/fhir/patient/1'
    rescue Exception => e
        $stdout.puts "Error #{e}"
        return false
    end
end

def start_server
    res = system 'sh start_server.sh'
    $stdout.puts 'wait 15 seconds fo server to start...'
    sleep(15)
    attempts_left = 30
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
    res = system 'sh stop_server.sh'
end

stop_server

#start_server

at_exit do 
    stop_server
end

Before do |scenario|
    # stop_server

    # start_server
end

After do |scenario|
    # stop_server
end
