const express = require('express')
const app = express()

var server_address = 'http://51.140.57.74'
var growth_chart_port = 9000
var fhir_port = 8090

app.get('/growth-chart/id/:patientId', function (req, res) {
      var patient_id = req.params['patientId'];
      console.log(patient_id);
      var url = `${server_address}:${growth_chart_port}/launch.html?fhirServiceUrl=${server_address}:${fhir_port}/fhir&patientId=${patient_id}`
      console.log(url);
      res.redirect(url);
})

app.listen(3000)
