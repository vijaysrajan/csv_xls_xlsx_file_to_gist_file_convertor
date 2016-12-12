package com.fratics.precis.file_convertor.csv_xls_xlsx_file_to_gist_file_format;

public class BadHeaderException extends Exception {
	private static final long serialVersionUID = -1;
	private String msg;
	BadHeaderException() {
		msg = "The header must have at least one field.";
	}
	
	public String getMsg() {
		return msg;
	}
	
}
