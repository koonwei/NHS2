package fhirconverter.spark;

import fhirconverter.ConverterOpenempi;

import org.hl7.fhir.dstu3.model.Patient;
import spark.*;

import java.util.HashMap;

public class PatientController {
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
        response.body(new ConverterOpenempi().groupCreate(format));
        return response;
    };

    public static Route searchPatient = (request, response) -> {
        Representation format = request.attribute("format");
        //TODO: Update to JSON
        response.body(new ConverterOpenempi().patientSearch(request.params("id"), format));
        return response;
    };

    public static Route readPatient = (request, response) -> {
        Representation format = request.attribute("format");
        response.body(new ConverterOpenempi().patientRead(request.params("id"), format));
        return response;
    };

    public static Route updatePatient = (request, response) -> {
        Representation format = request.attribute("format");
        response.body(new ConverterOpenempi().patientUpdate(request.params("id"), format));
        return response;
    };

    public static Route patchPatient = (request, response) -> {
        Representation format = request.attribute("format");
        response.body(new ConverterOpenempi().patientPatch(request.params("id"), format));
        return response;
    };

    public static Route deletePatient = (request, response) -> {
        Representation format = request.attribute("format");
        response.body(new ConverterOpenempi().patientDelete(request.params("id")));
        return response;
    };
}
