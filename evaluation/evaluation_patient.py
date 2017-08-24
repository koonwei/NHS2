import requests
import time

total = 1000
total_time = 0
delay = 0.1

# server_base = 'http://51.140.111.127:9080'
# paitent_ids = ['5995a4e860b26a5b8e62be6a']

server_base = 'http://51.140.111.127:8090/fhir'
paitent_ids = ['3']

for i in range(1, total+1):
  for p_id in paitent_ids:
    url = '%s/Patient/%s' % (server_base, p_id)
    response = requests.get(url)
    # print(response.text)
    request_time = response.elapsed.total_seconds() * 1000
    print('Iteration %d for patient %s: %f ms' % (i, p_id, request_time))
    total_time += request_time
    time.sleep(delay)


print('Average: %f ms' % (total_time/total/len(paitent_ids)))