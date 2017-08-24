import requests
import time

total = 10
total_time = 0
delay = 0.1

# server_base = 'http://51.140.111.127:9080'

server_base = 'http://51.140.111.127:8090/fhir'
patient_ids = [2]
codes = ['http://loinc.org|3141-9', 'http://loinc.org|8302-2', 'http://loinc.org|8287-5', 'http://loinc.org|58941-6']

print(','.join(codes))

for i in range(1, total+1):
  for p_id in patient_ids:
    url = '%s/Observation?code=%s&patient=%s&_format=json' % (server_base, ','.join(codes), p_id)
    print(url)
    response = requests.get(url)
    print(response.text)
    request_time = response.elapsed.total_seconds() * 1000
    print('Iteration %d for patient %s: %f ms' % (i, p_id, request_time))
    total_time += request_time
    time.sleep(delay)


print('Average: %f ms' % (total_time/total/len(patient_ids)))