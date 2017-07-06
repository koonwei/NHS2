import static spark.Spark.*;

public class SparkFHIR {
	public static void main(String[] args) {
		get("/hello", (req, res) -> "Hello World");
	}
}
