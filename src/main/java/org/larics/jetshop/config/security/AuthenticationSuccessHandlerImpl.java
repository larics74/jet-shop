package org.larics.jetshop.config.security;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.larics.jetshop.controller.helper.JetShopMessages;
import org.larics.jetshop.model.CurrentUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;

/*
 * Sets some parameters upon successful authentication.
 * 
 * @author Igor Laryukhin
 */
@Component
public class AuthenticationSuccessHandlerImpl
		implements AuthenticationSuccessHandler {

	private static final Logger logger = LoggerFactory.
			getLogger(AuthenticationSuccessHandlerImpl.class);

	private static final String CODE_USER_LOGGED_IN = "user.logged.in";

	private final MessageSource messageSource;
	private final LocaleResolver localeResolver;

	@Autowired
	public AuthenticationSuccessHandlerImpl(MessageSource messageSource,
			LocaleResolver localeResolver) {
		this.messageSource = messageSource;
		this.localeResolver = localeResolver;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request,
			HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {

		// Updating user id in the session (to show Profile href).
		CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
		HttpSession session = request.getSession();
		session.setAttribute("currentUserId", currentUser.getId());
		logger.debug("AuthenticationSuccessHandler: currentUser added: {}",
				currentUser);

		// Retrieving target url from the session,
		// where Spring have saved it.
		String url = "/";
		SavedRequest savedRequest = (SavedRequest) session.
				getAttribute("SPRING_SECURITY_SAVED_REQUEST");
		if (savedRequest != null) {
			url = savedRequest.getRedirectUrl();
			logger.debug("savedRequest.getRedirectUrl() = {}",
					savedRequest.getRedirectUrl());
		} else {
			logger.debug("savedRequest = NULL");
		}
		response.sendRedirect(url);

		// Adding message to JetShopMessages,
		// adding JetShopMessages to session,
		// it will be retrieved by GlobalControllerAdvice.
		JetShopMessages messages = new JetShopMessages();
		Locale locale = localeResolver.resolveLocale(request);
		messages.addInfoMessage(messageSource.getMessage(CODE_USER_LOGGED_IN,
				null, locale));
		session.setAttribute("messages", messages);
		logger.debug("New JetShopMessages object added to Session: {}",
				messages);
	}

}
