package fhirconverter.fhirservlet;

import ca.uhn.fhir.model.base.composite.BaseCodingDt;
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
import fhirconverter.converter.ConverterOpenempi;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.json.JSONObject;

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

    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return Observation.class;
    }

    @Search()
    public List<Observation> searchObservation(@OptionalParam(name = Observation.SP_CODE) TokenOrListParam observationCode,
                                       @OptionalParam(name = Observation.SP_PATIENT) ReferenceParam patient) {

       LOGGER.info("Observation Code: " + observationCode.getListAsCodings());
       List<BaseCodingDt> codingList = observationCode.getListAsCodings();
       for (BaseCodingDt coding: codingList)
       {
           LOGGER.info("Coding: " + coding.getCodeElement().getValue());

       }

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

//        List<Observation> observations = getDataFromServer(patientId);

        observations.add(dummyBilliRubinObservation(patientId, 7.0, "1957-01-01T02:20:00Z"));
        observations.add(dummyBilliRubinObservation(patientId, 9.0, "1957-01-02T14:00:00Z"));
        observations.add(dummyBilliRubinObservation(patientId, 10.0, "1957-01-02T18:00:00Z"));
        return observations;
    }

    private Observation dummyObservation(String patientId) {
        Observation observation = new Observation();
        observation.getSubject().setReference(patientId);
        observation.setId(String.valueOf(ThreadLocalRandom.current().nextInt()));
        return observation;
    }

    private Observation dummyBilliRubinObservation(String patientId, Double value, String date) {
        Observation observation = dummyObservation(patientId);
        CodingDt coding = new CodingDt("http://loinc.org", "58941-6");
        coding.setDisplay("Transcutaneous Bilirubin");
        observation.getCode().addCoding(coding);
        observation.getCode().setText("bilirubin");
        QuantityDt quantity = new QuantityDt();
        quantity.setValue(value);
        quantity.setCode("mg/dL");
        quantity.setUnit("mg/dL");
        quantity.setSystem("http://unitsofmeasure.org");
        observation.setValue(quantity);
        observation.setEffective(new DateTimeDt(date));
        return observation;
    }

    private Observation dummyWeightObservation(String patientId, Double value, String date) {
        Observation observation = dummyObservation(patientId);
        observation.getCode().addCoding(new CodingDt("http://loinc.org", "3141-9"));
        observation.getCode().setText("weight");
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
        observation.getCode().addCoding(new CodingDt("http://loinc.org", "39156-5"));
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

    private List<Observation> getDataFromServer(String patientId) {
        List<Observation> observations = new ArrayList<Observation>();
        try {

            Unirest.setAsyncHttpClient(createSSLClient());
            HttpResponse<String> response = Unirest.get("https://test.operon.systems/rest/v1/query?aql=select%20%20%20%20%20a_a%2Fdata%5Bat0002%5D%2Fevents%5Bat0003%5D%2Fdata%5Bat0001%5D%2Fitems%5Bat0004%5D%2Fvalue%2Fmagnitude%20as%20Weight_magnitude%2C%20%20%20%20%20a_b%2Fdata%5Bat0001%5D%2Fevents%5Bat0002%5D%2Fdata%5Bat0003%5D%2Fitems%5Bat0004%5D%2Fvalue%2Fmagnitude%20as%20Height_Length_magnitude%2C%20%20%20%20%20a_c%2Fdata%5Bat0001%5D%2Fevents%5Bat0010%5D%2Fdata%5Bat0003%5D%2Fitems%5Bat0004%5D%2Fvalue%2Fmagnitude%20as%20Head_circumference_magnitude%20from%20EHR%20e%5Behr_id%2Fvalue%3D'61272d11-b789-4cd6-b388-5a914b9c12b3'%5D%20contains%20COMPOSITION%20a%5BopenEHR-EHR-COMPOSITION.report.v1%5D%20contains%20(%20%20%20%20%20OBSERVATION%20a_a%5BopenEHR-EHR-OBSERVATION.body_weight.v1%5D%20or%20%20%20%20%20OBSERVATION%20a_b%5BopenEHR-EHR-OBSERVATION.height.v1%5D%20or%20%20%20%20%20OBSERVATION%20a_c%5BopenEHR-EHR-OBSERVATION.head_circumference.v0%5D)%20where%20a%2Fname%2Fvalue%3D'Smart%20Growth%20Report'%20offset%200%20limit%20100")
                    .header("authorization", "Basic b3Bybl90cmFpbmluZzpHaXlUQVphRTEyMQ==")
                    .header("ehr-session-disabled", "0ce5ec82-3954-4388-bfd7-e48f6db613e8")
                    .asString();

            JSONObject jsonResponse = new JSONObject(response.getBody());
            JSONObject resultSet = jsonResponse.getJSONArray("resultSet").getJSONObject(0);
            Double weight = resultSet.optDouble("Weight_magnitude");
//            Double weight = resultSet.optDouble("Head_circumference_magnitude");
            Double height = resultSet.optDouble("Height_Length_magnitude");

            LOGGER.info(resultSet);
            LOGGER.info(weight);

            observations.add(dummyWeightObservation(patientId, weight, "1957-02-24"));
            LOGGER.info(height);
            observations.add(createDummyHeightObservation(patientId, height, "1957-02-24"));
            return  observations;

        } catch (UnirestException e) {
            e.printStackTrace();
            return null;
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
