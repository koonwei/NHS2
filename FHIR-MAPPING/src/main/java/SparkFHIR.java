import static spark.Spark.*;
import fhirconverter.ConverterOpenempi;

public class SparkFHIR {
	public static void main(String[] args) {

		get("/hello", (req, res) -> {return ConverterOpenempi.main(null);});
	}
}
