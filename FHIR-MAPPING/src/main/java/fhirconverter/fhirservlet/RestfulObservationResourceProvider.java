package fhirconverter.fhirservlet;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.base.composite.BaseCodingDt;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.TokenOrListParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import fhirconverter.ConverterOpenempi;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hl7.fhir.instance.model.api.IBaseResource;

import java.util.ArrayList;
import java.util.List;

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
        List<BaseCodingDt> codingList = observationCode.getListAsCodings();
        for (BaseCodingDt coding: codingList)
        {
            LOGGER.info("Coding: " + coding.getCodeElement());

        }
        LOGGER.info("Patient ID: " + patient.getIdPart());


        List<Observation> observations = new ArrayList<Observation>();
        FhirContext ctx = FhirContext.forDstu2();
        IParser parser = ctx.newJsonParser();
        Observation observation = parser.parseResource(Observation.class, DUMMY_HEIGHT);
        observations.add(observation);
        observation = parser.parseResource(Observation.class, DUMMY_WEIGHT);
        observations.add(observation);
        observation = parser.parseResource(Observation.class, DUMMY_BMI);
        observations.add(observation);
        return observations;
    }

    private Observation createDummyObservation(String patientId)
    {
        Observation observation = new Observation();
        observation.getSubject().setReference(patientId);

//        observation.getCode().addCoding()

        return observation;
    }
}
