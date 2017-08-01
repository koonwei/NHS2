#!/usr/bin/env python
# -*- coding: utf-8 -*-

import json
import requests
from pprint import pprint

date_format = 'data[%s]/origin/value'
magnitude_format = 'data[%s]/events[%s]/data[%s]/items[%s]/value/magnitude'
units_format = 'data[%s]/events[%s]/data[%s]/items[%s]/value/units'

def load_json(filename):
  with open(filename, 'r') as fp:
    content = json.load(fp)
  return content

def save_json(filename, json_data):
  with open(filename, 'w') as fp:
    json.dump(json_data, fp, indent=2, sort_keys=True)

def construct_aql(archetype):
  aql = "select o from OBSERVATION o [%s];" % archetype
  return aql

def query_observation(db, auth_token, aql):
  url = '%s/rest/v1/query' % db
  payload = {'aql' : aql}
  headers = {'Authorization' : auth_token, 'Content-Type' : 'application/json'}
  response = requests.post(url, json=payload, headers=headers)
  return response.json()

def get_path(json_data):
  observation = json_data['resultSet'][0]['#0']
  data_node = observation['data']
  events_node = data_node['events'][0]
  date_node_2 = events_node['data']
  items_node = date_node_2['items'][0]

  date_path = date_format % data_node['archetype_node_id']
  magnitude_path = magnitude_format % (data_node['archetype_node_id'], events_node['archetype_node_id'], date_node_2['archetype_node_id'], items_node['archetype_node_id'])
  units_path = units_format % (data_node['archetype_node_id'], events_node['archetype_node_id'], date_node_2['archetype_node_id'], items_node['archetype_node_id'])

  return {'date': date_path, 'magnitude': magnitude_path, 'units': units_path}


def main():

  mapping = load_json('mapping.json')
  path_json = dict()

  for code in mapping['mapping']:
    json_content = dict()
    json_content['archetype'] = code['archetype']
    json_content['text'] = code['text']

    aql = construct_aql(code['archetype'])
    response = query_observation('https://test.operon.systems', 'Basic b3Bybl9qYXJyb2Q6WmF5RllDaU82NDQ=', aql)
    paths = get_path(response)
    json_content['path'] = paths
    path_json[code['code']] = json_content

  save_json('aql_path.json', path_json)



if __name__ == '__main__':
  main()