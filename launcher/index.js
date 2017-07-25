const express = require('express')
const app = express()

app.set('view engine', 'pug')

// var server_address = 'http://51.140.57.74'
var fhir_port = 8090

var server_address = '127.0.0.1'
var growth_chart_port = 9000

var rest = require('restler');

app.get('/growth-chart/id/:patientId', function (req, res) {
  var patient_id = req.params['patientId'];
  console.log(patient_id);
  var url = `http://${server_address}:${growth_chart_port}/launch.html?fhirServiceUrl=http://${server_address}:${fhir_port}/fhir&patientId=${patient_id}`
  console.log(url);
  res.redirect(url);
})

app.get('/patients', function (req, res) {
  var url = `http://${server_address}:${fhir_port}/fhir/Patient?_format=json`;
  console.log(url);
  rest.get(url).on('complete', function(result) {
    if (result instanceof Error) {
      console.log('Error:', result.message);
    } else {
      // console.log(JSON.parse(result));

      var patients = JSON.parse(result)['entry'].map(function(item, index) {
        var resource = item['resource'];
        return resource;
      });
      patients = patients.map(function(item, index) {
        var patient = new Object();
        patient.id = item.id
        if(item.birthDate == undefined) {
          patient.birthDate = '';
        }
        else {
          patient.birthDate = item.birthDate;
        }

        if(item.name == undefined) {
          patient.given = '';
          patient.family = '';
        }
        else {
          patient.given = item.name[0].given[0] 
          patient.family = item.name[0].family[0];
        }
        console.log(patient);
        return patient;
      });

      // res.send(patients)
      res.render('patients', { patients: patients})
    }
  });
})

app.listen(3000)
