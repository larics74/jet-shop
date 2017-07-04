package org.larics.jetshop.controller;

import java.util.List;
import java.util.Locale;

import javax.validation.Valid;

import org.larics.jetshop.controller.helper.JetShopMessages;
import org.larics.jetshop.model.JetModel;
import org.larics.jetshop.model.JetOrder;
import org.larics.jetshop.model.Requirement;
import org.larics.jetshop.model.User;
import org.larics.jetshop.service.JetModelService;
import org.larics.jetshop.service.JetOrderService;
import org.larics.jetshop.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/*
 * Provides jet orders related views.
 * 
 * @author Igor Laryukhin
 */
@Controller
@RequestMapping("/jetOrders")
public class JetOrderController {

	private static final Logger logger = LoggerFactory.
			getLogger(JetOrderController.class);

	private static final String CODE_JETORDERS_NOT_FOUND =
			"jetOrders.not.found";
	private static final String CODE_JETORDER_NOT_FOUND = "jetOrder.not.found";
	private static final String CODE_DUPLICATE_JETORDER_LINENUMBER = 
			"duplicate.jetOrder.lineNumber";
	private static final String CODE_JETORDER_DELETED = "jetOrder.deleted";
	private static final String CODE_JETORDER_NOT_FOUND_CANNOT_DELETE = 
			"jetOrder.not.found.cannnot.delete";
	private static final String CODE_JETMODEL_NOT_FOUND_ORDER_ANOTHER =
			"jetModel.not.found.order.another";
	private static final String CODE_JETMODELS_NOT_AVAILABLE =
			"jetModel.not.available";

	private final JetOrderService jetOrderService;
	private final UserService userService;
	private final JetModelService jetModelService;
	private final MessageSource messageSource;

	@Autowired
	public JetOrderController(
			JetOrderService jetOrderService,
			UserService userService,
			JetModelService jetModelService,
			MessageSource messageSource) {

		this.jetOrderService = jetOrderService;
		this.userService = userService;
		this.jetModelService = jetModelService;
		this.messageSource = messageSource;
	}

	/*
	 * Returns list of all orders.
	 */
	@GetMapping
	public String getAll(Model model,
			@ModelAttribute("messages") JetShopMessages messages,
			Locale locale) {

		List<JetOrder> jetOrders = jetOrderService.getAll();

		if (jetOrders.isEmpty()) {

			messages.addErrorMessage(
					messageSource.getMessage(CODE_JETORDERS_NOT_FOUND,
					null, locale));
			logger.error("No JetOrders found");

		} else {
			model.addAttribute("jetOrders", jetOrders);
			logger.debug("{} JetOrders found", jetOrders.size());
		}

		return "jetOrders/list";
	}

	/*
	 * Returns one order for viewing.
	 */
	@GetMapping("/{id}")
	public String getOne(@PathVariable Long id,
			Model model,
			RedirectAttributes attributes,
			@ModelAttribute("messages") JetShopMessages messages,
			Locale locale) {

		JetOrder jetOrder = getJetOrder(id, model, attributes, messages,
				locale);
		if (jetOrder == null) {
			return "redirect:/jetOrders";
		}

		return "jetOrders/view";
	}

	/*
	 * Returns create order form.
	 * 
	 * @param jetModelId
	 * 			Id of JetModel, not null if it was ordered
	 * 			on jetModels/view screen
	 */
	@GetMapping("/create")
	public String getNew(
			@RequestParam(value = "jetModelId", required = false)
				Long jetModelId,
			Model model,
			RedirectAttributes attributes,
			@ModelAttribute("messages") JetShopMessages messages,
			Locale locale) {

		JetOrder jetOrder = new JetOrder();
		
		if (jetModelId != null) {

			JetModel jetModel = jetModelService.getById(jetModelId);

			if (jetModel == null) {
				messages.addErrorMessage(
						messageSource.getMessage(
						CODE_JETMODEL_NOT_FOUND_ORDER_ANOTHER,
						null, locale));
				logger.error("JetModel with id={} not found", jetModelId);

			} else {
				logger.debug("JetModel found: {}", jetModel);
				jetOrder.setJetModel(jetModel);
			}
		}

		boolean isModelReady =
				addModelAttributes(model, attributes, messages, locale);
		if (!isModelReady) {
			return "redirect:/jetOrders";
		}

		model.addAttribute("jetOrder", jetOrder );

		return "jetOrders/edit";
	}

	/*
	 * Returns one order for editing.
	 */
	@GetMapping("/edit/{id}")
	public String getUpdatable(@PathVariable Long id,
			Model model,
			RedirectAttributes attributes,
			@ModelAttribute("messages") JetShopMessages messages,
			Locale locale) {

		JetOrder jetOrder = getJetOrder(id, model, attributes, messages,
				locale);
		if (jetOrder == null) {
			return "redirect:/jetOrders";
		}

		boolean isModelReady =
			addModelAttributes(model, attributes, messages, locale);
		if (!isModelReady) {
			return "redirect:/jetOrders";
		}

		model.addAttribute("jetOrder", jetOrder );

		return "jetOrders/edit";
	}

	/*
	 * Adds new Requirement to JetOrder and returns back 
	 * to order creating/editing.
	 */
	// TODO: restrictions?
	@PostMapping(params = {"add"})
	public String addRequirement(@ModelAttribute JetOrder jetOrder,
			Model model) {

		jetOrder.addRequirement(new Requirement());
		logger.debug("New Requirement added");

		model.addAttribute("jetOrder", jetOrder);
		addModelAttributes(model, null, null, null);

		return "jetOrders/edit";
	}

