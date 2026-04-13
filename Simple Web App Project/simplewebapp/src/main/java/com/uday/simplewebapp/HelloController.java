package com.uday.simplewebapp;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
	
	@GetMapping("/")
	public String greet() {
		return "Welcome to Grocito";
	}
	
	@GetMapping("/info")
	public String aboutMachine() {
		//this function will return a machine info
		//so when request will go to /info then it should return machine config
		
		String os = System.getProperty("os.name");
        String version = System.getProperty("os.version");
        String architecture = System.getProperty("os.arch");
        String javaVersion = System.getProperty("java.version");
        String user= System.getProperty("user.name");

        return "OS: "+os+",  Version: "+version+",  Architechture: "+architecture+",  Java Version: "+javaVersion+",  User: "+user;
		
	}
}
