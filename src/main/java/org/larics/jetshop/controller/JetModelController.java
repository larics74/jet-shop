package org.larics.jetshop.controller;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import javax.validation.Valid;

import org.larics.jetshop.controller.helper.JetShopMessages;
import org.larics.jetshop.model.JetModel;
import org.larics.jetshop.service.JetModelService;
import org.larics.jetshop.service.JetOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/*
 * Provides jet models related views.
 * 
 * @author Igor Laryukhin
 */
@Controller
@RequestMapping("/jetModels")
public class JetModelController {

	private static final Logger logger = LoggerFactory.
			getLogger(JetModelController.class);

	private static final String CODE_JETMODELS_NOT_FOUND =
			"jetModels.not.found";
	private static final String CODE_JETMODEL_NOT_FOUND = "jetModel.not.found";
	private static final String CODE_NO_DRAWING = "jetModel.no.drawing";
	private static final String MESSAGE_NO_DRAWING =
			"No or empty drawing has been provided";
	private static final String CODE_DUPLICATE_JETMODEL_NAME =
			"duplicate.jetModel.name";
	private static final String MESSAGE_DUPLICATE_JETMODEL_NAME =
			"The Jet model with the provided name already exists";
	private static final String CODE_JETMODEL_HAS_JETORDERS =
			"jetModel.has.orders";
	private static final String CODE_JETMODEL_DELETED = "jetModel.deleted";

	private static final String CODE_JETMODEL_NOT_FOUND_CANNOT_DELETE =
			"jetModel.not.found.cannot.delete";

	private final JetModelService jetModelService;
	private final JetOrderService jetOrderService;
	private final MessageSource messageSource;

	@Autowired
	public JetModelController(JetModelService jetModelService,
			JetOrderService jetOrderService,
			MessageSource messageSource) {

		this.jetModelService = jetModelService;
		this.jetOrderService = jetOrderService;
		this.messageSource = messageSource;
	}

	/*
	 * Returns list of all models.
	 */
	@GetMapping
	public String getAll(Model model,
			@ModelAttribute("messages") JetShopMessages messages,
			Locale locale) {

		getJetModels(model, messages, locale);
		return "jetModels/list";
	}

	/*
	 * Returns one model for viewing.
	 */
	@GetMapping("/{id}")
	public String getOne(@PathVariable Long id,
			Model model,
			RedirectAttributes attributes,
			@ModelAttribute("messages") JetShopMessages messages,
			Locale locale) {

		JetModel jetModel = getJetModel(id, model, attributes, messages,
				locale);
		if (jetModel == null) {
			return "redirect:/jetmodels";
		}

		return "jetModels/view";
	}

	/*
	 * Returns model drawing.
	 * Note, this method is called automatically
	 * when user views/edits a model.
	 */
	// TODO: If user manually sends this request,
	// the system shows bytes. Probably, there is a way to restrict it.
	@GetMapping(value = "/drawings/{id}")
	public ResponseEntity<byte[]> getDrawing(
			@PathVariable("id") Long id,
			Model model,
			RedirectAttributes attributes,
			@ModelAttribute("messages") JetShopMessages messages,
			Locale locale) {

		JetModel jetModel = getJetModel(id, model, attributes, messages,
				locale);
		assert jetModel != null; // drawing cannot be null and the method
									// is called for existing jetModel

		HttpHeaders headers = new HttpHeaders();
		headers.setCacheControl(CacheControl.noCache().getHeaderValue());

		ResponseEntity<byte[]> responseEntity =
				new ResponseEntity<>(
						jetModel.getDrawing(), headers, HttpStatus.OK);

		return responseEntity;
	}

	/*
	 * Returns create model form.
	 */
	@PreAuthorize("hasAuthority('ADMIN')")
	@GetMapping("/create")
	public String getNew(Model model) {

		model.addAttribute("jetModel", new JetModel());

		return "jetModels/edit";
	}

	/*
	 * Returns one model for editing.
	 */
	@PreAuthorize("hasAuthority('ADMIN')")
	@GetMapping("/edit/{id}")
	public String getUpdatable(@PathVariable Long id,
			Model model,
			RedirectAttributes attributes,
			@ModelAttribute("messages") JetShopMessages messages,
			Locale locale) {

		JetModel jetModel = getJetModel(id, model, attributes, messages,
				locale);
		if (jetModel == null) {
			return "redirect:/jetModels";
		}

		// Switches help messages on
		model.addAttribute("help", true);

		return "jetModels/edit";
	}

