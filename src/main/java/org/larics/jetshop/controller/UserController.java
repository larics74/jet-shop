package org.larics.jetshop.controller;

import java.util.List;
import java.util.Locale;

import javax.validation.Valid;

import org.larics.jetshop.controller.helper.JetShopMessages;
import org.larics.jetshop.model.CurrentUser;
import org.larics.jetshop.model.Role;
import org.larics.jetshop.model.User;
import org.larics.jetshop.service.JetOrderService;
import org.larics.jetshop.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/*
 * Provides users related views.
 * 
 * @author Igor Laryukhin
 */
@Controller
public class UserController {

	private static final Logger logger = LoggerFactory.
			getLogger(UserController.class);

	// Message code in messages.properties.
	public static final String CODE_SIZE_USER_NEWPASSWORD =
			"size.user.newPassword";
	// Default message for validation error if there is no message in
	// messages.properties.
	public static final String MESSAGE_SIZE_USER_NEWPASSWORD =
			"The password must be at least " + User.PASSWORD_MIN_SIZE +
			" characters";

	public static final String CODE_MISMATCH_USER_PASSWORDREPEATED =
			"mismatch.user.passwordRepeated";
	public static final String MESSAGE_MISMATCH_USER_PASSWORDREPEATED =
			"The password doesn't match";

	public static final String CODE_DUPLICATE_USER_NAME = "duplicate.user.name";
	public static final String MESSAGE_DUPLICATE_USER_NAME =
			"The user with the provided name already exists";

	private static final String TITLE_USER_DETAILS = "title.user.details";
	private static final String TITLE_REGISTER_FORM = "title.register";

	private static final String CODE_USER_REGISTERED = "user.registered";
	private static final String CODE_USER_IS_CUSTOMER = "user.is.customer";
	private static final String CODE_USER_NOT_FOUND = "user.not.found";
	private static final String CODE_USERS_NOT_FOUND = "users.not.found";
	private static final String CODE_USER_DELETED = "user.deleted";
	private static final String CODE_USER_SAVED = "user.saved";
	private static final String CODE_USER_NOT_FOUND_CANNOT_DELETE =
			"user.not.found.cannot.delete";

	private static enum Action {REGISTER, SAVE};

	// TODO: move to service?
	private static final BCryptPasswordEncoder encoder =
			new BCryptPasswordEncoder();

	private final UserService userService;
	private final JetOrderService jetOrderService;
	private final MessageSource messageSource;

	@Autowired
	public UserController(
			UserService userService,
			JetOrderService jetOrderService,
			MessageSource messageSource) {

		this.userService = userService;
		this.jetOrderService = jetOrderService;
		this.messageSource = messageSource;
	}

	/*
	 * Returns customer register form.
	 */
	// Not authenticated only - set in SecurityConfig
	@GetMapping("/register")
	public String getRegister(Model model) {

		User user = new User();
		model.addAttribute("user", user);
		addModelAttributes(Action.REGISTER, model);

		return "users/edit";
	}

	/*
	 * Registers (saves) the customer.
	 */
	// Not authenticated only - set in SecurityConfig
	@PostMapping("/register")
	public String register(@Valid User user, BindingResult result,
			Model model,
			RedirectAttributes attributes,
			@ModelAttribute("messages") JetShopMessages messages,
			Locale locale) {

		validateAndSave(Action.REGISTER,
				user, result, model, attributes, messages, locale);
		if (result.hasErrors()) {
			return "users/edit";
		}
		
		return "redirect:/login";
	}

	/*
	 * Returns list of all users.
	 */
	@PreAuthorize("hasAuthority('ADMIN')")
	@GetMapping("/users")
	public String getAll(Model model,
			@ModelAttribute("messages") JetShopMessages messages,
			Locale locale) {

		getUsers(model, messages, locale);
		return "users/list";
	}

	/*
	 * Returns one user for viewing.
	 */
	@PreAuthorize("@currentUserService.canAccessUser(principal, #id)")
	@GetMapping("/users/{id}")
	public String getOne(@PathVariable Long id,
			Model model,
			RedirectAttributes attributes,
			@ModelAttribute("messages") JetShopMessages messages,
			Locale locale) {

		User user = getUser(id, model, attributes, messages, locale);
		if (user == null) {
			return "redirect:/users";
		}

		return "users/view";
	}

