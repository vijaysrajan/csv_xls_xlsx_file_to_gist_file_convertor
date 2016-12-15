package com.fratics.precis.file_convertor.csv_xls_xlsx_file_to_gist_file_format;

public class BadXLSSheetNumberException extends Exception{

	private static final long serialVersionUID = -1;
	private String msg;
	BadXLSSheetNumberException() {
		msg = "The sheet number must be valid.";
	}
	
	public String getMsg() {
		return msg;
	}
	
}
