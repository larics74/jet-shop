package org.larics.jetshop.controller;

import java.util.Locale;

import javax.servlet.http.HttpSession;

import org.larics.jetshop.controller.helper.JetShopMessages;
import org.larics.jetshop.service.JetModelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

/*
 * Provides home page.
 * 
 * @author Igor Laryukhin
 */
@Controller
public class HomeController {

	private static final Logger logger = LoggerFactory.
			getLogger(HomeController.class);

	private static final String CODE_USER_LOGGED_OUT = "user.logged.out";

	// Ad data.
	private final String latestJetModelName;
	private final JetModelService jetModelService;

	private final MessageSource messageSource;

	@Autowired
	public HomeController(
			// Injecting parameter from application.properties.
			@Value("${ad.latestJetModelName}") String latestJetModelName,
			JetModelService jetModelService,
			MessageSource messageSource) {

		this.latestJetModelName = latestJetModelName;
		this.jetModelService = jetModelService;
		this.messageSource = messageSource;
	}

	@GetMapping("/")
	public String getHome(Model model,
			// Parameters login and logout are set in SecurityConfig.
			// TODO: to delete
//			String login,
			String logout,
			@ModelAttribute("messages") JetShopMessages messages,
			Locale locale,
			HttpSession session) {

		// If user logged out,
		// remove user id from the session
		// (it was added in org.larics.jetshop.config.
		// 		AuthenticationSuccessHandlerImpl)
		if (logout != null) {

			session.removeAttribute("currentUserId");

			messages.addInfoMessage(
					messageSource.getMessage(CODE_USER_LOGGED_OUT,
					null, locale));
			logger.debug("User has been logged out");
		}

		// Ad data.
		Long latestJetModelId = jetModelService.getIdByName(latestJetModelName);
		model.addAttribute("latestJetModelName", latestJetModelName);
		model.addAttribute("latestJetModelId", latestJetModelId);

		return "index";
	}

}
