/**
 * 
 */
package fhirconverter.converter;

import org.json.JSONObject;

/**
 * @author Shruti Sinha
 *
 */
public class Test {

	public static void main(String a[]) throws Exception{
		OpenEMPIbase o = new OpenEMPIbase();
		String test = "{ \"name\" : \"Didac\", \"family\" : \"Magrina\"}";
		String test2 = "{ \"family\" : \"Magrina\"}";
		JSONObject j = new JSONObject(test);
		JSONObject j2 = new JSONObject(test2);
		System.out.println("----------Didac Magrina-----------");
		o.commonSearchPersonByAttributes(j);
		System.out.println("----------Magrina-----------");
		o.commonSearchPersonByAttributes(j2);
	}
}