	/*
	 * Saves created/updated model.
	 */
	@PreAuthorize("hasAuthority('ADMIN')")
	@PostMapping()
	public String save(@Valid JetModel jetModel, BindingResult result,
			@RequestParam("file") MultipartFile file,
			Model model,
			@ModelAttribute("messages") JetShopMessages messages,
			Locale locale) throws IOException {

		// TODO: If file was deleted/renamed after it was selected
		// then browser shows error before we even get to this method
		// and there is no exception.
		// Haven't got any idea how to handle it yet.

		// New model must have drawing.
		if (jetModel.getId() == null && file.isEmpty()) {
			result.rejectValue("drawing", CODE_NO_DRAWING,
					MESSAGE_NO_DRAWING);
			logger.error("No or empty drawing has been provided");
		}

		// Model name must be unique.
		Long existingId = jetModelService.getIdByName(jetModel.getName());
		if (existingId != null &&
				// New model contains existing model name or ...
				((jetModel.getId() == null) || 
				// ... existing model name renamed
				// to another existing model name.
				jetModel.getId() != existingId)) {

			result.rejectValue("name", CODE_DUPLICATE_JETMODEL_NAME,
					MESSAGE_DUPLICATE_JETMODEL_NAME);
			logger.error("JetModel with name='{}' already exists, model id={}",
					jetModel.getName(), existingId);
		}

		// Notes:
		// Html input type="file" value is lost in case of validation errors,
		// so, the user have to select file again.
		// Possible workarounds (not used here): 
		// 1) upload file (if specified) to the temporary folder
		// and then (after successful validation) move it to the final folder.
		// 2) create separate form for upload
		// 3) make form a "wizard" and move upload to the 2nd step
		if (result.hasErrors()) {
			logger.debug("Validation errors: {}", result.getAllErrors());

			if (jetModel.getId() != null) {
				// Switches help messages on
				model.addAttribute("help", true);
			}

			return "jetModels/edit";
		}

		if (!file.isEmpty()) {
			jetModel.setDrawing(file.getBytes());
		}

		Long id = jetModelService.save(jetModel);
		logger.debug("JetModel saved, name='{}'", jetModel.getName());

		return "redirect:/jetModels/" + id;
	}

	/*
	 * Deletes the model.
	 */
	@PreAuthorize("hasAuthority('ADMIN')")
	@GetMapping(value = "/delete/{id}")
	public String delete(@PathVariable Long id,
			RedirectAttributes attributes,
			@ModelAttribute("messages") JetShopMessages messages,
			Locale locale) {

		Long count = jetModelService.countById(id);
		logger.debug("{} JetModels found by id={}", count, id);

		if (count < 1) {
			messages.addErrorMessage(
					messageSource.getMessage(
					CODE_JETMODEL_NOT_FOUND_CANNOT_DELETE,
					null, locale));
			logger.error("JetModel not found, cannot be deleted");

		} else {
			count = jetOrderService.countByJetModelId(id);
			logger.debug("Found {} JetOrders with JetModel.id={}", count, id);

			if (count > 0) {
				messages.addErrorMessage(
						messageSource.getMessage(CODE_JETMODEL_HAS_JETORDERS,
						null, locale));
				logger.error("Some orders contain this model,"
						+ " the model cannot be deleted");

			} else {
				jetModelService.delete(id);
				messages.addInfoMessage(
						messageSource.getMessage(CODE_JETMODEL_DELETED,
						null, locale));
				logger.debug("JetModel with id={} deleted", id);
			}
		}

		attributes.addFlashAttribute("messages", messages);

		return "redirect:/jetModels";
	}

	/*
	 * Helper methods.
	 */

	/*
	 * Retrieves the model.
	 */
	private JetModel getJetModel(Long id,
			Model model,
			RedirectAttributes attributes,
			JetShopMessages messages,
			Locale locale) {

		JetModel jetModel = jetModelService.getById(id);

		if (jetModel == null) {

			messages.addErrorMessage(
					messageSource.getMessage(CODE_JETMODEL_NOT_FOUND,
					null, locale));
			logger.error("JetModel with id={} not found", id);

			attributes.addFlashAttribute("messages", messages);

		} else {
			model.addAttribute("jetModel", jetModel);
			logger.debug("JetModel found: {}", jetModel);
		}

		return jetModel;
	}

	/*
	 * Retrieves all models.
	 */
	private void getJetModels(Model model,
			JetShopMessages messages,
			Locale locale) {

		List<JetModel> jetModels = jetModelService.getAll();

		if (jetModels.isEmpty()) {

			messages.addErrorMessage(
					messageSource.getMessage(CODE_JETMODELS_NOT_FOUND,
					null, locale));
			logger.error("No JetModels found");

		} else {
			model.addAttribute("jetModels", jetModels);
			logger.debug("{} JetModels found", jetModels.size());
		}
	}

}
