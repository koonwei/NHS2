import static spark.Spark.*;
import fhirconverter.ConverterOpenempi;
import fhirconverter.Representation;

public class SparkFHIR {

	public static void main(String[] args) {
		path("/fhir", () -> {
			before("/*", (req, resp) -> {
				Representation format = Representation.fromString(req.queryParams("_format"));
				req.attribute("format", format);
			});
			/*TODO*/ before("/*", (req, resp) -> System.out.println("Validate Request!"));
			/*TODO*/ before("/*", (req, resp) -> System.out.println("Validate Parameters!"));
			/*TODO*/ after("/*", (req, resp) -> {
				System.out.println("Requested Type: " + req.contentType());
				System.out.println("Validate Format of " + resp.body());
			});

			path("/patient", () -> {
				post("", (req, resp) -> {
					String response_body = ConverterOpenempi.patientCreate(req.attribute("format"));
					resp.body(response_body);
					return resp;
				});
				get("",  (req, resp) ->  "searchPatient");
				path("/:id", () -> {
					get("",    (req, resp) -> {
						resp.body("readPatient " + req.params("id"));
						return resp;
					});
					put("",    (req, resp) ->   "updatePatient"+req.params("id"));
					patch("",  (req, resp) ->   "patchPatient"+req.params("id"));
					delete("", (req, resp) ->   "deletePatient"+req.params("id"));
				});
			});
		});
	}
}
