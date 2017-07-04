package org.larics.jetshop.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.larics.jetshop.controller.helper.JetShopMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;

/*
 * Provides custom error page (instead of white label error page).
 * 
 * @author Igor Laryukhin
 */
@Controller
public class JetErrorController implements ErrorController {

	private static final Logger logger = LoggerFactory.
			getLogger(JetErrorController.class);

	private static final String PATH = "/error";

	private static final String CODE_ERROR_403 = "error.403";
	private static final String CODE_ERROR_404 = "error.404";
	private static final String CODE_ERROR_ANY = "error.any";

	private final MessageSource messageSource;
	private ErrorAttributes errorAttributes;

	@Autowired
	public JetErrorController(MessageSource messageSource,
			ErrorAttributes errorAttributes) {
		this.messageSource = messageSource;
		this.errorAttributes = errorAttributes;
	}

	@GetMapping(value = PATH)
	public String getError(HttpServletRequest request,
			@ModelAttribute("messages") JetShopMessages messages,
			Locale locale) {

		String message = null;
		int statusCode = (int) request
				.getAttribute("javax.servlet.error.status_code");
		switch (statusCode) {
			case 403:
				message = messageSource.getMessage(CODE_ERROR_403,
						null, locale);
				break;
			case 404:
				message = messageSource.getMessage(CODE_ERROR_404,
						null, locale);
				break;
			default:
				message = messageSource.getMessage(CODE_ERROR_ANY,
						null, locale);
				break;
		}
		messages.addErrorMessage(message);

		// Not intended for the user, so, i18n is not used
		messages.addDebugMessage("Status code: " +
			request.getAttribute("javax.servlet.error.status_code"));
		messages.addDebugMessage("URL: " +
			request.getAttribute("javax.servlet.forward.request_uri"));

		RequestAttributes requestAttributes = new ServletRequestAttributes(
				request);
		logger.error("Error occurred, error attributes: {}",
				errorAttributes.getErrorAttributes(requestAttributes, true));

		return "errors/error";
	}

	@Override
	public String getErrorPath() {
		return PATH;
	}

}
