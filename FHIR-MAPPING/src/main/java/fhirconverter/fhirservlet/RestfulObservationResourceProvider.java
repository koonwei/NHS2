package fhirconverter.fhirservlet;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.dstu2.resource.OperationOutcome;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.model.primitive.StringDt;
import ca.uhn.fhir.parser.DataFormatException;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.api.PatchTypeEnum;
import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.TokenOrListParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.InternalErrorException;
import ca.uhn.fhir.rest.server.exceptions.InvalidRequestException;
import ca.uhn.fhir.rest.server.exceptions.MethodNotAllowedException;
import com.github.fge.jsonpatch.JsonPatch;
import fhirconverter.ConverterOpenempi;
import fhirconverter.exceptions.ResourceNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

        LOGGER.info("Observation Code: " + observationCode);
        LOGGER.info("Patient ID: " + patient);

        List<Observation> observations = new ArrayList<Observation>();
        return observations;
    }
}
