package com.example.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.PathMatcher;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * Purpose of this class is to return information regarding the mapping url's
 * done on Spring Controllers/Method. This will be helpful in searching the Java
 * Classes corresponding to the URL to be provided in the Query Parameter
 * 
 * @author Mohit Manga
 * 
 */
@Controller
@RequestMapping(value = "/mappingInfo")
public class RequestMappingController {

	
	@Autowired
	private RequestMappingHandlerMapping handlerMapping;

	
	/**
	 * Function will be called whenever user wants all the mapping information
	 * managed by the spring context. Will return list of all the mapping 
	 * information
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value = "/allMappings", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody List<MappingInformationDTO> getAllMappingInfo(ModelMap modelMap) {
		List<MappingInformationDTO> responseList = new ArrayList<MappingInformationDTO>();
		Map<RequestMappingInfo, HandlerMethod> map = handlerMapping.getHandlerMethods();
		for (Entry<RequestMappingInfo, HandlerMethod> entry : map.entrySet()) {
			RequestMappingInfo mappingInfo = entry.getKey();
			HandlerMethod handlerMethod = entry.getValue();
			responseList.add(new MappingInformationDTO(handlerMethod.getBean(),handlerMethod.getMethod().getDeclaringClass(), 
								handlerMethod.getMethod().getName(), mappingInfo.getPatternsCondition().getPatterns()));
		}
		return responseList;
	}
	
	/**
	 * Function will be called whenever user searches for an particular mapping.
	 * Response will contain all the mapping's that match's the path searched for.
	 * Please note that this will not take into consideration criteria's such as 
	 * http method type, header's etc.
	 * @param path  Requested Path
	 * @param modelMap model map
	 * @return List of Mapping's
	 */
	@RequestMapping(value = "/singleMapping", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody List<MappingInformationDTO> getSingleMappingInfo(@RequestParam("path") String path, ModelMap modelMap) {
		List<MappingInformationDTO> responseList = new ArrayList<MappingInformationDTO>();
		Map<RequestMappingInfo, HandlerMethod> map = handlerMapping.getHandlerMethods();
		for (Entry<RequestMappingInfo, HandlerMethod> entry : map.entrySet()) {
			RequestMappingInfo mappingInfo = entry.getKey();
			HandlerMethod handlerMethod = entry.getValue();
			if(handlerMapping.getPathMatcher() != null){
				PathMatcher pathMatcher = handlerMapping.getPathMatcher();
				for(String singlePath : mappingInfo.getPatternsCondition().getPatterns()){
					/**
					 * Find the closest match
					 */
					if(pathMatcher.match(singlePath, path) || pathMatcher.matchStart(singlePath, path)){
						responseList.add(new MappingInformationDTO(handlerMethod.getBean(),handlerMethod.getMethod().getDeclaringClass(), 
								handlerMethod.getMethod().getName(), mappingInfo.getPatternsCondition().getPatterns()));
					}
				}
			}else{
				if (mappingInfo.getPatternsCondition().getPatterns().contains(path)) {
					responseList.add(new MappingInformationDTO(handlerMethod.getBean(),handlerMethod.getMethod().getDeclaringClass(), 
							handlerMethod.getMethod().getName(), mappingInfo.getPatternsCondition().getPatterns()));
					break;
				}
			}
		}
		return responseList;
	}
	
	/**
	 * Inner Class, This DTO is passed to the front end with all the relevant information
	 * regarding the Request Mapping
	 * @author Mohit Manga
	 *
	 */
	@SuppressWarnings("unused")
	@JsonIgnoreProperties(ignoreUnknown = true)
	@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
	private class MappingInformationDTO{
		@JsonProperty("BeanName")
		private Object beanName;
		@JsonProperty("DeclaringClass")
		private Object declaringClass;
		@JsonProperty("MethodName")
		private Object methodName;
		@JsonProperty("AccessURL")
		private Object accessURL;
		
		public MappingInformationDTO(){
			
		}
		
		public MappingInformationDTO(Object beanName, Object declaringClass, Object methodName, Object accessURL) {
			super();
			this.beanName = beanName;
			this.declaringClass = declaringClass;
			this.methodName = methodName;
			this.accessURL = accessURL;
		}

		@Override
		public String toString() {
			return "MappingInformationDTO [beanName=" + beanName
					+ ", declaringClass=" + declaringClass + ", methodName="
					+ methodName + ", accessURL=" + accessURL + "]";
		}
	}
}
