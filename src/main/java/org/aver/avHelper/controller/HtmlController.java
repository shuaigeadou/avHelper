package org.aver.avHelper.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/avHelper")
public class HtmlController {

	@RequestMapping
	public String index() {
		return "manager";
	}

}
