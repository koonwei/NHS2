/**
 * 
 */
package fhirconverter.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.json.JSONObject;
import org.junit.Test;

/**
 * Test Cases to test functionalities of the class OpenEMPIbase
 * 
 * @author Shruti Sinha
 *
 */
public class OpenEMPIConnectorTests {

	public static String addPersonParameters = "<person><address1>8 Winterhouse</address1>"
			+ "<address2>Main Road</address2>"
			+ "<birthOrder>3</birthOrder>"
			+ "<birthPlace>Brighton</birthPlace>"
			+ "<city>London</city>"
			+ "<country>United Kingdom</country>"
			+ "<countryCode>UK</countryCode>"
			+ "<dateOfBirth>1932-10-19T00:00:00Z</dateOfBirth>"
			+ "<deathInd>23</deathInd>"
			+ "<deathTime>2017-03-19T12:00:00Z</deathTime>"
			+ "<degree>Masters</degree>"
			+ "<familyName>Mill</familyName>"
			+ "<fatherName>Pret</fatherName>"
			+ "<gender>"
			+ "<genderCd>1</genderCd>"
			+ "<genderCode>F</genderCode>"
			+ "<genderDescription>Female</genderDescription>"
			+ "<genderName>Female</genderName>"
			+ "</gender>"
			+ "<givenName>Anna</givenName>"
			+ "<maritalStatusCode>Married</maritalStatusCode>"
			+ "<middleName>Will</middleName>"
			+ "<motherName>Min</motherName>"
			+ "<mothersMaidenName>Kim</mothersMaidenName>"
			+ "<multipleBirthInd>2</multipleBirthInd>"
			+ "<personIdentifiers>"
			+ "<identifier>555</identifier>"
			+ "<identifierDomain>"
			+ "<identifierDomainId>10</identifierDomainId>"
			+ "<identifierDomainName>SSN</identifierDomainName>"
			+ "</identifierDomain>"
			+ "</personIdentifiers>"
			+ "<phoneAreaCode>44</phoneAreaCode>"
			+ "<phoneCountryCode>44</phoneCountryCode>"
			+ "<phoneExt>888</phoneExt>"
			+ "<phoneNumber>90909090</phoneNumber>"
			+ "<postalCode>SE1234L</postalCode>"
			+ "<prefix>Ms</prefix>"
			+ "<ssn>666</ssn>"
			+ "<state>London</state>"
			+ "<suffix>SS</suffix>"
			+ "<language>English</language>"
			+ "<email>anna@gmail.com</email>"
			+ "</person>";
	
	public static String addParametersWithNHS = "<person><address1>8 TowerHouse</address1>"
			+ "<address2>Main Road</address2>"
			+ "<birthOrder>3</birthOrder>"
			+ "<birthPlace>Ipswich</birthPlace>"
			+ "<city>London</city>"
			+ "<country>United Kingdom</country>"
			+ "<countryCode>UK</countryCode>"
			+ "<dateOfBirth>1982-10-19T00:00:00Z</dateOfBirth>"
			+ "<deathInd>23</deathInd>"
			+ "<degree>Masters</degree>"
			+ "<familyName>Priti</familyName>"
			+ "<fatherName>Tom</fatherName>"
			+ "<gender>"
			+ "	<genderCd>1</genderCd>"
			+ "	<genderCode>F</genderCode>"
			+ "	<genderDescription>Female</genderDescription>"
			+ "	<genderName>Female</genderName>"
			+ "</gender>"
			+ "<givenName>Gemma</givenName>"
			+ "<maritalStatusCode>Single</maritalStatusCode>"
			+ "<middleName>Hen</middleName>"
			+ "<motherName>Jolly</motherName>"
			+ "<mothersMaidenName>Lisa</mothersMaidenName>"
			+ "<multipleBirthInd>2</multipleBirthInd>"
			+ "<personIdentifiers>"
			+ "<identifier>555</identifier>"
			+ "<identifierDomain>"
			+ "<identifierDomainName>NHS</identifierDomainName>"
			+ "</identifierDomain>"
			+ "</personIdentifiers>"
			+ "<phoneAreaCode>44</phoneAreaCode>"
			+ "<phoneCountryCode>44</phoneCountryCode>"
			+ "<phoneExt>888</phoneExt>"
			+ "<phoneNumber>90909090</phoneNumber>"
			+ "<postalCode>SE1234L</postalCode>"
			+ "<prefix>Ms</prefix>"
			+ "<ssn>666</ssn>"
			+ "<state>London</state>"
			+ "<suffix>SS</suffix>"
			+ "<language>English</language>"
			+ "<email>gen@gmail.com</email>"
			+ "</person>";
	
