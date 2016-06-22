package com.redhat.iot.rhrss.gw.camel;

import org.apache.camel.Exchange;

public class RouteToDevice {

	public String routeTo(Exchange exchange){
		String roomId = exchange.getIn().getHeader("room", java.lang.String.class);
		
		return "mqtt:gw-out-"+ roomId + "?clientId=gw-out-"+ roomId + "&publishTopicName=" + roomId;
	}
}
