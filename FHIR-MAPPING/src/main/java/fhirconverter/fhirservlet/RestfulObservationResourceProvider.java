package fhirconverter.fhirservlet;

import ca.uhn.fhir.model.dstu2.composite.CodingDt;
import ca.uhn.fhir.model.dstu2.composite.QuantityDt;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.TokenOrListParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import fhirconverter.ConverterOpenempi;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hl7.fhir.instance.model.api.IBaseResource;

import javax.net.ssl.SSLContext;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RestfulObservationResourceProvider implements IResourceProvider {
    private static Logger LOGGER = LogManager.getLogger(RestfulObservationResourceProvider.class);

    private static FHIRParser<Observation> parser = new FHIRParser<>(Observation.class);

    private ConverterOpenempi converterOpenempi = new ConverterOpenempi();

    private final String DUMMY_HEIGHT = "{\n" +
            "            \"resourceType\" : \"Observation\",\n" +
            "            \"id\" : \"1482713\",\n" +
            "            \"effectiveDateTime\" : \"2003-11-28\",\n" +
            "            \"text\" : {\n" +
            "               \"status\" : \"generated\",\n" +
            "               \"div\" : \"<div>2003-11-28: height = 115.316 cm</div>\"\n" +
            "            },\n" +
            "            \"meta\" : {\n" +
            "               \"versionId\" : \"19628\",\n" +
            "               \"lastUpdated\" : \"2015-09-30T14:31:29.576+00:00\"\n" +
            "            },\n" +
            "            \"code\" : {\n" +
            "               \"text\" : \"height\",\n" +
            "               \"coding\" : [\n" +
            "                  {\n" +
            "                     \"system\" : \"http://loinc.org\",\n" +
            "                     \"code\" : \"8302-2\",\n" +
            "                     \"display\" : \"height\"\n" +
            "                  }\n" +
            "               ]\n" +
            "            },\n" +
            "            \"subject\" : {\n" +
            "               \"reference\" : \"Patient/40058\"\n" +
            "            },\n" +
            "            \"status\" : \"final\",\n" +
            "            \"valueQuantity\" : {\n" +
            "               \"unit\" : \"cm\",\n" +
            "               \"system\" : \"http://unitsofmeasure.org\",\n" +
            "               \"value\" : 115.316,\n" +
            "               \"code\" : \"cm\"\n" +
            "            }\n" +
            "         }";

    private final String DUMMY_WEIGHT = "{\n" +
            "            \"code\" : {\n" +
            "               \"text\" : \"weight\",\n" +
            "               \"coding\" : [\n" +
            "                  {\n" +
            "                     \"code\" : \"3141-9\",\n" +
            "                     \"system\" : \"http://loinc.org\",\n" +
            "                     \"display\" : \"weight\"\n" +
            "                  }\n" +
            "               ]\n" +
            "            },\n" +
            "            \"meta\" : {\n" +
            "               \"lastUpdated\" : \"2015-09-30T14:31:29.645+00:00\",\n" +
            "               \"versionId\" : \"19676\"\n" +
            "            },\n" +
            "            \"valueQuantity\" : {\n" +
            "               \"system\" : \"http://unitsofmeasure.org\",\n" +
            "               \"unit\" : \"kg\",\n" +
            "               \"value\" : 18.55193,\n" +
            "               \"code\" : \"kg\"\n" +
            "            },\n" +
            "            \"status\" : \"final\",\n" +
            "            \"subject\" : {\n" +
            "               \"reference\" : \"Patient/40058\"\n" +
            "            },\n" +
            "            \"effectiveDateTime\" : \"2003-11-28\",\n" +
            "            \"resourceType\" : \"Observation\",\n" +
            "            \"id\" : \"1482714\",\n" +
            "            \"text\" : {\n" +
            "               \"status\" : \"generated\",\n" +
            "               \"div\" : \"<div>2003-11-28: weight = 18.55193 kg</div>\"\n" +
            "            }\n" +
            "         }";

    private final String DUMMY_BMI = "{\n" +
            "            \"subject\" : {\n" +
            "               \"reference\" : \"Patient/40058\"\n" +
            "            },\n" +
            "            \"status\" : \"final\",\n" +
            "            \"valueQuantity\" : {\n" +
            "               \"value\" : 13.9,\n" +
            "               \"code\" : \"kg/m2\",\n" +
            "               \"unit\" : \"kg/m2\",\n" +
            "               \"system\" : \"http://unitsofmeasure.org\"\n" +
            "            },\n" +
            "            \"meta\" : {\n" +
            "               \"lastUpdated\" : \"2015-09-30T14:31:29.663+00:00\",\n" +
            "               \"versionId\" : \"19688\"\n" +
            "            },\n" +
            "            \"code\" : {\n" +
            "               \"text\" : \"bmi\",\n" +
            "               \"coding\" : [\n" +
            "                  {\n" +
            "                     \"code\" : \"39156-5\",\n" +
            "                     \"system\" : \"http://loinc.org\",\n" +
            "                     \"display\" : \"bmi\"\n" +
            "                  }\n" +
            "               ]\n" +
            "            },\n" +
            "            \"text\" : {\n" +
            "               \"div\" : \"<div>2003-11-28: bmi = 13.9 kg/m2</div>\",\n" +
            "               \"status\" : \"generated\"\n" +
            "            },\n" +
            "            \"effectiveDateTime\" : \"2003-11-28\",\n" +
            "            \"id\" : \"1482715\",\n" +
            "            \"resourceType\" : \"Observation\"\n" +
            "         }";

    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return Observation.class;
    }

    @Search()
    public List<Observation> searchObservation(@OptionalParam(name = Observation.SP_CODE) TokenOrListParam observationCode,
                                       @OptionalParam(name = Observation.SP_PATIENT) ReferenceParam patient) {

//        LOGGER.info("Observation Code: " + observationCode.getListAsCodings());
//        List<BaseCodingDt> codingList = observationCode.getListAsCodings();
//        for (BaseCodingDt coding: codingList)
//        {
//            LOGGER.info("Coding: " + coding.getCodeElement().getValue());
//
//        }

        String patientId = patient.getValue();

        LOGGER.info("Patient ID: " + patientId);


        List<Observation> observations = new ArrayList<Observation>();
//        FhirContext ctx = FhirContext.forDstu2();
//        IParser parser = ctx.newJsonParser();
//        Observation observation = parser.parseResource(Observation.class, DUMMY_HEIGHT);
//        observations.add(observation);
//        observation = parser.parseResource(Observation.class, DUMMY_WEIGHT);
//        observations.add(observation);
//        observation = parser.parseResource(Observation.class, DUMMY_BMI);
//        observations.add(observation);

        getDataFromServer();

        observations.add(dummyWeightObservation(patientId, 30.0, "2003-01-11"));
        return observations;
    }

    private Observation dummyObservation(String patientId) {
        Observation observation = new Observation();
        observation.getSubject().setReference(patientId);
        observation.setId(String.valueOf(ThreadLocalRandom.current().nextInt()));
        return observation;
    }

    private Observation dummyWeightObservation(String patientId, Double value, String date) {
        Observation observation = dummyObservation(patientId);
        observation.getCode().addCoding(new CodingDt("http://loinc.org", "39156-5"));
        observation.getCode().setText("bmi");
        QuantityDt quantity = new QuantityDt();
        quantity.setValue(value);
        quantity.setCode("kg");
        quantity.setUnit("kg");
        quantity.setSystem("http://unitsofmeasure.org");
        observation.setValue(quantity);
        observation.setEffective(new DateTimeDt(date));
        return observation;
    }

    private Observation dummyBMIObservation(String patientId, Double value, String date) {
        Observation observation = dummyObservation(patientId);
        observation.getCode().addCoding(new CodingDt("http://loinc.org", "3141-9"));
        observation.getCode().setText("bmi");
        QuantityDt quantity = new QuantityDt();
        quantity.setValue(value);
        quantity.setCode("kg/m2");
        quantity.setUnit("kg/m2");
        quantity.setSystem("http://unitsofmeasure.org");
        observation.setValue(quantity);
        observation.setEffective(new DateTimeDt(date));

        return observation;

    }

    private Observation createDummyHeightObservation(String patientId, Double value, String date) {
        Observation observation = dummyObservation(patientId);
        observation.getCode().addCoding(new CodingDt("http://loinc.org", "8302-2"));
        observation.getCode().setText("height");
        QuantityDt quantity = new QuantityDt();
        quantity.setValue(value);
        quantity.setCode("cm");
        quantity.setUnit("cm");
        quantity.setSystem("http://unitsofmeasure.org");
        observation.setValue(quantity);
        observation.setEffective(new DateTimeDt(date));

        return observation;
    }

    private void getDataFromServer() {
        try {
            Unirest.setAsyncHttpClient(createSSLClient());
            HttpResponse<String> response = Unirest.get("https://test.operon.systems/rest/v1/query?aql=select%20%20%20%20%20a_a%2Fdata%5Bat0002%5D%2Fevents%5Bat0003%5D%2Fdata%5Bat0001%5D%2Fitems%5Bat0004%5D%2Fvalue%2Fmagnitude%20as%20Weight_magnitude%2C%20%20%20%20%20a_b%2Fdata%5Bat0001%5D%2Fevents%5Bat0002%5D%2Fdata%5Bat0003%5D%2Fitems%5Bat0004%5D%2Fvalue%2Fmagnitude%20as%20Height_Length_magnitude%2C%20%20%20%20%20a_c%2Fdata%5Bat0001%5D%2Fevents%5Bat0010%5D%2Fdata%5Bat0003%5D%2Fitems%5Bat0004%5D%2Fvalue%2Fmagnitude%20as%20Head_circumference_magnitude%20from%20EHR%20e%5Behr_id%2Fvalue%3D'61272d11-b789-4cd6-b388-5a914b9c12b3'%5D%20contains%20COMPOSITION%20a%5BopenEHR-EHR-COMPOSITION.report.v1%5D%20contains%20(%20%20%20%20%20OBSERVATION%20a_a%5BopenEHR-EHR-OBSERVATION.body_weight.v1%5D%20or%20%20%20%20%20OBSERVATION%20a_b%5BopenEHR-EHR-OBSERVATION.height.v1%5D%20or%20%20%20%20%20OBSERVATION%20a_c%5BopenEHR-EHR-OBSERVATION.head_circumference.v0%5D)%20where%20a%2Fname%2Fvalue%3D'Smart%20Growth%20Report'%20offset%200%20limit%20100")
                    .header("authorization", "Basic b3Bybl90cmFpbmluZzpHaXlUQVphRTEyMQ==")
                    .header("ehr-session-disabled", "0ce5ec82-3954-4388-bfd7-e48f6db613e8")
                    .asString();
            LOGGER.info(response.getBody());
        } catch (UnirestException e) {
            e.printStackTrace();
        }

    }


    private CloseableHttpAsyncClient createSSLClient() {
        TrustStrategy acceptingTrustStrategy = new TrustStrategy() {

            @Override
            public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                return true;
            }
        };

        SSLContext sslContext = null;
        try {
            sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
        } catch (Exception e) {
            LOGGER.error("Could not create SSLContext");
        }

        return HttpAsyncClients.custom()
                .setHostnameVerifier(SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER)
                .setSSLContext(sslContext).build();
    }
}