	public static String updatePersonParameters = "<person>"
			+ "<birthPlace>Brighton</birthPlace>"
			+ "<city>London</city>"
			+ "<country>United Kingdom</country>"
			+ "<countryCode>UK</countryCode>"
			+ "<deathInd>23</deathInd>"
			+ "<familyName>Mill</familyName>"
			+ "<fatherName>Pret</fatherName>"
			+ "<gender>"
			+ "<genderCd>1</genderCd>"
			+ "<genderCode>F</genderCode>"
			+ "<genderDescription>Female</genderDescription>"
			+ "<genderName>Female</genderName>"
			+ "</gender>"
			+ "<givenName>Anna</givenName>"
			+ "<middleName>Penny</middleName>"
			+ "<motherName>Min</motherName>"
//			+ "<personIdentifiers>"
//			+ "<identifier>5000</identifier>"
//			+ "<identifierDomain>"
//			+ "<identifierDomainId>10</identifierDomainId>"
//			+ "<identifierDomainName>SSN</identifierDomainName>"
//			+ "</identifierDomain>"
//			+ "</personIdentifiers>"
			+ "<phoneAreaCode>44</phoneAreaCode>"
			+ "<phoneCountryCode>44</phoneCountryCode>"
			+ "<phoneExt>888</phoneExt>"
			+ "<phoneNumber>90909090</phoneNumber>"
			+ "<postalCode>SE1234L</postalCode>"
			+ "<prefix>Ms</prefix>"
			+ "<state>London</state>"
			+ "<language>English</language>"
			+ "</person>";
	
	/**
	 * Test for addPerson()
	 * 
	 * @throws Exception
	 */
	@Test
	public void testAddPerson() throws Exception {
		String personIdSSN = null;
		OpenEMPIConnector openEMPIconnector = new OpenEMPIConnector();
		String obtainedResults = openEMPIconnector.commonAddPerson(addPersonParameters);
		if (!obtainedResults.isEmpty()) {
			personIdSSN = obtainedResults.substring(obtainedResults.indexOf("<personId>") + 10,
					obtainedResults.indexOf("</personId>"));
		}
		System.out.println("testAddPerson");
		openEMPIconnector.commonRemovePersonById(personIdSSN);
		assertNotNull(personIdSSN);
	}

//	/**
//	 * Test for addPerson() with NHS as one of the identifier
//	 * 
//	 * @throws Exception
//	 */
//	@Test
//	public void testAddPersonWithNHS() throws Exception {
//		String personIdNHS = null;
//		OpenEMPIbase openEMPIconnector = new OpenEMPIbase();
//		String obtainedResults = openEMPIconnector.commonAddPerson(addParametersWithNHS);
//		if (!obtainedResults.isEmpty()) {
//			personIdNHS = obtainedResults.substring(obtainedResults.indexOf("<personId>") + 10,
//					obtainedResults.indexOf("</personId>"));
//		}
//		System.out.println("testAddPersonWithNHS");
//		openEMPIconnector.commonRemovePersonById(personIdNHS);
//		assertNotNull(personIdNHS);
//	}

	/**
	 * Test for SearchPersonByAttributes()
	 * 
	 * @throws Exception
	 * 
	 */
	@Test
	public void testSearchPersonByAttributes() throws Exception {
		String personId = "";
		OpenEMPIConnector openEMPIconnector = new OpenEMPIConnector();
		String obtainedResultsAdd = openEMPIconnector.commonAddPerson(addPersonParameters);
		String test = "{ \"family\" : \"Mill\"}";
		if (!obtainedResultsAdd.isEmpty()) {
			personId = obtainedResultsAdd.substring(obtainedResultsAdd.indexOf("<personId>") + 10,
					obtainedResultsAdd.indexOf("</personId>"));
		}
		JSONObject searchParameters = new JSONObject(test);
		String obtainedResults = openEMPIconnector.commonSearchPersonByAttributes(searchParameters);
		System.out.println("testSearchPersonByAttributes");
		openEMPIconnector.commonRemovePersonById(personId);
		assertNotNull(obtainedResults);
	}