	/*
	 * Deletes specified Requirement from JetOrder and returns back 
	 * to order creating/editing.
	 */
	// TODO: restrictions?
	@PostMapping(params = {"remove"})
	public String removeRequirement(@ModelAttribute JetOrder jetOrder, 
			@RequestParam("remove") String remove,
			Model model) {

		int rowId = Integer.valueOf(remove);
		Requirement requirement = jetOrder.getRequirements().get(rowId);
		jetOrder.removeRequirement(requirement);
		logger.debug("Requirement removed: {}", requirement);

		model.addAttribute("jetOrder", jetOrder);
		addModelAttributes(model, null, null, null);

		return "jetOrders/edit";
	}

	/*
	 * Saves created/updated order.
	 */
	@PreAuthorize("@currentUserService.canAccessUser(principal, #jetOrder.customer.id)")
	@PostMapping(params = {"save"})
	public String save(@Valid JetOrder jetOrder, BindingResult result,
			Model model,
			RedirectAttributes attributes,
			@ModelAttribute("messages") JetShopMessages messages,
			Locale locale) {

		// TODO: test (move below errors?)
		for (Requirement requirement : jetOrder.getRequirements()) {
			requirement.setJetOrder(jetOrder);
		}

		// Line number must be unique
		Long existingId = jetOrderService
				.getIdByLineNumber(jetOrder.getLineNumber());
		if (existingId != null &&
			// New order got line number of existing order or ...
			(jetOrder.getId() == null ||
			// ... existing order got line number of another existing order.
			jetOrder.getId() != null && jetOrder.getId() != existingId)) {

			result.rejectValue("lineNumber",
					CODE_DUPLICATE_JETORDER_LINENUMBER);
			logger.error("JetOrder with lineNumber={} already exists,"
					+ " jetOrder.id={}", jetOrder.getLineNumber(), existingId);
		}

		if (result.hasErrors()) {
			logger.debug("Validation errors: {}", result.getAllErrors());
			// No checking here, assuming model was checked initially
			// in getNew or getUpdatable.
			addModelAttributes(model, attributes, messages, locale);

			return "jetOrders/edit";
		}

		Long id = jetOrderService.save(jetOrder);
		logger.debug("JetOrder saved, id={}", id);

		return "redirect:/jetOrders/" + id;
	}

	/*
	 * Deletes the order.
	 */
	@PreAuthorize("hasAuthority('ADMIN')")
	@GetMapping("/delete/{id}")
	public String delete(@PathVariable Long id,
			RedirectAttributes attributes,
			@ModelAttribute("messages") JetShopMessages messages,
			Locale locale) {

		Long count = jetOrderService.countById(id);
		logger.debug("{} JetOrders found by id={}", count, id);

		if (count < 1) {
			messages.addErrorMessage(
					messageSource.getMessage(
					CODE_JETORDER_NOT_FOUND_CANNOT_DELETE,
					null, locale));
			// TODO: ... or access denied?
			logger.error("JetOrder not found, cannot be deleted");

		} else {
			jetOrderService.delete(id);
			logger.debug("JetOrder with id={} deleted", id);
			messages.addInfoMessage(
					messageSource.getMessage(CODE_JETORDER_DELETED,
					null, locale));
		}

		attributes.addFlashAttribute("messages", messages);

		return "redirect:/jetOrders";
	}

	/*
	 * Helper methods.
	 */

	/*
	 * Retrieves the order.
	 */
	private JetOrder getJetOrder(Long id,
			Model model,
			RedirectAttributes attributes,
			JetShopMessages messages,
			Locale locale) {

		JetOrder jetOrder = jetOrderService.getById(id);

		if (jetOrder == null) {

			messages.addErrorMessage(
					messageSource.getMessage(CODE_JETORDER_NOT_FOUND,
					null, locale));
			// TODO: ... or access denied?
			logger.error("JetOrder not found");

			attributes.addFlashAttribute("messages", messages);

		} else {
			model.addAttribute("jetOrder", jetOrder);
			logger.debug("JetOrder found: {}", jetOrder);
		}

		return jetOrder;
	}

	/*
	 * Adds Model attributes needed for create/edit views:
	 * customers and jet models.
	 * 
	 * Notes:
	 * 1) The main disadvantage of this approach is repeated retrieving of the 
	 * lists from the DB each time when the user adds/removes requirement row.
	 * In real system this will be probably fixed by AJAX.
	 * 
	 * 2) Using @ModelAttribute method doesn't give the best fit as not all the
	 * methods need these attributes.
	 * Moreover, even for the same method, "save", the attributes needed only
	 * in case of validation errors.
	 * 
	 * 3) @SessionAttributes are hard to maintain as there is no direct time
	 * point to clear them (e.g., user can cancel editing by navigating to
	 * another page/site).
	 * 
	 * 4) Using AOP seems to have much overhead for such a small problem within
	 * one controller.
	 * 
	 * @return true, if data added successfully,
	 * 			false, if some data missing
	 */
	private boolean addModelAttributes(Model model,
			RedirectAttributes attributes,
			JetShopMessages messages,
			Locale locale) {

		List<JetModel> jetModels = jetModelService.getAll();
		model.addAttribute("jetModels", jetModels);

		if (jetModels.isEmpty()) {
			// It's ok for single-user system.
			if (attributes != null && messages != null && locale != null) {
				messages.addErrorMessage(
						messageSource.getMessage(CODE_JETMODELS_NOT_AVAILABLE,
						null, locale));
	
				attributes.addFlashAttribute("messages", messages);
			}
			logger.error("No JetModels found");

			return false;
		}

		// No need to check customers as method restricted
		// to authenticated users only.
		List<User> customers = userService.getCustomers();
		model.addAttribute("customers", customers);

		return true;
	}

}
