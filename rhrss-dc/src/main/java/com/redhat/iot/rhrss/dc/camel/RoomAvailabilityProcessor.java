package com.redhat.iot.rhrss.dc.camel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;

import javax.sql.DataSource;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;

public class RoomAvailabilityProcessor implements Processor {

	private static Logger log = Logger.getLogger(RoomAvailabilityProcessor.class);
	
	private static final String dataFormat = "yyyy-MM-dd HH:mm";
	
	private DataSource datasource = null;
	private SimpleDateFormat simpleDateFormat = null;
	
	public RoomAvailabilityProcessor() {
		simpleDateFormat = new SimpleDateFormat(dataFormat);
	}
	
	@Override
	public void process(Exchange exchange) throws Exception {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
			
			String location = exchange.getIn().getHeader("location", java.lang.String.class);
			String room = exchange.getIn().getHeader("room", java.lang.String.class);
			int motion = exchange.getIn().getHeader("motion", java.lang.Integer.class);
			
			log.info("Searching for reservation in location " + location + " for room " +room);
			
			con = datasource.getConnection();
							
			long currentTime = System.currentTimeMillis();
			
			stmt = con.prepareStatement("SELECT reservation_start, reservation_end, reserved FROM reservations WHERE location = ? AND room = ? AND reservation_start <= ? AND  reservation_end >= ?");
			stmt.setString(1, location);
			stmt.setString(2, room);
			stmt.setLong(3, currentTime);
			stmt.setLong(4, currentTime);			
			rs = stmt.executeQuery();
	
			int reserved = 0;
			
			if (rs.next()) {
				long reservationStart = rs.getLong(1);
				long reservationEnd = rs.getLong(2);
				reserved = rs.getInt(3);
				
				rs.close();
				stmt.close();
				
				exchange.getIn().setHeader("reservationStart", simpleDateFormat.format(reservationStart));
				exchange.getIn().setHeader("reservationEnd", simpleDateFormat.format(reservationEnd));
				
				log.info("Found reservation in location " + location + " for room " + room + " reservation-start " + reservationStart + " reservation-end " + reservationEnd);
				
				// There is a reservation but no motion, so free up the room
				if (motion == 0) {
					stmt = con.prepareStatement("UPDATE reservations SET reserved = 0 WHERE location = ? AND room = ? AND reservation_start = ? AND  reservation_end = ?");
					stmt.setString(1, location);
					stmt.setString(2, room);
					stmt.setLong(3, reservationStart);
					stmt.setLong(4, reservationEnd);			
					stmt.execute();
					stmt.close();
					
					reserved = 0;
				}
			}
			
			exchange.getIn().setHeader("reserved", reserved);			
		} catch (Exception e) {
			log.error("Error" + e.getMessage(), e);
			
			if (rs != null) {
				rs.close();
			}
			
			if (stmt != null) {
				stmt.close();
			}
			
			if (con != null) {
				con.close();
			}
		} finally {
			if (rs != null) {
				rs.close();
			}
			
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
