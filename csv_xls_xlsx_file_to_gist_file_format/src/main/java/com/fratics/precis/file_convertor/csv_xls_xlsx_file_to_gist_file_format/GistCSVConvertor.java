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
			schemaStrBuilder.append(":");
			schemaStrBuilder.append("d");
			schemaStrBuilder.append(":");
			schemaStrBuilder.append("string");
			schemaStrBuilder.append(":");
			schemaStrBuilder.append("t");
			schemaStrBuilder.append("\n");
		}
		schemaWriter.write(schemaStrBuilder.toString());
		schemaWriter.close();
		return elements.length;
	}
	
	private void writeLineToDataFile(String [] elements) {
		StringBuffer buildLine = new StringBuffer();
		for (int i = 0; i < (elements.length -1);i++  ) {
			buildLine.append(elements[i]);
			buildLine.append(Constants.SEPARATOR_STR);
		}
		buildLine.append(elements[elements.length - 1]);
		dataWriter.println(buildLine.toString());
		buildLine.delete(0, buildLine.length());
	}
	
	private void writeLineToBadDataFile(String [] elements) {
		StringBuffer buildLine = new StringBuffer();
		for (int i = 0; i < (elements.length -1);i++  ) {
			buildLine.append(elements[i]);
			buildLine.append(Constants.SEPARATOR_STR);
		}
		buildLine.append(elements[elements.length - 1]);
		badDataWriter.println(buildLine.toString());
		buildLine.delete(0, buildLine.length());
	}
	
	private void closeAll() throws IOException{
		dataCSVReader.close();	
		dataWriter.close();
		badDataWriter.close();
	}
	
	public void writeToGistFormat () throws BadHeaderException{
        //read file line by line
        try {
    		String [] elements = null;	
    		int lineNum = 0;
    		int numberOfHeaderFields = 0;
        	try {
        		dataCSVReader = new CSVReader(new FileReader(csvFile),',',Constants.QUOTE_CHR,Constants.ESCAPE_CHR);
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
        					writeLineToBadDataFile(elements);
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
		gistFileFormatter.writeToGistFormat();
	}
	

}




//public void writeToGistFormat2 () {
//
//    //read file line by line
//    try {
//
//    	//CSVParser csvParser = new CSVParser(',','"');
//    	//CSVReader csvReader = new CSVReader(new FileReader(csvFile),0, csvParser);
//    	//CSVReader csvReader = new CSVReader(new FileReader(csvFile),',','\"','\\');        	
//    	//CSVReader csvReader = new CSVReader(new StringReader("value1, value2, value3, value4, \"value5, 1234\", " +
//	    //  "value6, value7, \"value8\", value9, \"value10, 123.23\""));
//
//    	//read file line by line
//    	PrintWriter writer = new PrintWriter("dataFile.txt", "UTF-8");
//        BufferedReader br = new BufferedReader(new FileReader(csvFile));
//        String line;
//        while ((line = br.readLine()) != null) {
//        	//System.out.println(line);
//            CSVReader csvReader = new CSVReader(new StringReader(line));//,',','"');
//            String [] elements = null;
//            StringBuffer buildLine = new StringBuffer();
//            while ((elements = csvReader.readNext()) != null) {
//            	for (int i = 0; i < (elements.length -1);i++  ) {
//            		buildLine.append(elements[i]);
//            		buildLine.append(Constants.SEPARATOR_STR);
//            	}
//            	buildLine.append(elements[elements.length - 1]);
//            	writer.println(buildLine.toString());
//            	buildLine.delete(0, buildLine.length());    
//             }
//             csvReader.close();	
//            }
//        	br.close();
//        	writer.close();
//    	
//    } catch (IOException ioe) {
//        System.out.println(ioe);
//        ioe.printStackTrace();
//    }
//}