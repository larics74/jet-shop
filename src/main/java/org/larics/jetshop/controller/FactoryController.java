package org.larics.jetshop.controller;

import java.util.Locale;

import org.larics.jetshop.controller.helper.JetShopMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

/*
 * Provides factory schedule page.
 * 
 * @author Igor Laryukhin
 */
@Controller
public class FactoryController {

	private static final String CODE_UNDER_CONSTRUCTION = "under.construction";
	private static final String CODE_FUTURE_FACTORY = "future.factory";

	private final MessageSource messageSource;

	@Autowired
	public FactoryController(MessageSource messageSource) {

		this.messageSource = messageSource;
	}

	@GetMapping("/factory")
	public String getHome(
			@ModelAttribute("messages") JetShopMessages messages,
			Locale locale) {

		messages.addInfoMessage(
				messageSource.getMessage(CODE_UNDER_CONSTRUCTION,
				null, locale));
		messages.addInfoMessage(
				messageSource.getMessage(CODE_FUTURE_FACTORY,
				null, locale));

		return "factory/schedule";
	}

}
