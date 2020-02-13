package no.comp.cinema.helper;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateConverter {
	private DateConverter() {}
	
	public static String DateToString(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.applyPattern("yyyy-MM-dd");
		
		return sdf.format(date);
	}
	
	public static Date StringToDate(String str) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.applyPattern("yyyy-MM-dd");
		return sdf.parse(str);
	}
}
