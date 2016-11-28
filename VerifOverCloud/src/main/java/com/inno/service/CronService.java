package com.inno.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Properties;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.inno.util.ComUtil;
import com.inno.util.DateUtil;

/**
 *
 * 1. 패키지명 : com.inno.service 
 * 2. 타입명 : CronService.java 
 * 3. 작성일 : 2016. 10. 10. 오후 2:15:53 
 * 4. 작성자 : taegyu 
 * 5. 설명 :
  */
@Component
public class CronService {

	static final Logger log = LoggerFactory.getLogger(CronService.class);


	// 날짜관련
	Calendar now, startTime;
	int compareBefore, compareAfter; 
	Long diffMin = 0L;
	boolean systemFlag, serviceFlag, webFlag;
	Properties props = new Properties();
	private ClassPathResource classPathResource = new ClassPathResource("properties.xml");

	String executeResult = "";
	String printResult = "";
	ArrayList<String> cmdList = new ArrayList<String>();
	
	InputStream is; 
	int subPoint;
	StringTokenizer portToken;
	String tmpPort = "";
	
	@Scheduled(cron = "*/60 * * * * *")
	public void aJob() {

		//Initialize Falg
		systemFlag = false;
		serviceFlag = false;
		webFlag = false;
		compareBefore = 0;
		compareAfter = 0;
		
		try {
			props.loadFromXML(classPathResource.getInputStream());

			// 현재시간을 지속적으로 받아옴
			now = Calendar.getInstance();
			startTime = DateUtil.CalendarFromString(dateMerge("system", "start"));
			 
			// 시간차이 계산 분단위 이하를 버리기위해 현재시간에 (60*1000)를 나눔
			diffMin = (now.getTimeInMillis() / (60 * 1000)) -  startTime.getTimeInMillis() / (60 * 1000);
			log.info("min : " + diffMin);
			
			//시간비교(	처리시간이 1분 넘는 경우를 대비해 Flag값 먼저 생성) 
			compareBefore = DateUtil.CalendarFromString(dateMerge("system", "start")).compareTo(now);
			compareAfter = DateUtil.CalendarFromString(dateMerge("system", "end")).compareTo(now);
			if(props.getProperty("system.power").equals("on")){
				if(compareBefore <= 0 && compareAfter >= 0 && !props.getProperty("system.period").equals("0") && (diffMin%Integer.parseInt(props.getProperty("system.period")) == 0)) {
					systemFlag = true;
				}
			}
			
			compareBefore = DateUtil.CalendarFromString(dateMerge("service","start")).compareTo(now);
			compareAfter = DateUtil.CalendarFromString(dateMerge("service","end")).compareTo(now);
			if(props.getProperty("service.power").equals("on")){
				if(compareBefore <= 0 && compareAfter >= 0 && !props.getProperty("service.period").equals("0") && (diffMin%Integer.parseInt(props.getProperty("service.period")) == 0)) {
					serviceFlag = true;
				}
			}

			compareBefore = DateUtil.CalendarFromString(dateMerge("web","start")).compareTo(now);
			compareAfter = DateUtil.CalendarFromString(dateMerge("web","end")).compareTo(now);
			if(props.getProperty("web.power").equals("on")){
				if(compareBefore <= 0 && compareAfter >= 0 && !props.getProperty("web.period").equals("0") && (diffMin%Integer.parseInt(props.getProperty("web.period")) == 0)) {
					webFlag = true;
				}
			}

			log.info("systemFlag : " + systemFlag);
			log.info("serviceFlag : " + serviceFlag);
			log.info("webFlag : " + webFlag);

			if(systemFlag){
				verifySystem();
			}
			if(serviceFlag){
				verifyService();
			}
			if(webFlag){
				verifyWeb();
			}
			
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} 

	}
	
	
	public String dateMerge(String svc, String when){
		return props.getProperty(svc+"."+when+"Date") + " " + props.getProperty(svc+"."+when+"Hour") + ":" + props.getProperty(svc+"."+when+"Min");
	}
	
	
	public void verifySystem() {
		log.info("verifySystem() : start");
		
		// 검증 프로세스
		cmdList.clear();
		try{
			
			if (props.get("os").equals("linux")) {
				cmdList.add("/bin/bash");
				cmdList.add("-c");
				cmdList.add("ping -c 10 " + props.getProperty("system.targetIP"));
				executeResult = ComUtil.executeArr(cmdList, props.getProperty("os"));
				
				if(executeResult.indexOf("unknown") >= 0){
					log.warn("[System - Target: "+ props.getProperty("system.targetIP") +"] ERROR :\n" + executeResult);	//실패
				}
				else{
					subPoint = executeResult.lastIndexOf("\n", executeResult.indexOf("ping statistics"));
					log.warn("[System - Target: "+ props.getProperty("system.targetIP") +"] RESULT :\n" +  executeResult.substring(subPoint) + "\n"); //성공
				}
	
			} else {// Case of Windows
				cmdList.add("cmd");
				cmdList.add("/c ping -n 10 " + props.getProperty("system.targetIP"));
				executeResult = ComUtil.executeArr(cmdList, props.getProperty("os"));
				
				if(executeResult.indexOf("호스트를 찾을 수 없습니다") >=0 ){
					log.warn("[System - Target: "+ props.getProperty("system.targetIP") +"] ERROR :\n" + executeResult + "\n");	//실패
				}
				else{
					subPoint = executeResult.lastIndexOf("\n", executeResult.indexOf("통계"));
					log.warn("[System - Target: "+ props.getProperty("system.targetIP") +"] RESULT :\n" +  executeResult.substring(subPoint) + "\n"); //성공
				}
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		
		log.info("verifySystem() : end");
	}
	
	public void verifyService(){
		log.info("verifyService() : start");
		
		// 검증 프로세스
		portToken = new StringTokenizer(props.getProperty("service.targetPort"), ",");
		printResult ="";
		
		try{
			
			while(portToken.hasMoreTokens()){
				cmdList.clear();
				tmpPort = portToken.nextToken();
				
				if (props.get("os").equals("linux")) {
					cmdList.add("/bin/bash");
					cmdList.add("-c");
					cmdList.add("nc -v -w2 -z " + props.getProperty("service.targetIP") + " " + tmpPort);
				} else {
					cmdList.add("cmd");
					cmdList.add("/c D:\\temp\\nc111nt\\nc -v -w2 -z " + props.getProperty("service.targetIP") + " " + tmpPort);
				}
				executeResult = ComUtil.executeArr(cmdList, props.getProperty("os"));
				if(executeResult.indexOf("HOST_NOT_FOUND") >= 0 || executeResult.indexOf("service not known") >= 0){
					printResult += executeResult + "\n";
				}
				else{
					int startPoint = executeResult.lastIndexOf("\n",executeResult.lastIndexOf(" " + tmpPort + " "));
					if(startPoint > -1){
						executeResult = executeResult.substring(startPoint).trim();	
					}
					printResult += executeResult + "\n";
				}
				//리눅스는 마지막 한줄만 가져와도 됨
			}
			log.warn("[Service - Target: "+ props.getProperty("service.targetIP") +"] RESULT :\n" + printResult);	

		} catch(Exception e){
			e.printStackTrace();
		}

		log.info("verifyService() : end");
	}

	
	public void verifyWeb(){
		log.info("verifyWeb() : start");
		
		// 검증 프로세스
		cmdList.clear();

		try {
			
			if (props.get("os").equals("linux")) {
				cmdList.add("/bin/bash");
				cmdList.add("-c");
				cmdList.add("curl -s --head " + props.getProperty("web.targetIP"));
			} else {
				cmdList.add("cmd");
				cmdList.add("/c D:\\temp\\I386\\curl.exe -s --head " + props.getProperty("web.targetIP"));
			}
			executeResult = ComUtil.executeArr(cmdList, props.getProperty("os"));
			
			if(executeResult.indexOf("HTTP/1.1") < 0){
				log.warn("[Web - Target: "+ props.getProperty("web.targetIP") +"] ERROR :\n" + executeResult + "\n");	//실패
			}
			else{
				log.warn("[Web - Target: "+ props.getProperty("web.targetIP") +"] RESULT :\n" +  executeResult + "\n"); //성공
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		log.info("verifyWeb() : end");
	}

}
