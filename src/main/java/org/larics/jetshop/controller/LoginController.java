package org.larics.jetshop.controller;

import org.larics.jetshop.controller.helper.JetShopMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

/*
 * Provides login page.
 * 
 * @author Igor Laryukhin
 */
@Controller
public class LoginController {

	private static final Logger logger = LoggerFactory.
			getLogger(LoginController.class);

	// Not authenticated only
	@GetMapping(value = "/login")
	public String login(
			Model model, 
			// "error" is set in SecurityConfig: failureUrl("/login?error")
			String error,
			@ModelAttribute("messages") JetShopMessages messages) {

		if (error != null) {
			logger.debug("Invalid username or password");
			messages.
				addErrorMessage("Invalid username or password, try again.");
		}

		return "login/login";
	}

}