	/**
	 * Test for SearchPersonById() with duplicate records
	 * 
	 * @throws Exception
	 * 
	 */
	@Test
	public void testSearchPersonById() throws Exception {
		String personId = "";
		OpenEMPIConnector openEMPIconnector = new OpenEMPIConnector();
		String obtainedResultsAdd = openEMPIconnector.commonAddPerson(addPersonParameters);
		if (!obtainedResultsAdd.isEmpty()) {
			personId = obtainedResultsAdd.substring(obtainedResultsAdd.indexOf("<personId>") + 10,
					obtainedResultsAdd.indexOf("</personId>"));
		}
		String test = "{ \"identifier_value\" : \"555\", \"identifier_domain\" : \"NHS\"}";
		JSONObject serachWithIdParameters = new JSONObject(test);
		String obtainedResults = openEMPIconnector.commonSearchPersonById(serachWithIdParameters);
		System.out.println("testSearchPersonById");
		openEMPIconnector.commonRemovePersonById(personId);
		assertNotNull(obtainedResults);

	}

	/**
	 * Test for ReadPerson() with person Id
	 * 
	 * @throws Exception
	 * 
	 */
	@Test
	public void testReadPerson() throws Exception {
		String personId = null;
		OpenEMPIConnector openEMPIconnector = new OpenEMPIConnector();
		String obtainedResultsAdd = openEMPIconnector.commonAddPerson(addPersonParameters);
		if (!obtainedResultsAdd.isEmpty()) {
			personId = obtainedResultsAdd.substring(obtainedResultsAdd.indexOf("<personId>") + 10,
					obtainedResultsAdd.indexOf("</personId>"));
		}
		String obtainedResults = openEMPIconnector.commonReadPerson(personId);
		openEMPIconnector.commonRemovePersonById(personId);
		System.out.println("testAddPersonWithNHS");
		assertNotNull(obtainedResults);

	}

	/**
	 * Test for UpdatePerson()
	 * 
	 * @throws Exception
	 * 
	 */
	@Test
	public void testUpdatePerson() throws Exception {
		String personId = "";
		OpenEMPIConnector openEMPIconnector = new OpenEMPIConnector();
		String obtainedResultsAdd = openEMPIconnector.commonAddPerson(updatePersonParameters);
		if (!obtainedResultsAdd.isEmpty()) {
			personId = obtainedResultsAdd.substring(obtainedResultsAdd.indexOf("<personId>") + 10,
					obtainedResultsAdd.indexOf("</personId>"));
		}
		obtainedResultsAdd = obtainedResultsAdd.replace("Mill", "George");
		String obtainedResults = openEMPIconnector.commonUpdatePerson(obtainedResultsAdd);
		String expectedUpdatePersonResults = "Updated";
		openEMPIconnector.commonRemovePersonById(personId);
		System.out.println("testUpdatePerson");
		
		assertEquals(expectedUpdatePersonResults, obtainedResults);

	}

	/**
	 * Test for RemovePersonById(). This test will first check if a person
	 * record exist to be removed
	 * 
	 * @throws Exception
	 * 
	 */
	@Test
	public void testRemovePersonById() throws Exception {
		String personId = null;
		OpenEMPIConnector openEMPIconnector = new OpenEMPIConnector();
		String obtainedResultsAdd = openEMPIconnector.commonAddPerson(addPersonParameters);
		if (!obtainedResultsAdd.isEmpty()) {
			personId = obtainedResultsAdd.substring(obtainedResultsAdd.indexOf("<personId>") + 10,
					obtainedResultsAdd.indexOf("</personId>"));
		}
		String removeParameters = personId;
		String expectedRemovePersonByIdResults = "Remove Successful";
		String obtainedResults = openEMPIconnector.commonRemovePersonById(removeParameters);
		System.out.println("testRemovePersonById");
		assertEquals(expectedRemovePersonByIdResults, obtainedResults);
	}
	
//	@Test
//	public void testGetIdentifierDomains() throws Exception {
//		OpenEMPIconnector openEMPIconnector = new OpenEMPIconnector();
//		List<String> domainList = openEMPIconnector.getIdentifierDomains();
//	}
}

