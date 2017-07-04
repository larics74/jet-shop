package org.larics.jetshop.controller;

import java.util.List;
import java.util.Locale;

import org.larics.jetshop.controller.helper.JetShopMessages;
import org.larics.jetshop.model.JetModel;
import org.larics.jetshop.model.JetOrder;
import org.larics.jetshop.model.Role;
import org.larics.jetshop.model.User;
import org.larics.jetshop.service.JetModelService;
import org.larics.jetshop.service.JetOrderService;
import org.larics.jetshop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

/*
 * For development/presentation.
 * Provides view, which represents overall UI design in one page.
 * 
 * @author Igor Laryukhin
 */
@Controller
public class DesignController {

	private static final String CODE_SAMPLE_ERROR = "sample.error";
	private static final String CODE_SAMPLE_INFO = "sample.info";

	// ad data
	private final String latestJetModelName;
	private final UserService userService;
	private final JetModelService jetModelService;
	private final JetOrderService jetOrderService;

	private final MessageSource messageSource;

	@Autowired
	public DesignController(
			// injecting parameter from application.properties
			@Value("${ad.latestJetModelName}") String latestJetModelName,
			JetModelService jetModelService,
			UserService userService,
			JetOrderService jetOrderService,
			MessageSource messageSource) {

		this.latestJetModelName = latestJetModelName;
		this.userService = userService;
		this.jetModelService = jetModelService;
		this.jetOrderService = jetOrderService;
		this.messageSource = messageSource;
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@GetMapping("/design")
	public String getDesign(
			Model model,
			@ModelAttribute("messages") JetShopMessages messages,
			Locale locale) {

		model.addAttribute("design", true);

		messages.addErrorMessage(messageSource.getMessage(CODE_SAMPLE_ERROR,
				null, locale));
		messages.addInfoMessage(messageSource.getMessage(CODE_SAMPLE_INFO,
				null, locale));
		messages.addDebugMessage("Sample debug message");

		// Home
		// ad data
		Long latestJetModelId = jetModelService.getIdByName(latestJetModelName);
		model.addAttribute("latestJetModelName", latestJetModelName);
		model.addAttribute("latestJetModelId", latestJetModelId);

		// Users
		User user = userService.getById(1L);
		model.addAttribute("user", user);

		model.addAttribute("action", "/users");
		model.addAttribute("headTitle", "title.user.details");
		model.addAttribute("roles", Role.values());

		model.addAttribute("help", true);

		List<User> users = userService.getAll();
		model.addAttribute("users", users);

		// JetModels
		JetModel jetModel = jetModelService.getById(3L);
		model.addAttribute("jetModel", jetModel);

		List<JetModel> jetModels = jetModelService.getAll();
		model.addAttribute("jetModels", jetModels);

		// JetOrders
		JetOrder jetOrder = jetOrderService.getById(1L);
		model.addAttribute("jetOrder", jetOrder);

		List<User> customers = userService.getCustomers();
		model.addAttribute("customers", customers);

		List<JetOrder> jetOrders = jetOrderService.getAll();
		model.addAttribute("jetOrders", jetOrders);

		return "common/design";
	}

}
