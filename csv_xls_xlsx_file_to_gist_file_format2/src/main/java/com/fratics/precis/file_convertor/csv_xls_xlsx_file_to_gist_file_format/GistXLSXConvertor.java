package com.fratics.precis.file_convertor.csv_xls_xlsx_file_to_gist_file_format;


import java.io.File;
import org.apache.commons.collections.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.Row;

public class GistXLSXConvertor {
	
	private String xlsxFile = null;
	private String dataFile = null;
	private String schemaFile = null;
	private String badDataFile = null;
	private String characterEncoding = null;
	PrintWriter dataWriter = null;
	PrintWriter schemaWriter = null;
	PrintWriter badDataWriter = null;
	XSSFWorkbook workbook = null;
	FileInputStream file = null;
	
	public GistXLSXConvertor(String _xlsxFileInputFile, 
            String _dataFile, 
            String _schemaFile, 
            String _badDataFile, 
            String _characterEncoding) throws FileNotFoundException, UnsupportedEncodingException, IOException {

		dataFile = _dataFile;
		schemaFile = _schemaFile;
		badDataFile = _badDataFile;
		if ( (_characterEncoding == null) || (!_characterEncoding.startsWith("UTF-"))) {
			this.characterEncoding = Constants.DEFAULT_CHARACTER_ENCODING;
		} else if ((!_characterEncoding.endsWith("8")) || (!_characterEncoding.endsWith("16")) || 
				  (!_characterEncoding.endsWith("32") || (!_characterEncoding.endsWith("64")) ) ) {
			this.characterEncoding = Constants.DEFAULT_CHARACTER_ENCODING;
		} else {
			this.characterEncoding = _characterEncoding;
		}
		this.xlsxFile = _xlsxFileInputFile;
		this.dataFile = _dataFile;
		this.schemaFile = _schemaFile;
		this.badDataFile = _badDataFile;
		this.dataWriter = new PrintWriter(dataFile, characterEncoding); //"UTF-8");
		this.schemaWriter = new PrintWriter(schemaFile, characterEncoding); //"UTF-8");
		this.badDataWriter = new PrintWriter(badDataFile, characterEncoding); //"UTF-8");
		this.file = new FileInputStream(new File(this.xlsxFile));
		workbook = new XSSFWorkbook(this.file);
	}
	
	
	private String actualCellType(int cellType, Cell cell) {
		StringBuilder retVal = new StringBuilder();
         if (cellType == XSSFCell.CELL_TYPE_STRING) {
        	 retVal.append(cell.getStringCellValue());
         } else if (cellType == XSSFCell.CELL_TYPE_NUMERIC) {
        	 retVal.append(cell.getNumericCellValue());
         } else if (cellType == XSSFCell.CELL_TYPE_BOOLEAN) {
        	 retVal.append(cell.getBooleanCellValue());
         } else {
        	 return null;
         }
         return retVal.toString();
	}
	
	
	@SuppressWarnings({ "deprecation" })
	private String getCellStringValue(Cell cell) {
			String retVal =  actualCellType(cell.getCellType(), cell);
			XSSFFormulaEvaluator evaluator =  workbook.getCreationHelper().createFormulaEvaluator();
			//XSSFFormulaEvaluator.evaluateAllFormulaCells(workbook);
			//evaluator.evaluateAll();   // evaluateFormulaCellValue(cell);
			
			if (retVal == null) {
				if (cell.getCellType() == XSSFCell.CELL_TYPE_FORMULA) {
					//Cell newEvaluatedCell = evaluator.evaluateInCell(cell);
					//actualCellType(newEvaluatedCell.getCellType(), newEvaluatedCell);
					XSSFCell cellValue = evaluator.evaluateInCell(cell);
					retVal = actualCellType(cellValue.getCellType(), cellValue);
					//evaluator.
//				} else {
//					retVal = actualCellType(cell.getCellType(), cell);
				}
			}
			if (retVal == null) retVal = "";
	        return retVal;
	}
	
	
	private int writeToSchemaFile(Row row) {
		//For each row, iterate through each columns
		Iterator<Cell> cellIterator = row.cellIterator();
		StringBuilder schemaStrBuilder = new StringBuilder(); 
		int colCount = 0;
		while(cellIterator.hasNext()) {	
			Cell cell = cellIterator.next();
			
			if (getCellStringValue(cell).equals("") || 
			    (cell.getCellType() == Cell.CELL_TYPE_BLANK)  ) {
				break;
			}
			colCount++;
			schemaStrBuilder.append(getCellStringValue(cell));
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
		return colCount;
	}
	private void writeLineToDataOrBadDataFile(Row row, int rowNum, int colNum) {
		Iterator<Cell> cellIterator = row.cellIterator();
		ArrayList<String> arr = new ArrayList<String>();
		int colCount = 0;
		Cell cell = null;

		while(cellIterator.hasNext() ) {
			cell = cellIterator.next();
			arr.add(getCellStringValue(cell));
			colCount++;
		}
		if (colNum < colCount) { //bad data
			writeLineToBadDataFile(arr,rowNum);
		} else {
			for (int i = 0; i< (colNum - colCount); i++) {
				arr.add("");
			}
		}
		writeLineToDataFile(arr);
	}
	
	StringBuffer buildLine2 = new StringBuffer();
	private void writeLineToBadDataFile(ArrayList<String> arr, int lineNum) {
		//For each row, iterate through each columns
		int sz = arr.size();
		while (sz > 1)
		{
			String str = arr.remove(0);
			this.buildLine2.append(str);
			this.buildLine2.append(Constants.SEPARATOR_STR);
			sz--;
		}
		this.buildLine2.append(arr.remove(0));
		dataWriter.println(this.buildLine2.toString());
		this.buildLine2.delete(0, this.buildLine2.length());
	}
	
	StringBuffer buildLine = new StringBuffer();
	private void writeLineToDataFile(ArrayList<String> arr) {
		//For each row, iterate through each columns
		int sz = arr.size();
		//System.out.println("sz = " + sz);
		while (sz > 1)
		{
			String str = arr.remove(0);
			str = str.replace("\n", " ").replace("\r", " ");
			this.buildLine.append(str);
			this.buildLine.append(Constants.SEPARATOR_STR);
			sz--;
		}
		this.buildLine.append(arr.remove(0));
		dataWriter.println(this.buildLine.toString());
		this.buildLine.delete(0, this.buildLine.length());
	}
	
	public void writeToGistFormat (int sheetNum) throws BadXLSSheetNumberException, IOException {
		
		//Get first sheet from the workbook
		XSSFSheet sheet = workbook.getSheetAt(sheetNum);
		if (sheet == null) {
			throw new BadXLSSheetNumberException();
		}
		//Get iterator to all the rows in current sheet
		Iterator<Row> rowIterator = sheet.iterator();
		int rowNum = 0;
		int colCount = 0;
		while(rowIterator.hasNext()) {
			rowNum++;
			Row row = rowIterator.next();
			if (rowNum == 1) {
				colCount = writeToSchemaFile(row);
			} else {
				writeLineToDataOrBadDataFile(row,rowNum,colCount);
			}
		}
		closeAll();
	}
	
	private void closeAll() throws IOException {
		this.file.close();	
		dataWriter.close();
		badDataWriter.close();
	}
	
	public static void main(String[] args ) throws Exception {	
		GistXLSXConvertor g = new GistXLSXConvertor(  "test.xlsx" , //"test.xlsx", 
	            "xlsxDataFile", 
	            "xlsxSchemaFile", 
	            "xlsxBadDataFile", 
	           "UTF-8") ;
		
		g.writeToGistFormat(0);
		
		g = new GistXLSXConvertor("./test.xlsx", 
	            "xlsxDataFile2", 
	            "xlsxSchemaFile2", 
	            "xlsxBadDataFile2", 
	           "UTF-8") ;
		g.writeToGistFormat(1);
	}

}