	/*
	 * Returns create user form.
	 */
	@PreAuthorize("hasAuthority('ADMIN')")
	@GetMapping("/users/create")
	public String getNew(Model model) {

		model.addAttribute("user", new User());
		addModelAttributes(Action.SAVE, model);

		return "users/edit";
	}

	/*
	 * Returns one user for editing.
	 */
	@PreAuthorize("@currentUserService.canAccessUser(principal, #id)")
	@GetMapping("/users/edit/{id}")
	public String getUpdatable(@PathVariable Long id,
			Model model,
			RedirectAttributes attributes,
			@ModelAttribute("messages") JetShopMessages messages,
			Locale locale) {

		User user = getUser(id, model, attributes, messages, locale);
		if (user == null) {
			return "redirect:/users";
		}

		// Switches help messages on
		model.addAttribute("help", true);

		addModelAttributes(Action.SAVE, model);
		return "users/edit";
	}

	/*
	 * Saves registered/created/updated user.
	 */
	@PreAuthorize("@currentUserService.canAccessUser(principal, #user.id)")
	@PostMapping("/users")
	public String save(@Valid User user, BindingResult result,
			Model model,
			RedirectAttributes attributes,
			@ModelAttribute("messages") JetShopMessages messages,
			Locale locale) {

		// Must be Long (not long)
		// as the method returns null in case of validation errors.
		Long id = validateAndSave(Action.SAVE,
				user, result, model, attributes, messages, locale);

		if (result.hasErrors()) {
			return "users/edit";
		}
		return "redirect:/users/" + id;
	}

	/*
	 * Deletes the user.
	 */
	@PreAuthorize("hasAuthority('ADMIN')")
	@GetMapping("/users/delete/{id}")
	public String delete(@PathVariable Long id,
			RedirectAttributes attributes,
			@ModelAttribute("messages") JetShopMessages messages,
			Locale locale) {

		Long count = userService.countById(id);
		logger.debug("{} Users found by id={}", count, id);

		// TODO:
		// Looks more like business logic than validation
		// but at the same time provides i18n messages for UI.
		// Question: should it be moved to service or split between
		// controller and service? Or something else?
		if (count < 1) {
			messages.addErrorMessage(
					messageSource.getMessage(
					CODE_USER_NOT_FOUND_CANNOT_DELETE,
					null, locale));
			logger.error("User not found, cannot be deleted");

		} else {
			count = jetOrderService.countByCustomerId(id);
			logger.debug("Found {} JetOrders for User.id={}", count, id);

			if (count > 0) {
				messages.addErrorMessage(
						messageSource.getMessage(CODE_USER_IS_CUSTOMER,
						null, locale));
				logger.error("The user is a customer and has orders,"
						+ " cannot be deleted");

			} else {
				userService.delete(id);
					messages.addInfoMessage(
							messageSource.getMessage(CODE_USER_DELETED,
							null, locale));
					logger.debug("User with id={} deleted", id);
			}
		}

		attributes.addFlashAttribute("messages", messages);

		return "redirect:/users";
	}

	/*
	 * Helper methods.
	 */

	/*
	 * Retrieves the user.
	 */
	private User getUser(Long id,
			Model model,
			RedirectAttributes attributes,
			JetShopMessages messages,
			Locale locale) {

		User user = userService.getById(id);

		if (user == null) {

			messages.addErrorMessage(
					messageSource.getMessage(CODE_USER_NOT_FOUND,
					null, locale));
			logger.error("User with id={} not found", id);

			attributes.addFlashAttribute("messages", messages);

		} else {
			model.addAttribute("user", user);
			logger.debug("User found: {}", user);
		}

		return user;
	}

	/*
	 * Retrieves all users.
	 */
	private void getUsers(Model model,
			JetShopMessages messages,
			Locale locale) {

		List<User> users = userService.getAll();

		if (users.isEmpty()) {

			messages.addErrorMessage(
					messageSource.getMessage(CODE_USERS_NOT_FOUND,
					null, locale));
			logger.error("No Users found");

		} else {
			model.addAttribute("users", users);
			logger.debug("{} Users found", users.size());
		}
	}

