package fhirconverter.spark;

import static spark.Spark.*;
import fhirconverter.ConverterOpenempi;

public class SparkFHIR {

    private static final String rootPath = "";

    public static void main(String[] args) {

        path("/fhir", () -> {
            before("/*", Filters.requestFormatFilter);
            /*TODO*/
            before("/*", (req, resp) -> System.out.println("Validate Parameters!"));
            /*TODO*/
            after("/*", Filters.responseFormatValidater);

            path("/patient", () -> {
                post(rootPath, PatientController.createPatient);
                get(rootPath,  PatientController.searchPatient);
                path("/:id", () -> {
                    get(rootPath,    PatientController.readPatient);
                    put(rootPath,   PatientController.updatePatient);
                    patch(rootPath,  PatientController.patchPatient);
                    delete(rootPath, PatientController.deletePatient);
                });
            });
        });
    }
}
