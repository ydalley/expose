package com.expose.web;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.expose.ApplicationException;
import com.expose.model.RegistrationInfo;
import com.expose.service.RegistrationService;

@Controller
@RequestMapping("/")
public class MainWebController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	RegistrationService regService;

	@GetMapping
	String getDashboard() {
		return "index";
	}

	@GetMapping("/index")
	String getpage2() {
		return "index";
	}

	@GetMapping("/register/{regKey}")
	String register(@PathVariable String regKey,RedirectAttributes redirectAttributes, Model model) {
		String response = "";
		try {
			response = regService.register(regKey);
		} catch (ApplicationException e) {
			logger.warn("Error registering ", regKey, e);
			model.addAttribute("error", e.getMessage());
			return "login";
		}
		redirectAttributes.addFlashAttribute("message", response);
		return "redirect:/login";
	}

	
	@GetMapping("/signup")
	String signUp(Model model){
		RegistrationInfo info = new RegistrationInfo();
		model.addAttribute("info",info);
		return "register";
	}
	
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	String newRegistration(@ModelAttribute("info") @Valid RegistrationInfo info, BindingResult result,RedirectAttributes redirectAttributes,  Model model) {
		if (result.hasErrors()) {
			logger.warn("Error occurred creating RegistrationInfo{}", result.toString());
			return "register";

		}
		String response = "";
		try {
			response = regService.register(info);
		} catch (ApplicationException e) {
			logger.warn("Error registering ", info, e);
			result.reject(e.getMessage());
			// model.addAttribute("error", e.getMessage());
		}
		redirectAttributes.addFlashAttribute("message", response);
		return "redirect:/login";
	}

	@RequestMapping("/login")
	public String login() {
		return "login";
	}

}
