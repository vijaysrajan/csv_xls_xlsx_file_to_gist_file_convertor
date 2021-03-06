package com.fratics.precis.file_convertor.csv_xls_xlsx_file_to_gist_file_format;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp_SimpleTestCaseComma()
    {
    	String csvFile  = "simpleTestCaseComma.csv";
    	String schema   = "schemaFile_simpleTestCaseComma";
    	String dataFile = "dataFile_simpleTestCaseComma";
    	String badDataFile = "badDataFileCase_simpleTestCaseComma";
    	try {
    		GistCSVConvertor gistFileFormatter = new GistCSVConvertor (csvFile, dataFile, schema, badDataFile, "UTF-8");
    		gistFileFormatter.writeToGistFormat(',');
    		//check if schema File is of expected length
    		//check if dataFile is of expected length
    		//check if badDataFile is of expected length
    		//check contents of full schema file
    		//check contents of full dataFile file
    		//check contents of full badDataFile file
    		assertTrue( true );
    	} catch (Exception e) {
    		assertTrue( false );
    	}
    }
    
    public void testApp_SimpleTestCasePipe()
    {
    	String csvFile  = "simpleTestCasePipe.csv";
    	String schema   = "schemaFile_simpleTestCasePipe";
    	String dataFile = "dataFile_simpleTestCasePipe";
    	String badDataFile = "badDataFileCase_simpleTestCasePipe";
    	try {
    		GistCSVConvertor gistFileFormatter = new GistCSVConvertor (csvFile,dataFile, schema, badDataFile, "UTF-8");
    		gistFileFormatter.writeToGistFormat('|');
    		//check if schema File is of expected length
    		//check if dataFile is of expected length
    		//check if badDataFile is of expected length
    		//check contents of full schema file
    		//check contents of full dataFile file
    		//check contents of full badDataFile file
    		assertTrue( true );
    	} catch (Exception e) {
    		assertTrue( false );
    	}
    }
}