	/*
	 * Adds model attributes for register/create/edit user form.
	 */
	private void addModelAttributes(Action action, Model model) {

		switch (action) {
		case REGISTER :

			model.addAttribute("action", "/register");
			model.addAttribute("headTitle", TITLE_REGISTER_FORM);
			model.addAttribute("roles", new Role[] {Role.CUSTOMER});

			break;
		case SAVE :

			model.addAttribute("action", "/users");
			model.addAttribute("headTitle", TITLE_USER_DETAILS);

			Authentication authentication =
					SecurityContextHolder.getContext().getAuthentication();
			// No ClassCastException catcher provided as method is accessible
			// only by authenticated users.
			CurrentUser currentUser = 
					(CurrentUser) authentication.getPrincipal();
			logger.debug("CurrentUser: {}", currentUser);

			Role role = currentUser.getRole();
			if (role == Role.ADMIN) {
				// Admin can edit any user.
				model.addAttribute("roles", Role.values());
			} else {
				// Non-admin can edit only his own data.
				model.addAttribute("roles", new Role[] {role});
			}

			break;
		}
	}

	/*
	 * Validates and saves registered/created/updated user.
	 */
	private Long validateAndSave(Action action,
			User user,
			BindingResult result,
			Model model,
			RedirectAttributes attributes,
			JetShopMessages messages,
			Locale locale) {

		// User name must be unique.
		Long existingId = userService.getIdByName(user.getName());
		if (existingId != null &&
			// New user got name of existing user or ...
			(user.getId() == null ||
			// ... existing user got name of another existing user.
			user.getId() != null && user.getId() != existingId)) {

			result.rejectValue("name",
					CODE_DUPLICATE_USER_NAME,
					// TODO: remove
					MESSAGE_DUPLICATE_USER_NAME);
			logger.error("User with name='{}' already exists, user id={}",
					user.getName(), existingId);
		}

		// Note: view form saves password field as a hidden parameter.
		// Probably, it would be better to remove it from view 
		// and get it from service level. 
		String newPassword = user.getNewPassword();

		// Note: though, newPassword and repeatedPassword are returned as empty 
		// strings if they had been nulls initially,
		// I didn't remove null checking below.

		// Password must be valid.
		if (
			// New user got empty/invalid password or ...
			user.getId() == null &&
				(newPassword == null ||
				newPassword.length() < User.PASSWORD_MIN_SIZE) ||
			// ... password was updated for existing user
			// but this password is invalid.
			// Empty password string is interpreted as if user haven't updated 
			// password (but passwordRepeated is also checked).
			user.getId() != null &&
				newPassword != null &&
				!newPassword.isEmpty() &&
				newPassword.length() < User.PASSWORD_MIN_SIZE) {

			result.rejectValue("newPassword",
					CODE_SIZE_USER_NEWPASSWORD,
					new Object[] {User.PASSWORD_MIN_SIZE},
					MESSAGE_SIZE_USER_NEWPASSWORD);
			logger.error("Password is not valid");
		}

		// Password must be equal to repeated password.
		if (newPassword != null &&
				!newPassword.equals(user.getPasswordRepeated())) {

			result.rejectValue("passwordRepeated",
					CODE_MISMATCH_USER_PASSWORDREPEATED,
					MESSAGE_MISMATCH_USER_PASSWORDREPEATED);
			logger.error("Password doesn't match");
		}

		if (result.hasErrors()) {
			if (user.getId() != null) {
				// Switches help messages on
				model.addAttribute("help", true);
			}

			logger.debug("Validation errors: {}", result.getAllErrors());
			addModelAttributes(action, model);
			return null;
		}

		// TODO: move to service?
		if (!newPassword.isEmpty()) {
			// Save new password or
			// update, if it has been updated.
			user.setPassword(encoder.encode(newPassword));
		}
		Long id = userService.save(user);
		logger.debug("User saved, username='{}'", user.getName());
		logger.debug("User saved, id='{}'", id);

		if (action == Action.REGISTER) {
			messages.addInfoMessage(
					messageSource.getMessage(CODE_USER_REGISTERED,
					null, locale));
		} else {

			// User saved after creating/editing.
			messages.addInfoMessage(
					messageSource.getMessage(CODE_USER_SAVED,
					null, locale));
		}

		attributes.addFlashAttribute("messages", messages);

		return id;
	}

}
