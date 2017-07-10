package fhirconverter.spark;

import fhirconverter.ConverterOpenempi;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hl7.fhir.dstu3.model.Patient;
import org.json.JSONObject;
import spark.*;

import java.util.HashMap;

public class PatientController {
    private static Logger LOGGER = LogManager.getLogger(Patient.class);

    private static ParamValidater validater;

    public static ParamValidater getValidater()
    {
        if(validater == null)
            validater = new ParamValidater(Patient.class);
        return validater;
    }

    public static Route createPatient = (request, response) -> {
        HashMap data = new HashMap();
        boolean isValid = getValidater().isValid(data);
        Representation format = request.attribute("format");
        String id = request.queryParams("id");

        JSONObject body = new JSONObject(request.body());
        response.body(new ConverterOpenempi().patientCreate(id, body, format));
        return response;
    };

    public static Route searchPatientByGet = (request, response) -> {
        Representation format = request.attribute("format");
        LOGGER.debug("Body: " + request.body());
        response.body(new ConverterOpenempi().patientSearch(null, format));

        return response;
    };

    public static Route searchPatientByPost = (request, response) -> {
        Representation format = request.attribute("format");
        JSONObject body = new JSONObject(request.body());
        response.body(new ConverterOpenempi().patientSearch(body, format));

        return response;
    };

    public static Route readPatient = (request, response) -> {
        Representation format = request.attribute("format");
        response.body(new ConverterOpenempi().patientRead(request.params("id"), format));
        return response;
    };

    public static Route updatePatient = (request, response) -> {
        Representation format = request.attribute("format");
        JSONObject body = new JSONObject(request.body());
        response.body(new ConverterOpenempi().patientUpdate(request.params("id"), body, format));
        return response;
    };

    public static Route patchPatient = (request, response) -> {
        Representation format = request.attribute("format");
        JSONObject body = new JSONObject(request.body());
        response.body(new ConverterOpenempi().patientPatch(request.params("id"), body, format));
        return response;
    };

    public static Route deletePatient = (request, response) -> {
        Representation format = request.attribute("format");
        response.body(new ConverterOpenempi().patientDelete(request.params("id")));
        return response;
    };
}
