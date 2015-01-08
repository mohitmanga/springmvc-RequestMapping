package com.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Sample Controller used to display the functionality
 * @author mohitmanga@gmail.com
 *
 */
@Controller("sample")
public class SampleController {

	
	@RequestMapping(value ="samplemethodone", produces ="application/json", method = RequestMethod.POST)
	public void sampleMethod1(String text){
		// NOP
	}
	
	@RequestMapping(value ="samplemethodtwo/{text}", produces ="application/json", method = RequestMethod.GET)
	public void sampleMethod2(@PathVariable("text") String text){
		System.out.println("I m here");
		// NOP
	}
}
