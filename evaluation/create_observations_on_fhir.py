import json
from pprint import pprint
import requests

server_base = 'http://51.140.111.127:9080'

def put_patient(server_base):
  url = '%s/Patient/2' % server_base
  patient = {"resourceType":"Patient", "id": "2", "identifier":[{"system":"uk.nhs.nhs_number","value":"TEST-123456789"}],"name":[{"use":"official","fa    mily":["Jones"],"given":["Keira"],"prefix":["Miss"]}],"gender":"female","birthDate":"2009-03-23","address":[{"text":"43 Islay Road, Garrowhill, West Yorkshire, LS23 4SP","line":["43 Islay Road    "],"city":"Garrowhill","postalCode":"LS23 4SP"}]}
  response = requests.put(url, data=json.dumps(patient), headers={'Content-Type': 'application/json'})
  print(response.status_code)

def post_observation(observation, server_base):
  url = '%s/Observation' % server_base
  response = requests.post(url, data=json.dumps(observation), headers={'Content-Type': 'application/json'})
  print(response.status_code)

def delete_observations(server_base):
  url = '%s/Observation?_format=json' % server_base
  response = requests.get(url)
  if 'entry' not in response.json():
    return
  for o in response.json()['entry']:
    o_id = o['resource']['id']
    response = requests.delete('%s/Observation/%s?_format=json' % (server_base, o_id))
    print(response.status_code)

delete_observations(server_base)
put_patient(server_base)

with open('Observations.json', 'r') as fp:
  observations = json.load(fp)
for obs in observations['entry']:
  new_obs = obs['resource']
  new_obs['subject'] = {'reference': 'Patient/2'}
  new_obs.pop('id', None)
  post_observation(new_obs, server_base)

