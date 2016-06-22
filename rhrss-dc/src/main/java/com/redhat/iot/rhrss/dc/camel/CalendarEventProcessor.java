package com.redhat.iot.rhrss.dc.camel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;

import javax.sql.DataSource;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;

public class CalendarEventProcessor implements Processor {

	private static final String dataFormat = "yyyy-MM-dd HH:mm";
	private static Logger log = Logger.getLogger(CalendarEventProcessor.class);
	
	
	private DataSource datasource = null;
	private SimpleDateFormat simpleDateFormat = null;
	
	public CalendarEventProcessor() {
		simpleDateFormat = new SimpleDateFormat(dataFormat);
	}
	
	@Override
	public void process(Exchange exchange) throws Exception {
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			
			String[] reservation = exchange.getIn().getBody(java.lang.String.class).split(",");
			
			con = datasource.getConnection();
									
			stmt = con.prepareStatement("INSERT INTO reservations (location, room , reservation_start , reservation_end , reserved) VALUES (?,?,?,?,?)");
			stmt.setString(1, reservation[0]);
			stmt.setString(2, reservation[1]);
			stmt.setLong(3, simpleDateFormat.parse(reservation[2]).getTime());
			stmt.setLong(4, simpleDateFormat.parse(reservation[3]).getTime());
			stmt.setInt(5, Integer.parseInt(reservation[4]));
			stmt.execute();
			stmt.close();
			
		} catch (Exception e) {
			log.error("Error" + e.getMessage(), e);
			if (stmt != null) {
				stmt.close();
			}
			
			if (con != null) {
				con.close();
			}
		} finally {
			if (stmt != null) {
				stmt.close();
			}
			
			if (con != null) {
				con.close();
			}
		}
	}
	
	public void setDatasource(DataSource datasource) {
		this.datasource = datasource;
	}
}
