package org.larics.jetshop.controller.helper;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/*
 * Adds {@link JetShopMessages} to UI model.
 * 
 * @author Igor Laryukhin
 */
@ControllerAdvice(basePackages = { "org.larics.jetshop.controller" })
public class GlobalControllerAdvice {

	private static final Logger logger = LoggerFactory.
			getLogger(GlobalControllerAdvice.class);

	private final String jetshopmessagesLevel;

	@Autowired
	public GlobalControllerAdvice(
			@Value("${jetshopmessages.level}") String jetshopmessagesLevel) {

		this.jetshopmessagesLevel = jetshopmessagesLevel;
	}

	@ModelAttribute
	public void addMessages(HttpSession session, Model model) {

		JetShopMessages messages = null;
		Object object = null;

		if ((object = session.getAttribute("messages")) != null) {

			// JetShopMessages object (with default jetshopmessagesLevel)
			// was put into Session by AuthenticationSuccessHandlerImpl
			messages = (JetShopMessages) object;
			messages.setLevel(jetshopmessagesLevel);

			logger.debug("JetShopMessages object found in Session,"
					+ " JetShopMessages = {}", messages);
			logger.debug("Session = {}", session);

			model.addAttribute("messages", messages);
			session.removeAttribute("messages");

		} else if ((object = model.asMap().get("messages")) != null) {

			// JetShopMessages object was put into Model
			// via RedirectAttributes
			messages = (JetShopMessages) object;

			logger.debug("JetShopMessages object found in Model,"
					+ " JetShopMessages = {}", messages);
			logger.debug("Model = {}", model.asMap());

		} else {

			// Creating new JetShopMessages
			messages = new JetShopMessages(jetshopmessagesLevel);

			logger.debug("New JetShopMessages object created: {}", messages);

			model.addAttribute("messages", messages);
		}

	}

}