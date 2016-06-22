package com.redhat.iot.rhrss.dc.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

public class InitDatabase {

	private static Logger log = Logger.getLogger(InitDatabase.class);
	
	private DataSource datasource = null;
	
	public InitDatabase(DataSource datasource) throws SQLException {
		this.datasource = datasource;
		
		initDatabase();
		
		log.info("Created reservation database");
	}
	
	private void initDatabase() throws SQLException{
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			con = datasource.getConnection();
						
			try {
				stmt = con.prepareStatement("DROP TABLE reservations");
				stmt.execute();
				stmt.close();
			} catch (Exception e) {
			}
			
			stmt = con.prepareStatement("CREATE TABLE reservations (location varchar(255), room varchar(255), reservation_start bigint, reservation_end bigint, reserved int)");
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
}
