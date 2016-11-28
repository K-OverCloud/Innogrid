package com.inno.vo;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
*
* 1. 패키지명 : com.inno.vo
* 2. 타입명 : VerificationVO.java
* 3. 작성일 : 2016. 10. 14. 오후 6:37:28
* 4. 작성자 : taegyu
* 5. 설명 : 
*
*/
public class VerificationVO {

	private String os; 
	private String targetAction;
	private String targetService;
	
	private String systemPower; 
	private String systemPeriod;  	//분단위
	private String systemStartDate;
	private String systemEndDate;
	private String systemTargetIP;
	private String servicePower; 
	private String servicePeriod;
	private String serviceStartDate;
	private String serviceEndDate;
	private String serviceTargetIP;
	private String serviceTargetPort;
	
	private String webPower; 
	private String webPeriod;
	private String webStartDate;
	private String webEndDate;
	private String webTargetIP;

	private String systemStartHour;
	private String systemStartMin;
	private String systemEndHour;
	private String systemEndMin;
		
	private String serviceStartHour;
	private String serviceStartMin;
	private String serviceEndHour;
	private String serviceEndMin;
	
	private String webStartHour;
	private String webStartMin;
	private String webEndHour;
	private String webEndMin;
	
	
	public String getOs() {
		return os;
	}
		
	public String getTargetAction() {
		return targetAction;
	}


	public void setTargetAction(String targetAction) {
		this.targetAction = targetAction;
	}


	public String getTargetService() {
		return targetService;
	}


	public void setTargetService(String targetService) {
		this.targetService = targetService;
	}


	public String getSystemPower() {
		return systemPower;
	}


	public void setSystemPower(String systemPower) {
		this.systemPower = systemPower;
	}


	public String getServicePower() {
		return servicePower;
	}


	public void setServicePower(String servicePower) {
		this.servicePower = servicePower;
	}


	public String getWebPower() {
		return webPower;
	}


	public void setWebPower(String webPower) {
		this.webPower = webPower;
	}


	public void setOs(String os) {
		this.os = os;
	}
	public String getSystemPeriod() {
		return systemPeriod;
	}
	public void setSystemPeriod(String systemPeriod) {
		this.systemPeriod = systemPeriod;
	}
	public String getSystemStartDate() {
		return systemStartDate;
	}
	public void setSystemStartDate(String systemStartDate) {
		this.systemStartDate = systemStartDate;
	}
	public String getSystemEndDate() {
		return systemEndDate;
	}
	public void setSystemEndDate(String systemEndDate) {
		this.systemEndDate = systemEndDate;
	}
	public String getSystemTargetIP() {
		return systemTargetIP;
	}
	public void setSystemTargetIP(String systemTargetIP) {
		this.systemTargetIP = systemTargetIP;
	}
	public String getServicePeriod() {
		return servicePeriod;
	}
	public void setServicePeriod(String servicePeriod) {
		this.servicePeriod = servicePeriod;
	}
	public String getServiceStartDate() {
		return serviceStartDate;
	}
	public void setServiceStartDate(String serviceStartDate) {
		this.serviceStartDate = serviceStartDate;
	}
	public String getServiceEndDate() {
		return serviceEndDate;
	}
	public void setServiceEndDate(String serviceEndDate) {
		this.serviceEndDate = serviceEndDate;
	}
	public String getServiceTargetIP() {
		return serviceTargetIP;
	}
	public void setServiceTargetIP(String serviceTargetIP) {
		this.serviceTargetIP = serviceTargetIP;
	}
	
	public String getServiceTargetPort() {
		return serviceTargetPort;
	}

	public void setServiceTargetPort(String serviceTargetPort) {
		this.serviceTargetPort = serviceTargetPort;
	}

	public String getWebPeriod() {
		return webPeriod;
	}
	public void setWebPeriod(String webPeriod) {
		this.webPeriod = webPeriod;
	}
	public String getWebStartDate() {
		return webStartDate;
	}
	public void setWebStartDate(String webStartDate) {
		this.webStartDate = webStartDate;
	}
	public String getWebEndDate() {
		return webEndDate;
	}
	public void setWebEndDate(String webEndDate) {
		this.webEndDate = webEndDate;
	}
	public String getWebTargetIP() {
		return webTargetIP;
	}
	public void setWebTargetIP(String webTargetIP) {
		this.webTargetIP = webTargetIP;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public String getSystemStartHour() {
		return systemStartHour;
	}

	public void setSystemStartHour(String systemStartHour) {
		this.systemStartHour = systemStartHour;
	}

	public String getSystemStartMin() {
		return systemStartMin;
	}

	public void setSystemStartMin(String systemStartMin) {
		this.systemStartMin = systemStartMin;
	}

	public String getSystemEndHour() {
		return systemEndHour;
	}

	public void setSystemEndHour(String systemEndHour) {
		this.systemEndHour = systemEndHour;
	}

	public String getSystemEndMin() {
		return systemEndMin;
	}

	public void setSystemEndMin(String systemEndMin) {
		this.systemEndMin = systemEndMin;
	}

	public String getServiceStartHour() {
		return serviceStartHour;
	}

	public void setServiceStartHour(String serviceStartHour) {
		this.serviceStartHour = serviceStartHour;
	}

	public String getServiceStartMin() {
		return serviceStartMin;
	}

	public void setServiceStartMin(String serviceStartMin) {
		this.serviceStartMin = serviceStartMin;
	}

	public String getServiceEndHour() {
		return serviceEndHour;
	}

	public void setServiceEndHour(String serviceEndHour) {
		this.serviceEndHour = serviceEndHour;
	}

	public String getServiceEndMin() {
		return serviceEndMin;
	}

	public void setServiceEndMin(String serviceEndMin) {
		this.serviceEndMin = serviceEndMin;
	}

	public String getWebStartHour() {
		return webStartHour;
	}

	public void setWebStartHour(String webStartHour) {
		this.webStartHour = webStartHour;
	}

	public String getWebStartMin() {
		return webStartMin;
	}

	public void setWebStartMin(String webStartMin) {
		this.webStartMin = webStartMin;
	}

	public String getWebEndHour() {
		return webEndHour;
	}

	public void setWebEndHour(String webEndHour) {
		this.webEndHour = webEndHour;
	}

	public String getWebEndMin() {
		return webEndMin;
	}

	public void setWebEndMin(String webEndMin) {
		this.webEndMin = webEndMin;
	}

}
