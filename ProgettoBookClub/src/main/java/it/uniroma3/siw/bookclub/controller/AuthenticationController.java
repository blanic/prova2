package it.uniroma3.siw.bookclub.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import it.uniroma3.siw.bookclub.controller.validator.CredentialsValidator;
import it.uniroma3.siw.bookclub.controller.validator.UserValidator;
import it.uniroma3.siw.bookclub.model.Credentials;
import it.uniroma3.siw.bookclub.model.User;
import it.uniroma3.siw.bookclub.service.CredentialsService;
import it.uniroma3.siw.bookclub.service.UserService;

@Controller
public class AuthenticationController {

	@Autowired
	private CredentialsService credentialsService;

	@Autowired
	private UserService userService;

	@Autowired
	private UserValidator userValidator;

	@Autowired
	private CredentialsValidator credentialsValidator;

	@RequestMapping(value = "/register", method = RequestMethod.GET) 
	public String showRegisterForm (Model model) {
		model.addAttribute("user", new User());
		model.addAttribute("credentials", new Credentials());
		return "registrationForm";
	}

	@RequestMapping(value = "/login", method = RequestMethod.GET) 
	public String showLoginForm (Model model) {
		return "loginForm";
	}

	@RequestMapping(value = "/logout", method = RequestMethod.GET) 
	public String logout(Model model) {
		return "index";
	}

	@RequestMapping(value = "/default", method = RequestMethod.GET)
	public String defaultAfterLogin(Model model) {

		UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
		if (credentials.getRole().equals(Credentials.ADMIN_ROLE)) {
			return "admin/home";
		}
		return "user/home";
	}

	@RequestMapping(value = { "/register" }, method = RequestMethod.POST)
	public String registerUser(@Valid @ModelAttribute("user") User user,
			BindingResult userBindingResult,
			@Valid @ModelAttribute("credentials") Credentials credentials,
			BindingResult credentialsBindingResult,
			Model model) {

		// validate user and credentials fields
		this.userValidator.validate(user, userBindingResult);
		this.credentialsValidator.validate(credentials, credentialsBindingResult);

		// if neither of them had invalid contents, store the User and the Credentials into the DB
		if(!userBindingResult.hasErrors() && ! credentialsBindingResult.hasErrors()) {
			// set the user and store the credentials;
			// this also stores the User, thanks to Cascade.ALL policy
			credentials.setUser(user);
			credentialsService.saveCredentials(credentials);
			return "successfulRegistration";
		}
		return "registrationForm";
	}

	@RequestMapping(value = "/admin/register", method = RequestMethod.GET) 
	public String toRegisterAdmin (Model model) {
		List<User> utenti = this.userService.getAllUsers();
		List<User> amministratori = new ArrayList<>();
		for(User utente : utenti) {
			if(utente.getCredentials().getRole() == Credentials.ADMIN_ROLE) {
				amministratori.add(utente);
			}	
		}

		utenti.removeAll(amministratori);
		model.addAttribute("amministratori", amministratori);
		model.addAttribute("utenti", utenti);
		return "admin/toSetAdmin";
	}

	@RequestMapping(value = "/admin/setAdmin/{id}", method = RequestMethod.GET) 
	public String registerAdmin(@PathVariable("id") Long id, Model model) {
		User utente = this.userService.findById(id);
		Credentials credentials = utente.getCredentials();
		credentials.setRole(Credentials.ADMIN_ROLE);
		this.credentialsService.saveCredentials(credentials);
		return "admin/home";
	}



}
