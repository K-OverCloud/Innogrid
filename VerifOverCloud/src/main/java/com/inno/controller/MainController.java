package com.inno.controller;

import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.inno.vo.VerificationVO;

@Controller
public class MainController {

	static final Logger log = LoggerFactory.getLogger(MainController.class);
	private Properties props = new Properties();
	private ClassPathResource classPathResource = new ClassPathResource("properties.xml");
	
	FileOutputStream fos;
	
	@RequestMapping(value ="/")
	public String index() throws Exception {
//		PropertyUtil propertyUtil = PropertyUtil.getInstance(resourcePath);
		log.info("This is INFO Log!");
		
		return "redirect:/main.do";
	}
	
	@RequestMapping(value ="/index.do")
	public String index2() throws Exception {
		
		return "index";
	}

	@RequestMapping(value ="/main.do")
	public String test(@ModelAttribute("param") VerificationVO param, Model model) throws Exception {
		props.loadFromXML(classPathResource.getInputStream());
		
		param = getProperties(props);
		
		model.addAttribute("param", param);
		
		return "main";
	}
	
	@RequestMapping(value = "/operation/saveSchedules", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> saveSchedules(@ModelAttribute("param") VerificationVO param) throws Exception {
		int ret = 1;
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		props.loadFromXML(classPathResource.getInputStream());
		
		log.info("saveSchedules Start");
		
		if(param.getTargetService().equals("system")){
			props.setProperty("system.targetIP", param.getSystemTargetIP());
			props.setProperty("system.period", param.getSystemPeriod());
			props.setProperty("system.startDate", param.getSystemStartDate());
			props.setProperty("system.startHour", param.getSystemStartHour());
			props.setProperty("system.startMin", param.getSystemStartMin());
			props.setProperty("system.endDate", param.getSystemEndDate());
			props.setProperty("system.endHour", param.getSystemEndHour());
			props.setProperty("system.endMin", param.getSystemEndMin());
		}
		else if(param.getTargetService().equals("service")){
			props.setProperty("service.targetIP", param.getServiceTargetIP());
			props.setProperty("service.targetPort", param.getServiceTargetPort());
			props.setProperty("service.period", param.getServicePeriod());
			props.setProperty("service.startDate", param.getServiceStartDate());
			props.setProperty("service.startHour", param.getServiceStartHour());
			props.setProperty("service.startMin", param.getServiceStartMin());
			props.setProperty("service.endDate", param.getServiceEndDate());
			props.setProperty("service.endHour", param.getServiceEndHour());
			props.setProperty("service.endMin", param.getServiceEndMin());
		}
		else if(param.getTargetService().equals("web")){
			props.setProperty("web.targetIP", param.getWebTargetIP());
			props.setProperty("web.period", param.getWebPeriod());
			props.setProperty("web.startDate", param.getWebStartDate());
			props.setProperty("web.startHour", param.getWebStartHour());
			props.setProperty("web.startMin", param.getWebStartMin());
			props.setProperty("web.endDate", param.getWebEndDate());
			props.setProperty("web.endHour", param.getWebEndHour());
			props.setProperty("web.endMin", param.getWebEndMin());
		}
		
		fos = new FileOutputStream(classPathResource.getFile());
		props.storeToXML(fos, "Verification Info");

		if( ret > 0 ){
			resultMap.put("result", true);
		} else {
			resultMap.put("result", false);
		}

		log.info("saveSchedules End");

		return resultMap; 
	}
		
	
	@RequestMapping(value = "/operation/jobsAjaxAction", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> jobsAction(@ModelAttribute("param") VerificationVO param) throws Exception {
		log.info("jobsAjaxAction");
		int ret = 1;
		Map<String, Object> resultMap = new HashMap<String, Object>();
		props.loadFromXML(classPathResource.getInputStream());
		
		String tService = param.getTargetService();
		String tAction = param.getTargetAction();
		
		if(tService.equals("system")){
			props.setProperty("system.power", tAction);
		}
		else if(tService.equals("service")){
			props.setProperty("service.power", tAction);
		}
		else if(tService.equals("web")){
			props.setProperty("web.power", tAction);
		}
		
		fos = new FileOutputStream(classPathResource.getFile());
		props.storeToXML(fos, "Verification Info");
		
		if( ret > 0 ){
			resultMap.put("result", true);
		} else {
			resultMap.put("result", false);
		}
		
		return resultMap;

	}
		
	public VerificationVO getProperties(Properties props){
		
		VerificationVO tmpVo = new VerificationVO(); 
		tmpVo.setOs(props.getProperty("os"));
		
		tmpVo.setSystemPower(props.getProperty("system.power"));
		tmpVo.setSystemPeriod(props.getProperty("system.period"));
		tmpVo.setSystemStartDate(props.getProperty("system.startDate"));
		tmpVo.setSystemStartHour(props.getProperty("system.startHour"));
		tmpVo.setSystemStartMin(props.getProperty("system.startMin"));
		tmpVo.setSystemEndDate(props.getProperty("system.endDate"));
		tmpVo.setSystemEndHour(props.getProperty("system.endHour"));
		tmpVo.setSystemEndMin(props.getProperty("system.endMin"));
		tmpVo.setSystemTargetIP(props.getProperty("system.targetIP"));
     	
		tmpVo.setServicePower(props.getProperty("service.power"));
		tmpVo.setServicePeriod(props.getProperty("service.period"));
		tmpVo.setServiceStartDate(props.getProperty("service.startDate"));
		tmpVo.setServiceStartHour(props.getProperty("service.startHour"));
		tmpVo.setServiceStartMin(props.getProperty("service.startMin"));
		tmpVo.setServiceEndDate(props.getProperty("service.endDate"));
		tmpVo.setServiceEndHour(props.getProperty("service.endHour"));
		tmpVo.setServiceEndMin(props.getProperty("service.endMin"));
		tmpVo.setServiceTargetIP(props.getProperty("service.targetIP"));
		tmpVo.setServiceTargetPort(props.getProperty("service.targetPort"));
     	
		tmpVo.setWebPower(props.getProperty("web.power"));
		tmpVo.setWebPeriod(props.getProperty("web.period"));
		tmpVo.setWebStartDate(props.getProperty("web.startDate"));
		tmpVo.setWebStartHour(props.getProperty("web.startHour"));
		tmpVo.setWebStartMin(props.getProperty("web.startMin"));
		tmpVo.setWebEndDate(props.getProperty("web.endDate"));
		tmpVo.setWebEndHour(props.getProperty("web.endHour"));
		tmpVo.setWebEndMin(props.getProperty("web.endMin"));
		tmpVo.setWebTargetIP(props.getProperty("web.targetIP"));

		log.info(tmpVo.toString());
		return tmpVo;
	}
}
