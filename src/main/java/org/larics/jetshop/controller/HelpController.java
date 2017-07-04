package org.larics.jetshop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/*
 * Provides help page.
 * 
 * @author Igor Laryukhin
 */
@Controller
public class HelpController {

	@GetMapping("/help")
	public String getHelp() {
		return "help/help";
	}

}
