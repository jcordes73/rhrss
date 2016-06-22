package com.redhat.iot.rhrss.dc.camel;

import org.apache.camel.Exchange;

public class RouteToGateway {

	public String routeTo(Exchange exchange){
		String location = exchange.getIn().getHeader("location", java.lang.String.class);
		
		return "activemq:queue:dc." + location + ".out";
	}
}
