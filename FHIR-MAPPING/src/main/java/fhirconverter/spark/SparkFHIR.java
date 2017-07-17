package fhirconverter.spark;

import static spark.Spark.*;

public class SparkFHIR {

    private static final String rootPath = "";
    private static final String searchPath = "/_search";
    private static final String basePath = "/fhir";

    public static void main(String[] args) {

        path(basePath, () -> {
            before("/*", Filters.formatFilter);
            /*TODO*/
            before("/*", (req, resp) -> System.out.println("Validate Parameters!"));
            /*TODO*/
            after("/*", Filters.responseFormatValidater);

            path("/Patient", () -> {
                post(rootPath, PatientController.createPatient);
                get(rootPath,  PatientController.searchPatientByGet);
                post(searchPath,  PatientController.searchPatientByPost);
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
