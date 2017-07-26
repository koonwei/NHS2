const express = require('express')
const app = express()

app.set('view engine', 'pug');
app.use('/bower_components',  express.static(__dirname + '/bower_components'));
app.use(express.static('css'))

// var server_address = 'http://51.140.57.74'

var rest = require('restler');

var config = require('./config.json');

var fhirPort = config.fhirPort;
var serverAddress = config.serverAddress;
var growthChartPort = config.growthChartPort;

app.get('/growth-chart/id/:patientId', function (req, res) {
  var patientId = req.params['patientId'];
  console.log(patientId);
  var url = `http://${serverAddress}:${growthChartPort}/launch.html?fhirServiceUrl=http://${serverAddress}:${fhirPort}/fhir&patientId=${patientId}`;
  console.log(url);
  res.redirect(url);
});

app.get('/patients', function (req, res) {
  var url = `http://${serverAddress}:${fhirPort}/fhir/Patient?_format=json`;
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
        if(item.birthDate === undefined) {
          patient.birthDate = '';
        }
        else {
          patient.birthDate = item.birthDate;
        }

        if(item.name === undefined) {
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
