package com.boy.sso.example.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class Test {
	@RequestMapping("/index")
	public String index(HttpServletRequest request,ModelMap map){
		map.addAttribute("username", request.getAttribute("username"));
		return "index";
	}
}
