package com.fratics.precis.file_convertor.csv_xls_xlsx_file_to_gist_file_format;


import com.opencsv.CSVReader;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.StringReader;
//import java.util.Iterator;
import java.io.IOException;
import java.io.PrintWriter;



public class GistCSVConvertor {
	
	public static final String SEPARATOR_STR = "\u0001";
	public static final char SEPARATOR_CHR = '\u0001';
	public static final char QUOTE_CHR = '"';
	public static final char ESCAPE_CHR = '\\';
	
	
	private String csvFile = null;
	

	public GistCSVConvertor(String _csvFile) {
		csvFile = _csvFile;
	}
	
	
	public void writeToGistFormat2 () {

        //read file line by line
        try {

        	//CSVParser csvParser = new CSVParser(',','"');
        	//CSVReader csvReader = new CSVReader(new FileReader(csvFile),0, csvParser);
        	//CSVReader csvReader = new CSVReader(new FileReader(csvFile),',','\"','\\');        	
        	//CSVReader csvReader = new CSVReader(new StringReader("value1, value2, value3, value4, \"value5, 1234\", " +
    	    //  "value6, value7, \"value8\", value9, \"value10, 123.23\""));

        	//read file line by line
        	PrintWriter writer = new PrintWriter("dataFile.txt", "UTF-8");
            BufferedReader br = new BufferedReader(new FileReader(csvFile));
            String line;
            while ((line = br.readLine()) != null) {
            	//System.out.println(line);
                CSVReader csvReader = new CSVReader(new StringReader(line));//,',','"');
                String [] elements = null;
                StringBuffer buildLine = new StringBuffer();
                while ((elements = csvReader.readNext()) != null) {
                	for (int i = 0; i < (elements.length -1);i++  ) {
                		buildLine.append(elements[i]);
                		buildLine.append(SEPARATOR_STR);
                	}
                	buildLine.append(elements[elements.length - 1]);
                	writer.println(buildLine.toString());
                	buildLine.delete(0, buildLine.length());    
                 }
                 csvReader.close();	
                }
            	br.close();
            	writer.close();
        	
        } catch (IOException ioe) {
            System.out.println(ioe);
            ioe.printStackTrace();
        }
	}
	
	public void writeToGistFormat () {

        //read file line by line
        try {
        	CSVReader dataCSVReader = new CSVReader(new FileReader(csvFile),',',QUOTE_CHR,ESCAPE_CHR);        	
        	PrintWriter dataWriter = new PrintWriter("dataFile.txt", "UTF-8");
        	PrintWriter schemaWriter = new PrintWriter("schemaFile.txt", "UTF-8");
            String [] elements = null;	
            StringBuffer buildLine = new StringBuffer();
            int lineNum = 0;
            while ((elements = dataCSVReader.readNext()) != null) {
            	lineNum++;
            	if (lineNum == 1) {
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
            		continue;
            	}
            	for (int i = 0; i < (elements.length -1);i++  ) {
            		buildLine.append(elements[i]);
            		buildLine.append(SEPARATOR_STR);
            	}
                buildLine.append(elements[elements.length - 1]);
                dataWriter.println(buildLine.toString());
                buildLine.delete(0, buildLine.length());    
            }
            dataCSVReader.close();	
            dataWriter.close();
        	
        } catch (IOException ioe) {
            System.out.println(ioe);
            ioe.printStackTrace();
        }
	}
	
	
	
	
	public static void main(String [] args) {
		GistCSVConvertor gistFileFormatter = new GistCSVConvertor (args[0]);
		gistFileFormatter.writeToGistFormat();
	}
	

}
