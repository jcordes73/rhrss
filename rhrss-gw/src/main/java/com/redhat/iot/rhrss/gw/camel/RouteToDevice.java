package com.redhat.iot.rhrss.gw.camel;

import org.apache.camel.Exchange;

public class RouteToDevice {

	public String routeTo(Exchange exchange){
		String roomId = (String)exchange.getProperty("room-id");
		
		return "mqtt:gw-out-"+ roomId + "?clientId=gw-out&publishTopicName=" + roomId;
	}
}
