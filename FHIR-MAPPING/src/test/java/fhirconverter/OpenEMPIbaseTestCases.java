/**
 * 
 */
package fhirconverter;

import static org.junit.Assert.*;

import org.json.JSONObject;
import org.junit.Test;

/**
 * Test Cases to test functionalities of the class OpenEMPIbase
 * @author Shruti Sinha
 *
 */
public class OpenEMPIbaseTestCases {

	private boolean flagCreate;

	/**
	 * Test for addPerson()
	 * @throws Exception
	 */
	@Test
	public void testAddPerson() throws Exception {
		OpenEMPIbase openEMPIbase = new OpenEMPIbase();
		String addParameters = "";
		String obtainedResults = openEMPIbase.commonAddPerson(addParameters);
		//assertEquals(null, obtainedResults);
		flagCreate = true;
	}

	/**
	 * Test for addPerson() with NHS as one of the identifier when NHS identifier domain not present in OpenEMPI
	 * @throws Exception
	 * 
	 */
	@Test
	public void testAddPersonWithNHSWhenNotInOpenEMPI() throws Exception {
		OpenEMPIbase openEMPIbase = new OpenEMPIbase();
		String addParametersWithoutNHSInOpenEMPI = "";
		String obtainedResults = openEMPIbase.commonAddPerson(addParametersWithoutNHSInOpenEMPI);
		//assertEquals(null, obtainedResults);

	}
	
	/**
	 * Test for addPerson() with NHS as one of the identifier when NHS identifier domain is present in OpenEMPI
	 * @throws Exception
	 */
	@Test
	public void testAddPersonWithNHSInOpenEMPI() throws Exception {
		OpenEMPIbase openEMPIbase = new OpenEMPIbase();
		String addParametersWithNHSInOpenEMPI = "";
		String obtainedResults = openEMPIbase.commonAddPerson(addParametersWithNHSInOpenEMPI);
		//assertEquals(null, obtainedResults);

	}

	/**
	 * Test for SearchPersonByAttributes()
	 * @throws Exception
	 * 
	 */
	@Test
	public void testSearchPersonByAttributes() throws Exception {
		if (flagCreate) {
			OpenEMPIbase openEMPIbase = new OpenEMPIbase();
			JSONObject searchParameters = null;
			String obtainedResults = openEMPIbase.commonSearchPersonByAttributes(searchParameters);
			//assertEquals(null, obtainedResults);
		}
	}

	/**
	 * Test for SearchPersonByAttributes() with duplicate records
	 * @throws Exception
	 * 
	 */
	@Test
	public void testSearchPersonByAttributeWithDuplicates() throws Exception {
		if (flagCreate) {
			OpenEMPIbase openEMPIbase = new OpenEMPIbase();
			JSONObject searchParametersWithDuplicates = null;
			String obtainedResults = openEMPIbase.commonSearchPersonByAttributes(searchParametersWithDuplicates);
			//assertEquals(null, obtainedResults);
		}
	}

	/**
	 * Test for SearchPersonById() with duplicate records
	 * @throws Exception
	 * 
	 */
	@Test
	public void testSearchPersonById() throws Exception {
		if (flagCreate) {
			OpenEMPIbase openEMPIbase = new OpenEMPIbase();
			JSONObject serachWithIdParameters = null;
			String obtainedResults = openEMPIbase.commonSearchPersonById(serachWithIdParameters);
			//assertEquals(null, obtainedResults);
		}

	}

	/**
	 * Test for ReadPerson() with person Id
	 * @throws Exception
	 * 
	 */
	@Test
	public void testReadPerson() throws Exception {
		if (flagCreate) {
			OpenEMPIbase openEMPIbase = new OpenEMPIbase();
			String readParameters = null;
			String obtainedResults = openEMPIbase.commonReadPerson(readParameters);
			//assertEquals(null, obtainedResults);
		}

	}

	/**
	 * Test for UpdatePerson() 
	 * @throws Exception
	 * 
	 */
	@Test
	public void testUpdatePerson() throws Exception {
		if (flagCreate) {
			OpenEMPIbase openEMPIbase = new OpenEMPIbase();
			String updateParameters = null;
			String obtainedResults = openEMPIbase.commonUpdatePerson(updateParameters);
			//assertEquals(null, obtainedResults);
		}

	}

	/**
	 * Test for UpdatePerson() when the parameters do not exists, so a new person record is created
	 * @throws Exception
	 * 
	 */
	@Test
	public void testUpdatePersonWithCreate() throws Exception {
		if (flagCreate) {
			OpenEMPIbase openEMPIbase = new OpenEMPIbase();
			String updateCreateParameters = null;
			String obtainedResults = openEMPIbase.commonUpdatePerson(updateCreateParameters);
			//assertEquals(null, obtainedResults);
		}

	}

	/**
	 * Test for DeletePersonById()
	 * @throws Exception
	 * 
	 */
	@Test
	public void testDeletePersonById() throws Exception {
		if (flagCreate) {
			OpenEMPIbase openEMPIbase = new OpenEMPIbase();
			String deleteParameters = null;
			String obtainedResults = openEMPIbase.commonDeletePersonById(deleteParameters);
			//assertEquals(null, obtainedResults);
		}

	}

	/**
	 * Test for RemovePersonById()
	 * @throws Exception
	 * 
	 */
	@Test
	public void testRemovePersonById() throws Exception {
		if (flagCreate) {
			OpenEMPIbase openEMPIbase = new OpenEMPIbase();
			String removeParameters = null;
			String obtainedResults = openEMPIbase.commonRemovePersonById(removeParameters);
			//assertEquals(null, obtainedResults);
		}

	}

	/**
	 * Test for checkIfIdendifierExists()
	 * @throws Exception
	 * 
	 */
	@Test
	public void testCheckIfIdentifierExists() throws Exception {
		OpenEMPIbase openEMPIbase = new OpenEMPIbase();
		String identifierParameters = "NHS";
		Boolean obtainedResults = openEMPIbase.checkIfIdendifierExists(identifierParameters);
		//assertEquals(null, obtainedResults);

	}

}
