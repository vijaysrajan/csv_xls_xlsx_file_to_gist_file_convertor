package com.fratics.precis.file_convertor.csv_xls_xlsx_file_to_gist_file_format;


import com.opencsv.CSVReader;
import java.io.FileReader;
//import java.io.BufferedReader;
//import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileNotFoundException;
//import java.io.UnsupportedEncodingException;



public class GistCSVConvertor {

	private String csvFile = null;
	private String dataFile = null;
	private String schemaFile = null;
	private String badDataFile = null;
	private String characterEncoding = null;
	PrintWriter dataWriter = null;
	PrintWriter schemaWriter = null;
	PrintWriter badDataWriter = null;
	CSVReader dataCSVReader = null;

	public GistCSVConvertor(String _csvFileInputFile, 
			                String _dataFile, 
			                String _schemaFile, 
			                String _badDataFile, 
			                String _characterEncoding) 
			throws FileNotFoundException, UnsupportedEncodingException {
		if ( (_characterEncoding == null) || (!_characterEncoding.startsWith("UTF-"))) {
			this.characterEncoding = Constants.DEFAULT_CHARACTER_ENCODING;
		} else if ((!_characterEncoding.endsWith("8")) || (!_characterEncoding.endsWith("16")) || 
				(!_characterEncoding.endsWith("32") || (!_characterEncoding.endsWith("64")) ) ) {
			this.characterEncoding = Constants.DEFAULT_CHARACTER_ENCODING;
		} else {
			this.characterEncoding = _characterEncoding;
		}
			
		csvFile = _csvFileInputFile;
		this.dataFile = _dataFile;
		this.schemaFile = _schemaFile;
		this.badDataFile = _badDataFile;
    	dataWriter = new PrintWriter(dataFile, characterEncoding); //"UTF-8");
    	schemaWriter = new PrintWriter(schemaFile, characterEncoding); //"UTF-8");
    	badDataWriter = new PrintWriter(badDataFile, characterEncoding); //"UTF-8");
	}
	
	private int writeToSchemaFile(String [] elements) {
		StringBuilder schemaStrBuilder = new StringBuilder(); 
		for (int i = 0; i < elements.length;i++  ) {
			schemaStrBuilder.append(elements[i]);
			schemaStrBuilder.append(Constants.SCHEMA_SEPARATOR);
			schemaStrBuilder.append(Constants.DEFAULT_SCHEMA_FIELD_TYPE);
			schemaStrBuilder.append(Constants.SCHEMA_SEPARATOR);
			schemaStrBuilder.append(Constants.DEFAULT_SCHEMA_FIELD_DATA_TYPE);
			schemaStrBuilder.append(Constants.SCHEMA_SEPARATOR);
			schemaStrBuilder.append(Constants.DEFAULT_SCHEMA_FIELD_INCLUSION);
			schemaStrBuilder.append("\n");
		}
		schemaWriter.write(schemaStrBuilder.toString());
		schemaWriter.close();
		return elements.length;
	}
	
	StringBuffer buildLine = new StringBuffer();
	private void writeLineToDataFile(String [] elements) {
		for (int i = 0; i < (elements.length -1);i++  ) {
			this.buildLine.append(elements[i]);
			this.buildLine.append(Constants.SEPARATOR_STR);
		}
		this.buildLine.append(elements[elements.length - 1]);
		dataWriter.println(this.buildLine.toString());
		this.buildLine.delete(0, this.buildLine.length());
	}
	
	StringBuffer buildLine2 = new StringBuffer();
	private void writeLineToBadDataFile(String [] elements, int lineNum, char dataSeparator) {
		this.buildLine2.append(lineNum);
		this.buildLine2.append(dataSeparator);
		for (int i = 0; i < (elements.length -1);i++  ) {
			this.buildLine2.append(elements[i]);
			this.buildLine2.append(dataSeparator);
		}
		this.buildLine2.append(elements[elements.length - 1]);
		badDataWriter.println(this.buildLine2.toString());
		this.buildLine2.delete(0, this.buildLine2.length());
	}
	
	private void closeAll() throws IOException{
		dataCSVReader.close();	
		dataWriter.close();
		badDataWriter.close();
	}
	
	public void writeToGistFormat (char dataSeparator) throws BadHeaderException{
        //read file line by line
        try {
    		String [] elements = null;	
    		int lineNum = 0;
    		int numberOfHeaderFields = 0;
        	try {
        		dataCSVReader = new CSVReader(new FileReader(csvFile),dataSeparator,Constants.QUOTE_CHR,Constants.ESCAPE_CHR);
        		while ((elements = dataCSVReader.readNext()) != null) {
        			lineNum++;
        			if (lineNum == 1) {
        				numberOfHeaderFields = writeToSchemaFile(elements);
            			if ( numberOfHeaderFields <= 0) {
            				throw new BadHeaderException();
            			}
        				continue;
        			} else {
        				if (numberOfHeaderFields == elements.length) {
        					writeLineToDataFile(elements);
        				} else {
        					writeLineToBadDataFile(elements,lineNum,dataSeparator);
        				}
        			}
        		} 
        	} finally {
        		closeAll();
        	}
        } catch (IOException ioe) {
            System.out.println(ioe);
            ioe.printStackTrace();
        }
	}
	
	
	
	
	public static void main(String [] args) throws Exception {
		GistCSVConvertor gistFileFormatter = new GistCSVConvertor (args[0], args[1], args[2], args[3], "UTF-8");
		gistFileFormatter.writeToGistFormat(',');
	}
}